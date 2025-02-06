package me.nobaboy.nobaaddons.config.option

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.controller.ControllerBuilder
import kotlinx.serialization.KSerializer
import net.minecraft.text.Text
import kotlin.reflect.KProperty
import dev.isxander.yacl3.api.Option as YACLOption

typealias YACLOptionBuilder<T> = (ConfigOption<T>) -> YACLOption<*>

interface ConfigOptionHolder {
	operator fun get(key: String): ConfigOption<*>?
}

class OptionBuilder<T>(val holder: ConfigOptionHolder, val serializer: KSerializer<T>) {
	var name: Text? = null

	var description: Text?
		// this is incredibly cursed
		get() = throw UnsupportedOperationException("description does not support getter; get descriptionFactory instead")
		set(value) {
			descriptionFactory = if(value != null) {
				{ value }
			} else {
				null
			}
		}

	var descriptionFactory: ((T) -> Text)? = null

	private var yaclOptionBuilder: YACLOptionBuilder<T>? = null
	var controller: ((YACLOption<T>) -> ControllerBuilder<T>)? = null

	lateinit var defaultFactory: () -> T

	var default: T
		get() = defaultFactory()
		set(value) {
			defaultFactory = { value }
		}

	var condition: OptionCondition? = null

	fun descriptionFactory(factory: (T) -> Text) {
		descriptionFactory = factory
	}

	fun requires(builder: ConditionBuilder.() -> OptionCondition) {
		condition = builder(ConditionBuilder(holder))
	}

	fun yacl(builder: YACLOptionBuilder<T>) {
		yaclOptionBuilder = builder
	}

	private fun defaultYaclBuilder(option: ConfigOption<T>): YACLOptionBuilder<T> = {
		YACLOption.createBuilder<T>().apply {
			name(name!!)
			descriptionFactory?.let {
				description { value -> OptionDescription.of(it(value)) }
			}
			controller(controller!!)
			binding(Binding.generic(option.defaultFactory(), option::get, option::set))
		}.build()
	}

	fun build(): ConfigOption<T> {
		val option = ConfigOption<T>(serializer, defaultFactory)

		if(yaclOptionBuilder == null && name != null) {
			yaclOptionBuilder = defaultYaclBuilder(option)
		}

		option.yaclOptionBuilder = yaclOptionBuilder
		option.condition = condition

		return option
	}
}

class ConfigOption<T>(val serializer: KSerializer<T>, val defaultFactory: () -> T) {
	private var value = defaultFactory()

	internal var yaclOptionBuilder: YACLOptionBuilder<T>? = null
	internal var condition: OptionCondition? = null

	// TODO are there any issues that this would cause by only having a single YACL Option instance
	//      over re-creating this for each new config screen? re-creating this on each new config screen
	//      would be much more complicated to resolve requirements for
	val yaclOption: YACLOption<*>? by lazy {
		yaclOptionBuilder?.let {
			val option = it(this)
			condition?.apply(option)
			option
		}
	}

	fun get() = value

	fun set(value: T) {
		this.value = value
	}

	@Suppress("unused")
	operator fun getValue(instance: Any?, property: KProperty<*>): T = get()

	@Suppress("unused")
	operator fun setValue(instance: Any?, property: KProperty<*>, value: T) = set(value)
}