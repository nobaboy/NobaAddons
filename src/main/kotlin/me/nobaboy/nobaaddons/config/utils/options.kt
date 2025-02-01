package me.nobaboy.nobaaddons.config.utils

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionAddable
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.SliderControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import dev.isxander.yacl3.api.controller.ValueFormatter
import dev.isxander.yacl3.gui.YACLScreen
import me.nobaboy.nobaaddons.features.WrappedOption
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import net.minecraft.text.Text
import java.awt.Color

inline fun buildCategory(name: Text, crossinline builder: ConfigCategory.Builder.() -> Unit): ConfigCategory =
	ConfigCategory.createBuilder().name(name).apply(builder).build()

inline fun buildGroup(name: Text, description: Text? = null, collapsed: Boolean = true, builder: OptionGroup.Builder.() -> Unit): OptionGroup =
	OptionGroup.createBuilder().apply {
		name(name)
		description?.let { description(OptionDescription.of(it)) }
		collapsed(collapsed)
		apply(builder)
	}.build()

inline fun ConfigCategory.Builder.group(
	name: Text,
	description: Text? = null,
	collapsed: Boolean = true,
	crossinline builder: OptionGroup.Builder.() -> Unit
) {
	group(buildGroup(name, description, collapsed, builder))
}

fun <G : OptionAddable, T : Any> G.add(
	name: Text,
	description: Text? = null,
	optionController: (Option<T>) -> ControllerBuilder<T>,
	option: WrappedOption<T>,
): Option<T> = Option.createBuilder<T>().apply {
	name(name)
	description?.let { description(OptionDescription.of(it)) }
	controller(optionController)
	binding(option.default, option::get, option::set)
}.build().also { option(it) }

fun <G : OptionAddable> G.boolean(
	name: Text,
	description: Text? = null,
	option: WrappedOption<Boolean>
): Option<Boolean> = add(name, description, { BooleanControllerBuilder.create(it).coloured(true) }, option)

fun <G : OptionAddable> G.tickBox(
	name: Text,
	description: Text? = null,
	option: WrappedOption<Boolean>
): Option<Boolean> = add(name, description, TickBoxControllerBuilder::create, option)

fun <G : OptionAddable> G.string(
	name: Text,
	description: Text? = null,
	option: WrappedOption<String>
): Option<String> = add(name, description, StringControllerBuilder::create, option)

inline fun <G : OptionAddable, reified E : Enum<E>> G.cycler(
	name: Text,
	description: Text? = null,
	option: WrappedOption<E>,
	onlyInclude: Array<E>? = null,
	formatter: ValueFormatter<E>? = null,
): Option<E> {
	val builder: (Option<E>) -> EnumControllerBuilder<E> = when(onlyInclude) {
		null -> { it -> EnumControllerBuilder.create(it).apply { if(formatter != null) formatValue(formatter) } }
		else -> { it -> LimitedEnumControllerBuilder(it, onlyInclude).apply { if(formatter != null) formatValue(formatter) } }
	}
	return add(name, description, builder, option)
}

@Suppress("UNCHECKED_CAST")
inline fun <G : OptionAddable, reified N : Number> G.slider(
	name: Text,
	description: Text? = null,
	option: WrappedOption<N>,
	min: N,
	max: N,
	step: N,
	noinline format: ((N) -> Text)? = null,
): Option<N> {
	val controller: (Option<N>) -> SliderControllerBuilder<N, *> = when(N::class) {
		Integer::class -> { option -> IntegerSliderControllerBuilder.create(option as Option<Int>) as SliderControllerBuilder<N, *> }
		Float::class -> { option -> FloatSliderControllerBuilder.create(option as Option<Float>) as SliderControllerBuilder<N, *> }
		Double::class -> { option -> DoubleSliderControllerBuilder.create(option as Option<Double>) as SliderControllerBuilder<N, *> }
		else -> throw MatchException(null, null)
	}

	val builder = { option: Option<N> ->
		controller(option)
			.range(min, max)
			.step(step)
			.apply { if(format != null) formatValue(format) }
	}

	return add(name, description, builder, option)
}

fun <G : OptionAddable> G.color(
	name: Text,
	description: Text? = null,
	option: WrappedOption<Color>,
	allowAlpha: Boolean = false,
): Option<Color> {
	val option = Option.createBuilder<Color>()
		.name(name)
		.also { if(description != null) it.description(OptionDescription.of(description)) }
		.controller { ColorControllerBuilder.create(it).allowAlpha(allowAlpha) }
		.binding(option.default, option::get, option::set)
		.build()
	option(option)
	return option
}

fun <G : OptionAddable> G.color(
	name: Text,
	description: Text? = null,
	option: WrappedOption<NobaColor>
): Option<Color> {
	val option = Option.createBuilder<Color>()
		.name(name)
		.also { if(description != null) it.description(OptionDescription.of(description)) }
		.controller(ColorControllerBuilder::create)
		.binding(option.default.toColor(), { option.get().toColor() }, { option.set(it.toNobaColor()) })
		.build()
	option(option)
	return option
}

fun <G : OptionAddable> G.label(vararg lines: Text): G = apply {
	require(lines.isNotEmpty()) { "Cannot create an empty label controller" }
	option(LabelOption.createBuilder().apply {
		if(lines.size == 1) line(lines[0]) else lines(lines.toList())
	}.build())
}

fun <G : OptionAddable> G.button(name: Text, description: Text? = null, text: Text? = null, action: (YACLScreen) -> Unit): ButtonOption {
	val button = ButtonOption.createBuilder()
		.name(name)
		.also { if(description != null) it.description(OptionDescription.of(description)) }
		.also { if(text != null) it.text(text) }
		.action { screen, _ -> action(screen) }
		.build()
	option(button)
	return button
}