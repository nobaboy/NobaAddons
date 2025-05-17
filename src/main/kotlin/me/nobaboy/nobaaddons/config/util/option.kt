package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionAddable
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.api.OptionFlag
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.gui.YACLScreen
import me.nobaboy.nobaaddons.config.AbstractNobaConfig
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import kotlin.reflect.KMutableProperty

/**
 * Generic config option builder
 */
class OptionBuilder<T> internal constructor(val binding: Binding<T>) {
	internal constructor(property: AbstractNobaConfig.() -> KMutableProperty<T>) : this(binding = property.binding())

	lateinit var name: Text
	var description: OptionDescription? = null

	lateinit var controller: (Option<T>) -> ControllerBuilder<T>
	val flags = mutableListOf<OptionFlag>()

	var requirement: OptionRequirement? = null

	fun require(builder: OptionRequirementFactory.() -> OptionRequirement) {
		this.requirement = builder(OptionRequirementFactory)
	}

	val updateListeners: MutableList<OptionEventListener<T>> = mutableListOf()

	fun onUpdate(listener: OptionEventListener<T>) {
		updateListeners.add(listener)
	}

	internal fun build(): Option<T> {
		val option = Option.createBuilder<T>().apply {
			name(name)
			description?.let(::description)
			binding(binding)
			controller(controller)
			flags(flags)
		}.build()

		requirement?.let(option::require)
		updateListeners.forEach(option::addEventListener)

		return option
	}

	companion object {
		lateinit var defaults: AbstractNobaConfig
	}
}

internal fun <T> binding(getter: (AbstractNobaConfig) -> T, setter: (AbstractNobaConfig, T) -> Unit): Binding<T> =
	Binding.generic(getter(OptionBuilder.defaults), { getter(NobaConfig) }, { setter(NobaConfig, it) })

internal fun <T> (AbstractNobaConfig.() -> KMutableProperty<T>).binding(): Binding<T> = binding(
	getter = { this(it).getter.call() },
	setter = { config, value -> this(config).setter.call(value) }
)

internal fun <A, B> (AbstractNobaConfig.() -> KMutableProperty<A>).binding(biMapper: BiMapper<A, B>): Binding<B> = binding(
	getter = { biMapper.to(this(it).getter.call()) },
	setter = { config, value -> this(config).setter.call(biMapper.from(value)) },
)

/**
 * Convenience parameter wrapping the provided [Text] in an [OptionDescription]
 */
var OptionBuilder<*>.descriptionText: Text?
	get() = description?.text()
	set(value) { description = value?.let { OptionDescription.of(it) } }

/**
 * Create a new config option
 */
fun <T> OptionAddable.add(
	property: AbstractNobaConfig.() -> KMutableProperty<T>,
	builder: OptionBuilder<T>.() -> Unit,
): Option<T> =
	OptionBuilder<T>(property).apply(builder).build().also(::option)

/**
 * Create a new config option, mapping the stored value type to another for use in YACL
 */
fun <A, B> OptionAddable.add(
	property: AbstractNobaConfig.() -> KMutableProperty<A>,
	mapping: BiMapper<A, B>,
	builder: OptionBuilder<B>.() -> Unit,
): Option<B> = OptionBuilder<B>(property.binding(mapping)).apply(builder).build().also(::option)

/**
 * Create a new config option with a custom [Binding]
 */
fun <T> OptionAddable.add(binding: Binding<T>, builder: OptionBuilder<T>.() -> Unit): Option<T> =
	OptionBuilder<T>(binding).apply(builder).build().also(::option)

/**
 * Create a new button option
 */
fun OptionAddable.button(
	name: Text,
	description: Text? = null,
	text: Text? = null,
	action: (YACLScreen) -> Unit,
): ButtonOption = ButtonOption.createBuilder().apply {
	name(name)
	description?.let { description(OptionDescription.of(it)) }
	text?.let(::text)
	action { screen, _ -> action(screen) }
}.build().also(::option)

/**
 * Create and add a new [LabelOption] to the current [OptionAddable] using [buildText]
 */
fun OptionAddable.label(builder: MutableText.() -> Unit): LabelOption = LabelOption.create(buildText(builder)).also(::option)

/**
 * Create and add a new [LabelOption] with one or more [Text] elements
 */
fun OptionAddable.label(text: Text, vararg extra: Text): LabelOption = LabelOption.createBuilder().apply {
	line(text)
	extra.toList().takeIf(List<*>::isNotEmpty)?.let(::lines)
}.build().also(::option)
