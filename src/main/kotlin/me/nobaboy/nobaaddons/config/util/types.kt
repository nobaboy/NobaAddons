package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import dev.isxander.yacl3.api.controller.ValueFormatter
import net.minecraft.text.Text
import java.awt.Color

fun OptionBuilder<Boolean>.booleanController() {
	controller = { BooleanControllerBuilder.create(it).coloured(true) }
}

fun OptionBuilder<Boolean>.tickBoxController() {
	controller = TickBoxControllerBuilder::create
}

fun OptionBuilder<String>.stringController() {
	controller = StringControllerBuilder::create
}

inline fun <reified T : Enum<T>> OptionBuilder<T>.enumController() {
	controller = { EnumControllerBuilder.create(it).enumClass(T::class.java) }
}

fun <T : Enum<T>> OptionBuilder<T>.enumController(onlyInclude: Array<T>) {
	controller = { LimitedEnumCyclerController(it, onlyInclude) }
}

fun OptionBuilder<Color>.colorController(allowAlpha: Boolean = false) {
	controller = { ColorControllerBuilder.create(it).allowAlpha(allowAlpha) }
}

fun OptionBuilder<Int>.intSliderController(min: Int, max: Int, step: Int = 1, format: ValueFormatter<Int>? = null) {
	controller = {
		IntegerSliderControllerBuilder.create(it).apply {
			range(min, max)
			step(step)
			format?.let(::formatValue)
		}
	}
}

fun OptionBuilder<Float>.floatSliderController(min: Float, max: Float, step: Float = 1f, format: ValueFormatter<Float>? = null) {
	controller = {
		FloatSliderControllerBuilder.create(it).apply {
			range(min, max)
			step(step)
			format?.let(::formatValue)
		}
	}
}

fun OptionBuilder<Int>.intFieldController() {
	controller = IntegerFieldControllerBuilder::create
}
