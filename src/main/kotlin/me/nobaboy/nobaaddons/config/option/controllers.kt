package me.nobaboy.nobaaddons.config.option

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.DoubleSliderControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.EnumDropdownControllerBuilder
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import dev.isxander.yacl3.api.controller.ValueFormatter
import dev.isxander.yacl3.gui.controllers.cycling.EnumController
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import net.minecraft.text.Text
import net.minecraft.util.TranslatableOption
import java.awt.Color

class LimitedEnumControllerBuilder<E : Enum<E>>(private val option: Option<E>, private val onlyInclude: Array<E>) : EnumControllerBuilder<E> {
	private var formatter: ValueFormatter<E> = ValueFormatter<E> {
		when(it) {
			is NameableEnum -> it.displayName
			is TranslatableOption -> it.text
			else -> Text.literal(it.name)
		}
	}

	override fun enumClass(p0: Class<E>): EnumControllerBuilder<E> = throw UnsupportedOperationException()
	override fun formatValue(p0: ValueFormatter<E>): EnumControllerBuilder<E> = this.apply { formatter = p0 }

	@Suppress("UnstableApiUsage")
	override fun build(): Controller<E> = EnumController.createInternal(option, formatter, onlyInclude)
}

fun OptionBuilder<Boolean>.booleanController() {
	controller = { BooleanControllerBuilder.create(it).coloured(true) }
}

fun OptionBuilder<Boolean>.tickBoxController() {
	controller = TickBoxControllerBuilder::create
}

fun OptionBuilder<String>.stringController() {
	controller = StringControllerBuilder::create
}

fun OptionBuilder<Color>.colorController(allowAlpha: Boolean = false) {
	controller = { ColorControllerBuilder.create(it).allowAlpha(allowAlpha) }
}

fun OptionBuilder<NobaColor>.colorController() {
	// this fucking sucks and absolutely breaks certain assumptions - namely, inter-option requirements
	// will violently explode when using the property reference syntax.
	// but, there's unfortunately not a lot else that can be done here without basically just copying the entire
	// color controller and adapting it here.
	// using awt Color is also very inconvenient, as doing so requires explicitly providing a KSerializer for it,
	// as (for whatever reason?) @Serializable doesn't appear to work with this config system(???).
	// in short: this sucks, but it's the most reasonable solution that exists given the limitations of
	// this implementation.
	yacl { option ->
		Option.createBuilder<Color>().apply {
			name(name!!)
			descriptionFactory?.let {
				description { value -> OptionDescription.of(it(value.toNobaColor())) }
			}
			controller(ColorControllerBuilder::create)
			binding(Binding.generic(default.toColor(), { option.get().toColor() }, { option.set(it.toNobaColor()) }))
		}.build()
	}
}

fun OptionBuilder<Int>.intSlider(min: Int, max: Int, step: Int = 1) {
	controller = { IntegerSliderControllerBuilder.create(it).step(step).range(min, max) }
}

fun OptionBuilder<Float>.floatSlider(min: Float, max: Float, step: Float = 1f) {
	controller = { FloatSliderControllerBuilder.create(it).step(step).range(min, max) }
}

fun OptionBuilder<Double>.doubleSlider(min: Double, max: Double, step: Double = 1.0) {
	controller = { DoubleSliderControllerBuilder.create(it).step(step).range(min, max) }
}

fun <T : Enum<T>> OptionBuilder<T>.enumDropdown() {
	controller = { EnumDropdownControllerBuilder.create(it) }
}

fun <T : Enum<T>> OptionBuilder<T>.enumCycler(cls: Class<T>) {
	controller = { EnumControllerBuilder.create(it).enumClass(cls) }
}

inline fun <reified T : Enum<T>> OptionBuilder<T>.enumCycler() {
	controller = { EnumControllerBuilder.create(it).enumClass(T::class.java) }
}

inline fun <reified T : Enum<T>> OptionBuilder<T>.enumCycler(onlyInclude: Array<T>) {
	controller = { LimitedEnumControllerBuilder(it, onlyInclude) }
}