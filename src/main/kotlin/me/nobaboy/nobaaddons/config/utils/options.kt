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
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import net.minecraft.text.Text
import java.awt.Color
import kotlin.reflect.KMutableProperty

inline fun buildCategory(name: Text, crossinline builder: ConfigCategory.Builder.() -> Unit): ConfigCategory =
	ConfigCategory.createBuilder().name(name).apply(builder).build()

inline fun ConfigCategory.Builder.buildGroup(
	name: Text,
	description: Text? = null,
	collapsed: Boolean = true,
	crossinline builder: OptionGroup.Builder.() -> Unit
) {
	group(OptionGroup.createBuilder()
		.name(name)
		.also { if(description != null) it.description(OptionDescription.of(description)) }
		.also { builder(it) }
		.collapsed(collapsed)
		.build())
}

fun <G : OptionAddable, T : Any> G.add(
	name: Text,
	description: Text? = null,
	optionController: (Option<T>) -> ControllerBuilder<T>,
	default: T,
	property: KMutableProperty<T>
): Option<T> {
	return Option.createBuilder<T>()
		.name(name)
		.also { if(description != null) it.description(OptionDescription.of(description)) }
		.binding(default, property.getter::call, property.setter::call)
		.controller(optionController)
		.build()
		.also { option(it) }
}

fun <G : OptionAddable> G.boolean(
	name: Text,
	description: Text? = null,
	default: Boolean,
	property: KMutableProperty<Boolean>
): Option<Boolean> {
	return add(name, description, BooleanControllerBuilder::create, default, property)
}

fun <G : OptionAddable> G.tickBox(
	name: Text,
	description: Text? = null,
	default: Boolean,
	property: KMutableProperty<Boolean>
): Option<Boolean> {
	return add(name, description, TickBoxControllerBuilder::create, default, property)
}

fun <G : OptionAddable> G.string(
	name: Text,
	description: Text? = null,
	default: String,
	property: KMutableProperty<String>
): Option<String> {
	return add(name, description, StringControllerBuilder::create, default, property)
}

inline fun <G : OptionAddable, reified E : Enum<E>> G.cycler(
	name: Text,
	description: Text? = null,
	default: E,
	property: KMutableProperty<E>,
	onlyInclude: Array<E>? = null,
	formatter: ValueFormatter<E>? = null,
): Option<E> {
	val builder: (Option<E>) -> EnumControllerBuilder<E> = when(onlyInclude) {
		null -> { it -> EnumControllerBuilder.create(it).apply { if(formatter != null) formatValue(formatter) } }
		else -> { it -> LimitedEnumControllerBuilder(it, onlyInclude).apply { if(formatter != null) formatValue(formatter) } }
	}
	return add(name, description, builder, default, property)
}

@Suppress("UNCHECKED_CAST")
inline fun <G : OptionAddable, reified N : Number> G.slider(
	name: Text,
	description: Text? = null,
	default: N,
	property: KMutableProperty<N>,
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

	return add(name, description, builder, default, property)
}

fun <G : OptionAddable> G.color(
	name: Text,
	description: Text? = null,
	default: Color,
	property: KMutableProperty<Color>,
	allowAlpha: Boolean = false,
): Option<Color> {
	val option = Option.createBuilder<Color>()
		.name(name)
		.also { if(description != null) it.description(OptionDescription.of(description)) }
		.controller { ColorControllerBuilder.create(it).allowAlpha(allowAlpha) }
		.binding(default, { property.getter.call() }) { property.setter.call(it) }
		.build()
	option(option)
	return option
}

fun <G : OptionAddable> G.color(
	name: Text,
	description: Text? = null,
	default: NobaColor,
	property: KMutableProperty<NobaColor>
): Option<Color> {
	val option = Option.createBuilder<Color>()
		.name(name)
		.also { if(description != null) it.description(OptionDescription.of(description)) }
		.controller(ColorControllerBuilder::create)
		.binding(default.toColor(), { property.getter.call().toColor() }) { property.setter.call(it.toNobaColor()) }
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