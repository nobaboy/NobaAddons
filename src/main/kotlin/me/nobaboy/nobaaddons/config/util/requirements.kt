package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionEventListener
import net.fabricmc.loader.api.FabricLoader

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

object OptionRequirementBuilder {
	infix fun OptionRequirement.and(other: OptionRequirement): OptionRequirement = OptionRequirement(listOf(this, other)) {
		this.check() && other.check()
	}

	infix fun OptionRequirement.or(other: OptionRequirement): OptionRequirement = OptionRequirement(listOf(this, other)) {
		this.check() || other.check()
	}

	fun OptionRequirement.invert(): OptionRequirement = OptionRequirement(depends) { !check() }

	fun option(option: Option<Boolean>): OptionRequirement = OptionRequirement(option, option::pendingValue)

	fun <T> option(option: Option<T>, mapping: (T) -> Boolean): OptionRequirement = OptionRequirement(option) {
		mapping(option.pendingValue())
	}

	fun mod(modId: String): OptionRequirement = OptionRequirement { FabricLoader.getInstance().isModLoaded(modId) }
}

fun <T> Option<T>.require(condition: OptionRequirement): Option<T> = apply {
	if(available()) setAvailable(condition.check())
	condition.depends.forEach {
		it.addEventListener { _, type ->
			if(type != OptionEventListener.Event.AVAILABILITY_CHANGE) setAvailable(condition.check())
		}
	}
}

infix fun <T> Option<T>.require(builder: OptionRequirementBuilder.() -> OptionRequirement): Option<T> =
	require(builder(OptionRequirementBuilder))
