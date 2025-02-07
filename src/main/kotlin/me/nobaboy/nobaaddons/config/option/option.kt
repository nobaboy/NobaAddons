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
	/**
	 * Optional name; if set, this builder will automatically attach a YACL option builder
	 * (unless a custom [yacl] builder is provided).
	 */
	var name: Text? = null

	/**
	 * Optional description displayed for this option.
	 *
	 * This property does not support its getter, and can only be written to - use [description]
	 * if you need to access this.
	 */
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

	/**
	 * Optional description factory used for the built YACL option
	 */
	var descriptionFactory: ((T) -> Text)? = null

	private var yaclOptionBuilder: YACLOptionBuilder<T>? = null
	private var onSave: ((T) -> Unit)? = null

	/**
	 * Controller for the built YACL option; this is required if [name] is set.
	 */
	var controller: ((YACLOption<T>) -> ControllerBuilder<T>)? = null

	/**
	 * Factory method providing a default value for this config option
	 */
	lateinit var defaultFactory: () -> T

	/**
	 * Default value for this config option; this property is a convenience wrapper around [defaultFactory].
	 */
	var default: T
		get() = defaultFactory()
		set(value) {
			defaultFactory = { value }
		}

	/**
	 * @see requires
	 */
	var condition: OptionCondition? = null

	/**
	 * @see descriptionFactory
	 */
	fun description(factory: (T) -> Text) {
		descriptionFactory = factory
	}

	/**
	 * Add an event listener to be run when the config is updated through the YACL menu
	 *
	 * Note that this method is always run, even if the value for this option has not changed.
	 */
	fun onSave(event: (T) -> Unit) {
		onSave = event
	}

	/**
	 * Add an option condition to this option; this will be attached to the built YACL option,
	 * marking it as unavailable if the condition returns `false`.
	 */
	fun requires(builder: ConditionBuilder.() -> OptionCondition) {
		condition = builder(ConditionBuilder(holder))
	}

	/**
	 * Override the default YACL option builder for this option
	 *
	 * Note that this allows for returning a different type than this option normally holds; this is
	 * by design, to allow for [colorController] to function properly.
	 *
	 * Care should be taken to avoid changing the built option type if it isn't strictly necessary,
	 * as doing so will result in any [requires] that uses property reference syntax on this option
	 * to violently explode (but this can be worked around by using the property name as a string instead).
	 */
	fun yacl(builder: YACLOptionBuilder<T>) {
		yaclOptionBuilder = builder
	}

	private fun defaultYaclBuilder(option: ConfigOption<T>): YACLOptionBuilder<T> = {
		val name = name!!
		val descriptionFactory = descriptionFactory
		val controller = controller!!
		YACLOption.createBuilder<T>().apply {
			name(name)
			descriptionFactory?.let {
				description { value -> OptionDescription.of(it(value)) }
			}
			controller(controller)
			binding(Binding.generic(option.defaultFactory(), option::get, option::set))
		}.build()
	}

	fun build(): ConfigOption<T> {
		val option = ConfigOption<T>(serializer, defaultFactory, onSave)

		if(yaclOptionBuilder == null && name != null) {
			yaclOptionBuilder = defaultYaclBuilder(option)
		}

		option.yaclOptionBuilder = yaclOptionBuilder
		option.condition = condition

		return option
	}
}

class ConfigOption<T> internal constructor(
	val serializer: KSerializer<T>,
	val defaultFactory: () -> T,
	private val onSave: ((T) -> Unit)? = null
) {
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

	fun saveEvent() {
		onSave?.invoke(get())
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