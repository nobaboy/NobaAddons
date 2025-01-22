package me.nobaboy.nobaaddons.config.utils

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionEventListener
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import kotlin.jvm.optionals.getOrNull

fun interface OptionCondition {
	fun validate(): Boolean
	fun listenTo(): Set<Option<*>> = emptySet()
}

private class WrappingCondition(private val wrapping: Collection<OptionCondition>, private val validate: OptionCondition) : OptionCondition {
	override fun validate(): Boolean = validate.validate()
	override fun listenTo(): Set<Option<*>> = wrapping.flatMap { it.listenTo() }.toSet()
}

fun any(vararg conditions: OptionCondition): OptionCondition =
	WrappingCondition(conditions.toList()) { conditions.any { it.validate() } }

fun all(vararg conditions: OptionCondition): OptionCondition =
	WrappingCondition(conditions.toList()) { conditions.all { it.validate() } }

fun none(vararg conditions: OptionCondition): OptionCondition =
	WrappingCondition(conditions.toList()) { conditions.none { it.validate() } }

fun config(option: Option<Boolean>, invert: Boolean = false) = config(option, invert) { it }

fun <T> config(option: Option<T>, invert: Boolean = false, condition: (T) -> Boolean) = object : OptionCondition {
	override fun validate() = if(invert) !condition(option.pendingValue()) else condition(option.pendingValue())
	override fun listenTo(): Set<Option<*>> = setOf(option)
}

fun mod(mod: String, minVersion: Version? = null, maxVersion: Version? = null, invert: Boolean = false) = OptionCondition {
	val container = FabricLoader.getInstance().getModContainer(mod).getOrNull() ?: return@OptionCondition false
	val ret = when {
		minVersion != null && minVersion > container.metadata.version -> false
		maxVersion != null && maxVersion < container.metadata.version -> false
		else -> true
	}
	if(invert) !ret else ret
}

infix fun <T> Option<T>.requires(requirement: OptionCondition): Option<T> {
	if(available()) setAvailable(requirement.validate())
	requirement.listenTo().forEach {
		it.addEventListener { _, type ->
			if(type != OptionEventListener.Event.AVAILABILITY_CHANGE) setAvailable(requirement.validate())
		}
	}
	return this
}