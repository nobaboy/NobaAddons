package me.nobaboy.nobaaddons.config.utils

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionEventListener
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import kotlin.jvm.optionals.getOrNull

fun interface OptionCondition {
	fun validate(): Boolean
	val listenForUpdates: List<Option<*>> get() = emptyList()
}

private class WrappingCondition(
	val conditions: Collection<OptionCondition> = emptyList(),
	val options: Collection<Option<*>> = emptyList(),
	val condition: OptionCondition,
) : OptionCondition {
	override fun validate(): Boolean = condition.validate()

	override val listenForUpdates: List<Option<*>> get() = buildSet {
		addAll(condition.listenForUpdates)
		addAll(options)
		conditions.forEach { addAll(it.listenForUpdates) }
	}.toList()
}

object ConditionBuilder {
	fun not(condition: OptionCondition): OptionCondition = WrappingCondition(listOf(condition)) {
		!condition.validate()
	}

	fun any(vararg conditions: OptionCondition): OptionCondition = WrappingCondition(conditions.toList()) {
		conditions.any(OptionCondition::validate)
	}

	fun all(vararg conditions: OptionCondition): OptionCondition = WrappingCondition(conditions.toList()) {
		conditions.all(OptionCondition::validate)
	}

	fun mod(mod: String, min: Version? = null, max: Version? = null) = OptionCondition {
		val mod = FabricLoader.getInstance().getModContainer(mod).getOrNull()
		when {
			mod == null -> false
			min != null && mod.metadata.version < min -> false
			max != null && mod.metadata.version > max -> false
			else -> true
		}
	}

	fun option(other: Option<Boolean>): OptionCondition = WrappingCondition(options = listOf(other)) {
		other.pendingValue() && other.available()
	}

	fun <T> option(other: Option<T>, mapper: (T) -> Boolean): OptionCondition = WrappingCondition(options = listOf(other)) {
		mapper(other.pendingValue()) && other.available()
	}
}

@Deprecated("Replaced in favor of more functional conditions")
fun configOption(option: Option<Boolean>): OptionCondition =
	ConditionBuilder.option(option)

@Deprecated("Replaced in favor of more functional conditions")
fun <T> configOption(option: Option<T>, mapping: (T) -> Boolean): OptionCondition =
	ConditionBuilder.option(option, mapping)

infix fun <T> Option<T>.requires(condition: OptionCondition): Option<T> {
	if(available()) setAvailable(condition.validate())
	condition.listenForUpdates.forEach {
		it.addEventListener { _, type ->
			if(type != OptionEventListener.Event.AVAILABILITY_CHANGE) setAvailable(condition.validate())
		}
	}
	return this
}

inline infix fun <T> Option<T>.requires(builder: ConditionBuilder.() -> OptionCondition): Option<T> =
	requires(builder(ConditionBuilder))