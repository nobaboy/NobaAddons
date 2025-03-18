package me.nobaboy.nobaaddons.config

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
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import dev.isxander.yacl3.api.controller.ValueFormatter
import dev.isxander.yacl3.gui.YACLScreen
import me.nobaboy.nobaaddons.config.util.LimitedEnumCyclerController
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import net.minecraft.text.Text
import java.awt.Color
import kotlin.reflect.KMutableProperty

object NobaConfigUtils {
	@Deprecated("")
	fun createBooleanController(option: Option<Boolean>): BooleanControllerBuilder {
		return BooleanControllerBuilder.create(option).yesNoFormatter().coloured(true)
	}

	@Deprecated("")
	fun createTickBoxController(option: Option<Boolean>): TickBoxControllerBuilder {
		return TickBoxControllerBuilder.create(option)
	}

	@Deprecated("")
	fun <E : Enum<E>> createLimitedCyclerController(option: Option<E>, onlyInclude: Array<E>) =
		LimitedEnumCyclerController(option, onlyInclude)

	@Deprecated("")
	inline fun <reified E : Enum<E>> createCyclerController(option: Option<E>, onlyInclude: Array<E>? = null): EnumControllerBuilder<E> {
		if(onlyInclude != null) return LimitedEnumCyclerController(option, onlyInclude)
		return EnumControllerBuilder.create(option).enumClass(E::class.java)
	}

	@Deprecated("")
	fun createIntegerSliderController(option: Option<Int>, min: Int, max: Int, step: Int, format: ((Int) -> Text)? = null): ControllerBuilder<Int> {
		return IntegerSliderControllerBuilder.create(option).range(min, max).step(step).also { if(format != null) it.formatValue(format) }
	}

	@Deprecated("")
	fun createFloatSliderController(option: Option<Float>, min: Float, max: Float, step: Float, format: ((Float) -> Text)? = null): ControllerBuilder<Float> {
		return FloatSliderControllerBuilder.create(option).range(min, max).step(step).also { if(format != null) it.formatValue(format) }
	}

	@Deprecated("")
	fun createDoubleSliderController(option: Option<Double>, min: Double, max: Double, step: Double, format: ((Double) -> Text)? = null): ControllerBuilder<Double> {
		return DoubleSliderControllerBuilder.create(option).range(min, max).step(step).also { if(format != null) it.formatValue(format) }
	}

	@Deprecated("")
	fun createColorController(option: Option<Color>, allowAlpha: Boolean = false): ColorControllerBuilder {
		return ColorControllerBuilder.create(option).allowAlpha(allowAlpha)
	}

	@Deprecated("")
	fun createStringController(option: Option<String>): StringControllerBuilder {
		return StringControllerBuilder.create(option)
	}

	@Deprecated("")
	fun createLabelController(vararg lines: Text): LabelOption.Builder {
		require(lines.isNotEmpty()) { "Cannot create an empty label controller" }
		return LabelOption.createBuilder().apply {
			if(lines.size == 1) line(lines[0]) else lines(lines.toList())
		}
	}

	@Deprecated("Use category(name) { ... } instead")
	inline fun buildCategory(name: Text, builder: ConfigCategory.Builder.() -> Unit): ConfigCategory = ConfigCategory.createBuilder()
		.name(name)
		.apply(builder)
		.build()

	@Deprecated("Use group(name) { ... } instead")
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

	@Deprecated("intellij please stop trying to import this")
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

	@Deprecated("Use add { booleanController() } instead")
	fun <G : OptionAddable> G.boolean(
		name: Text,
		description: Text? = null,
		default: Boolean,
		property: KMutableProperty<Boolean>
	): Option<Boolean> {
		return add(name, description, ::createBooleanController, default, property)
	}

	@Deprecated("Use add { tickBoxController() } instead")
	fun <G : OptionAddable> G.tickBox(
		name: Text,
		description: Text? = null,
		default: Boolean,
		property: KMutableProperty<Boolean>
	): Option<Boolean> {
		return add(name, description, ::createTickBoxController, default, property)
	}

	@Deprecated("Use add { stringController() } instead")
	fun <G : OptionAddable> G.string(
		name: Text,
		description: Text? = null,
		default: String,
		property: KMutableProperty<String>
	): Option<String> {
		return add(name, description, ::createStringController, default, property)
	}

	@Deprecated("Use add { enumController() } instead")
	inline fun <G : OptionAddable, reified E : Enum<E>> G.cycler(
		name: Text,
		description: Text? = null,
		default: E,
		property: KMutableProperty<E>,
		onlyInclude: Array<E>? = null,
		formatter: ValueFormatter<E>? = null,
	): Option<E> {
		val builder: (Option<E>) -> ControllerBuilder<E> = {
			createCyclerController(it, onlyInclude).apply { if(formatter != null) formatValue(formatter) }
		}
		return add(name, description, builder, default, property)
	}

	@Deprecated("Use add { typeSliderController() } instead")
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
		val controller: (Option<N>) -> ControllerBuilder<N> = when(N::class) {
			Integer::class -> { option ->
				createIntegerSliderController(option as Option<Int>, min.toInt(), max.toInt(), step.toInt(), format as ((Int) -> Text)?) as ControllerBuilder<N>
			}
			Float::class -> { option ->
				createFloatSliderController(option as Option<Float>, min.toFloat(), max.toFloat(), step.toFloat(), format as ((Float) -> Text)?) as ControllerBuilder<N>
			}
			Double::class -> { option ->
				createDoubleSliderController(option as Option<Double>, min.toDouble(), max.toDouble(), step.toDouble(), format as ((Double) -> Text)?) as ControllerBuilder<N>
			}
			else -> throw IllegalArgumentException("${N::class.java} does not have a slider controller")
		}

		return add(name, description, controller, default, property)
	}

	@Deprecated("Use add { colorController() } instead")
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
			.controller { createColorController(it, allowAlpha) }
			.binding(default, { property.getter.call() }) { property.setter.call(it) }
			.build()
		option(option)
		return option
	}

	@Deprecated("Use add { colorController() } instead")
	fun <G : OptionAddable> G.color(
		name: Text,
		description: Text? = null,
		default: NobaColor,
		property: KMutableProperty<NobaColor>
	): Option<Color> {
		val option = Option.createBuilder<Color>()
			.name(name)
			.also { if(description != null) it.description(OptionDescription.of(description)) }
			.controller(::createColorController)
			.binding(default.toColor(), { property.getter.call().toColor() }) { property.setter.call(it.toNobaColor()) }
			.build()
		option(option)
		return option
	}

	@Deprecated("Use 'label { +Text }' instead")
	fun <G : OptionAddable> G.label(vararg lines: Text): G = this.apply { option(createLabelController(*lines).build()) }

	@Deprecated("Use button(name) { ... } instead")
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
}