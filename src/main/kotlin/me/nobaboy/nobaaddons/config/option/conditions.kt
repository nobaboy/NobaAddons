package me.nobaboy.nobaaddons.config.option

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionEventListener
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.Version
import kotlin.jvm.optionals.getOrNull
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0
import kotlin.reflect.KProperty1
import kotlin.reflect.jvm.isAccessible

fun interface OptionCondition {
	fun validate(): Boolean
	fun listenForUpdates(): List<Option<*>> = emptyList()

	fun apply(option: Option<*>) {
		if(option.available()) option.setAvailable(validate())
		listenForUpdates().forEach {
			it.addEventListener { _, type ->
				if(type != OptionEventListener.Event.AVAILABILITY_CHANGE) option.setAvailable(validate())
			}
		}
	}
}

private class WrappingCondition(
	val conditions: Collection<OptionCondition> = emptyList(),
	val options: Collection<Option<*>> = emptyList(),
	val lazyOptions: () -> Collection<Option<*>> = { emptyList() },
	val condition: OptionCondition,
) : OptionCondition {
	override fun validate(): Boolean = condition.validate()

	override fun listenForUpdates(): List<Option<*>> = buildSet {
		addAll(condition.listenForUpdates())
		addAll(options)
		addAll(lazyOptions())
		conditions.forEach { addAll(it.listenForUpdates()) }
	}.toList()
}

class ConditionBuilder(private val group: ConfigOptionGroup) {
	/**
	 * Invert the provided [OptionCondition]'s return value
	 */
	fun not(other: OptionCondition): OptionCondition = WrappingCondition(listOf(other)) { !other.validate() }

	/**
	 * Require that at least one of the provided [conditions] pass validation
	 */
	fun any(vararg conditions: OptionCondition): OptionCondition = WrappingCondition(conditions.toList()) {
		conditions.any { it.validate() }
	}

	/**
	 * Require that all provided [conditions] pass validation
	 */
	fun all(vararg conditions: OptionCondition): OptionCondition = WrappingCondition(conditions.toList()) {
		conditions.all { it.validate() }
	}

	/**
	 * Marks another mod as a requirement, with an optional required version range
	 */
	fun mod(mod: String, min: Version? = null, max: Version? = null) = OptionCondition {
		val mod = FabricLoader.getInstance().getModContainer(mod).getOrNull()
		when {
			mod == null -> false
			min != null && mod.metadata.version < min -> false
			max != null && mod.metadata.version > max -> false
			else -> true
		}
	}

	/**
	 * Marks an option as unavailable if an associated kill switch has been activated
	 *
	 * ```kt
	 * val featureKillSwitch by KillSwitch("feature")
	 *
	 * val option by config(...) {
	 *     requires { killSwitch(::featureKillSwitch) }
	 * }
	 * ```
	 */
	fun killSwitch(killSwitch: () -> Boolean): OptionCondition = OptionCondition { !killSwitch() }

	/**
	 * Marks another [ConfigOption] as a required option. This overload requires that the
	 * provided property has already been defined.
	 *
	 * If that isn't possible, you should instead use `option("name")`.
	 */
	@Suppress("UNCHECKED_CAST")
	fun <T> option(
		other: KProperty<T>,
		group: ConfigOptionGroup = this.group,
		mapper: (T) -> Boolean = { it as Boolean }
	): OptionCondition {
		other.isAccessible = true
		val delegate = when(other) {
			is KProperty0<*> -> other.getDelegate() as ConfigOption<T>
			is KProperty1<*, *> -> (other as KProperty1<Any, *>).getDelegate(group) as ConfigOption<T>
			else -> error("Expected KProperty0 or KProperty1, got ${other::class.simpleName} instead")
		}
		val option = delegate.yaclOption!!
		return WrappingCondition(options = listOf(option)) {
			mapper(option.pendingValue() as T) && option.available()
		}
	}

	/**
	 * Marks another [ConfigOption] as a required option, using a property's name.
	 *
	 * Note that this is not strictly type safe, and you should prefer using a property reference
	 * instead where possible.
	 */
	fun option(key: String): OptionCondition = option<Boolean>(key) { it }

	/**
	 * Marks another [ConfigOption] as a required option, using a property's name and a mapper function.
	 *
	 * Note that this is not strictly type safe, and you should prefer using a property reference
	 * instead where possible.
	 */
	@Suppress("UNCHECKED_CAST")
	fun <T> option(key: String, mapper: (T) -> Boolean): OptionCondition {
		val lazyOther = {
			listOf(group[key]?.let { it as ConfigOption<T> }?.yaclOption as? Option<T>).requireNoNulls()
		}
		val other by lazy { lazyOther().first() }
		return WrappingCondition(lazyOptions = lazyOther) { mapper(other.pendingValue()) && other.available() }
	}
}