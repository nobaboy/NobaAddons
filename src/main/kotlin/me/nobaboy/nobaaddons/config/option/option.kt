package me.nobaboy.nobaaddons.config.option

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.controller.ControllerBuilder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.minecraft.text.Text
import kotlin.reflect.KProperty
import dev.isxander.yacl3.api.Option as YACLOption

interface ConfigOptionHolder {
	operator fun get(key: String): ConfigOption<*>?
}

class OptionBuilder<T>(val holder: ConfigOptionHolder, val serializer: KSerializer<T>) {
	var name: Text? = null
	var description: Text? = null

	var controller: ((YACLOption<T>) -> ControllerBuilder<T>)? = null

	lateinit var defaultFactory: () -> T

	var default: T
		get() = defaultFactory()
		set(value) {
			defaultFactory = { value }
		}

	var condition: OptionCondition? = null

	fun requires(builder: ConditionBuilder.() -> OptionCondition) {
		condition = builder(ConditionBuilder(holder))
	}

	fun build(): ConfigOption<T> {
		val option = ConfigOption<T>(serializer, defaultFactory)

		if(name != null) {
			option.yaclOptionBuilder = {
				val built = YACLOption.createBuilder<T>().apply {
					name(name!!)
					description?.let { description(OptionDescription.of(it)) }
					controller(controller!!)
					binding(Binding.generic(option.defaultFactory(), option::get, option::set))
				}.build()
				condition?.apply(built)
				built
			}
		}

		return option
	}
}

inline fun <reified T> buildOption(holder: ConfigOptionHolder, builder: OptionBuilder<T>.() -> Unit): ConfigOption<T> {
	val optionBuilder = OptionBuilder<T>(holder, serializer())
	builder(optionBuilder)
	return optionBuilder.build()
}

class ConfigOption<T>(val serializer: KSerializer<T>, val defaultFactory: () -> T) {
	private var value = defaultFactory()

	internal var yaclOptionBuilder: (() -> YACLOption<T>)? = null
	val yaclOption: YACLOption<T>? by lazy { yaclOptionBuilder?.let { it() } }

	fun get() = value

	fun set(value: T) {
		this.value = value
	}

	@Suppress("unused")
	operator fun getValue(instance: Any?, property: KProperty<*>): T = get()

	@Suppress("unused")
	operator fun setValue(instance: Any?, property: KProperty<*>, value: T) = set(value)
}