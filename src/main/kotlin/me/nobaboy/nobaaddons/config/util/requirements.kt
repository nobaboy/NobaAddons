package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionEventListener
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import kotlin.jvm.optionals.getOrNull

fun interface OptionRequirement {
	val depends: Set<Option<*>> get() = emptySet()
	fun check(): Boolean
}

fun OptionRequirement(options: Set<Option<*>>, requirement: OptionRequirement) = object : OptionRequirement {
	override val depends: Set<Option<*>> = options.plus(requirement.depends)
	override fun check(): Boolean = requirement.check()
}

fun OptionRequirement(option: Option<*>, requirement: OptionRequirement) = OptionRequirement(setOf(option), requirement)

fun OptionRequirement(wrapping: List<OptionRequirement>, requirement: OptionRequirement) = OptionRequirement(
	buildSet { wrapping.plus(requirement).map(OptionRequirement::depends).forEach(::addAll) },
	requirement,
)

object OptionRequirementFactory {
	infix fun OptionRequirement.and(other: OptionRequirement): OptionRequirement = OptionRequirement(listOf(this, other)) {
		this.check() && other.check()
	}

	infix fun OptionRequirement.or(other: OptionRequirement): OptionRequirement = OptionRequirement(listOf(this, other)) {
		this.check() || other.check()
	}

	operator fun OptionRequirement.not(): OptionRequirement = object : OptionRequirement {
		override val depends: Set<Option<*>> by this@not::depends
		override fun check(): Boolean = !this@not.check()
	}

	fun option(option: Option<Boolean>): OptionRequirement = OptionRequirement(option, option::pendingValue)

	fun <T> option(option: Option<T>, mapping: (T) -> Boolean): OptionRequirement = OptionRequirement(option) {
		mapping(option.pendingValue())
	}

	fun mod(modId: String): OptionRequirement = OptionRequirement { FabricLoader.getInstance().isModLoaded(modId) }

	fun mod(modId: String, range: ClosedRange<Version>): OptionRequirement {
		return OptionRequirement {
			val container = FabricLoader.getInstance().getModContainer(modId).getOrNull() ?: return@OptionRequirement false
			container.metadata.version in range
		}
	}

	fun minecraft(range: ClosedRange<Version>): OptionRequirement = mod("minecraft", range)

	fun minecraft(min: String? = null, max: String? = null): OptionRequirement {
		require(min != null || max != null) { "At least one of either min or max must be provided" }
		return mod("minecraft", (min ?: "1.0.0").let(Version::parse)..(max ?: "2.0.0").let(Version::parse))
	}
}

fun <T> Option<T>.require(condition: OptionRequirement): Option<T> = apply {
	if(available()) setAvailable(condition.check())
	condition.depends.forEach {
		it.addEventListener { _, type ->
			if(type != OptionEventListener.Event.AVAILABILITY_CHANGE) setAvailable(condition.check())
		}
	}
}

fun <T> Option<T>.require(builder: OptionRequirementFactory.() -> OptionRequirement): Option<T> =
	require(builder(OptionRequirementFactory))
