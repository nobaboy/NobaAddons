package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionAddable
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.gui.YACLScreen
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import kotlin.reflect.KMutableProperty

/**
 * Generic config option builder
 */
class OptionBuilder<T> internal constructor(val binding: Binding<T>) {
	internal constructor(property: NobaConfig.() -> KMutableProperty<T>) : this(binding = property.binding())

	lateinit var name: Text
	var description: OptionDescription? = null

	lateinit var controller: (Option<T>) -> ControllerBuilder<T>

	var requirement: OptionRequirement? = null

	fun require(builder: OptionRequirementBuilder.() -> OptionRequirement) {
		this.requirement = builder(OptionRequirementBuilder)
	}

	internal fun build(): Option<T> {
		val option = Option.createBuilder<T>().apply {
			name(name)
			description?.let(::description)
			binding(binding)
			controller(controller)
		}.build()

		requirement?.let(option::require)

		return option
	}
}

internal fun <T> (NobaConfig.() -> KMutableProperty<T>).binding(): Binding<T> {
	val getter: (NobaConfig) -> T = { this(it).getter.call() }
	val setter: (NobaConfig, T) -> Unit = { config, value -> this(config).setter.call(value) }
	return Binding.generic(getter(NobaConfig.DEFAULTS), { getter(NobaConfig.INSTANCE) }, { setter(NobaConfig.INSTANCE, it) })
}

internal fun <A, B> (NobaConfig.() -> KMutableProperty<A>).binding(biMapper: BiMapper<A, B>): Binding<B> {
	val getter: (NobaConfig) -> B = { biMapper.to(this(it).getter.call()) }
	val setter: (NobaConfig, B) -> Unit = { config, value -> this(config).setter.call(biMapper.from(value)) }
	return Binding.generic(getter(NobaConfig.DEFAULTS), { getter(NobaConfig.INSTANCE) }, { setter(NobaConfig.INSTANCE, it) })
}

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
	property: NobaConfig.() -> KMutableProperty<T>,
	builder: OptionBuilder<T>.() -> Unit,
): Option<T> =
	OptionBuilder<T>(property).apply(builder).build().also(::option)

/**
 * Create a new config option, mapping the stored value type to another for use in YACL
 */
fun <A, B> OptionAddable.add(
	property: NobaConfig.() -> KMutableProperty<A>,
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
}.build()
