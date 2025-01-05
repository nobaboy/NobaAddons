package me.nobaboy.nobaaddons.config

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.NameableEnum
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
import dev.isxander.yacl3.gui.controllers.cycling.EnumController
import net.minecraft.text.Text
import net.minecraft.util.TranslatableOption
import java.awt.Color
import kotlin.reflect.KMutableProperty

object NobaConfigUtils {
	fun createBooleanController(option: Option<Boolean>): BooleanControllerBuilder {
		return BooleanControllerBuilder.create(option).onOffFormatter().coloured(true)
	}

	fun createTickBoxController(option: Option<Boolean>): TickBoxControllerBuilder {
		return TickBoxControllerBuilder.create(option)
	}

	@Suppress("UnstableApiUsage")
	fun <E : Enum<E>> createLimitedCyclerController(option: Option<E>, onlyInclude: Array<E>) = object : EnumControllerBuilder<E> {
		// I couldn't get EnumController.createDefaultFormatter() to work, so we're just reimplementing
		// this ourselves instead.
		private var formatter: ValueFormatter<E> = ValueFormatter<E> {
			when(it) {
				is NameableEnum -> it.displayName
				is TranslatableOption -> it.text
				else -> Text.literal(it.name)
			}
		}

		override fun enumClass(p0: Class<E>): EnumControllerBuilder<E> = throw UnsupportedOperationException()
		override fun formatValue(p0: ValueFormatter<E>): EnumControllerBuilder<E> = this.apply { formatter = p0 }
		override fun build(): Controller<E> = EnumController.createInternal(option, formatter, onlyInclude)
	}

	inline fun <reified E : Enum<E>> createCyclerController(option: Option<E>, onlyInclude: Array<E>? = null): EnumControllerBuilder<E> {
		if(onlyInclude != null) return createLimitedCyclerController(option, onlyInclude)
		return EnumControllerBuilder.create(option).enumClass(E::class.java)
	}

	inline fun <reified E : Enum<E>> createCyclerController(option: Option<E>): EnumControllerBuilder<E> {
		return EnumControllerBuilder.create(option).enumClass(E::class.java)
	}

	fun createIntegerSliderController(option: Option<Int>, min: Int, max: Int, step: Int, format: ((Int) -> Text)? = null): ControllerBuilder<Int> {
		return IntegerSliderControllerBuilder.create(option).range(min, max).step(step).also { if(format != null) it.formatValue(format) }
	}

	fun createFloatSliderController(option: Option<Float>, min: Float, max: Float, step: Float, format: ((Float) -> Text)? = null): ControllerBuilder<Float> {
		return FloatSliderControllerBuilder.create(option).range(min, max).step(step).also { if(format != null) it.formatValue(format) }
	}

	fun createDoubleSliderController(option: Option<Double>, min: Double, max: Double, step: Double, format: ((Double) -> Text)? = null): ControllerBuilder<Double> {
		return DoubleSliderControllerBuilder.create(option).range(min, max).step(step).also { if(format != null) it.formatValue(format) }
	}

	fun createColorController(option: Option<Color>): ColorControllerBuilder {
		return ColorControllerBuilder.create(option)
	}

	fun createStringController(option: Option<String>): StringControllerBuilder {
		return StringControllerBuilder.create(option)
	}

	fun createLabelController(vararg lines: Text): LabelOption.Builder {
		return LabelOption.createBuilder().apply {
			if(lines.size == 1) line(lines[0]) else lines(lines.toList())
		}
	}

	inline fun buildCategory(name: Text, builder: ConfigCategory.Builder.() -> Unit): ConfigCategory = ConfigCategory.createBuilder()
		.name(name)
		.apply(builder)
		.build()

	inline fun ConfigCategory.Builder.buildGroup(
		name: Text,
		description: Text? = null,
		collapsed: Boolean = true,
		crossinline builder: OptionGroup.Builder.() -> Unit
	): ConfigCategory.Builder {
		group(OptionGroup.createBuilder()
			.name(name)
			.also { if(description != null) it.description(OptionDescription.of(description)) }
			.also { builder(it) }
			.collapsed(collapsed)
			.build())

		return this
	}

	fun <G : OptionAddable, T : Any> G.add(
		name: Text,
		description: Text? = null,
		optionController: (Option<T>) -> ControllerBuilder<T>,
		default: T,
		property: KMutableProperty<T>
	): G {
		option(Option.createBuilder<T>()
			.name(name)
			.also { if(description != null) it.description(OptionDescription.of(description)) }
			.binding(default, property.getter::call, property.setter::call)
			.controller(optionController)
			.build())

		return this
	}

	fun <G : OptionAddable> G.boolean(
		name: Text,
		description: Text? = null,
		default: Boolean,
		property: KMutableProperty<Boolean>
	): G {
		return add(name, description, ::createBooleanController, default, property)
	}

	fun <G : OptionAddable> G.tickBox(
		name: Text,
		description: Text? = null,
		default: Boolean,
		property: KMutableProperty<Boolean>
	): G {
		return add(name, description, ::createTickBoxController, default, property)
	}

	fun <G : OptionAddable> G.string(
		name: Text,
		description: Text? = null,
		default: String,
		property: KMutableProperty<String>
	): G {
		return add(name, description, ::createStringController, default, property)
	}

	inline fun <G : OptionAddable, reified E : Enum<E>> G.cycler(
		name: Text,
		description: Text? = null,
		default: E,
		property: KMutableProperty<E>,
		onlyInclude: Array<E>? = null,
		formatter: ValueFormatter<E>? = null,
	): G {
		val builder: (Option<E>) -> ControllerBuilder<E> = {
			createCyclerController(it, onlyInclude).apply { if(formatter != null) formatValue(formatter) }
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
	): G {
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

	fun <G : OptionAddable> G.color(
		name: Text,
		description: Text? = null,
		default: Color,
		property: KMutableProperty<Color>
	): G {
		return add(name, description, ::createColorController, default, property)
	}

	fun <G : OptionAddable> G.label(vararg lines: Text): G = this.apply { option(createLabelController(*lines).build()) }

	fun <G : OptionAddable> G.button(name: Text, description: Text? = null, text: Text? = null, action: (YACLScreen) -> Unit): G {
		option(ButtonOption.createBuilder()
			.name(name)
			.also { if(description != null) it.description(OptionDescription.of(description)) }
			.also { if(text != null) it.text(text) }
			.action { screen, _ -> action(screen) }
			.build())
		return this
	}
}