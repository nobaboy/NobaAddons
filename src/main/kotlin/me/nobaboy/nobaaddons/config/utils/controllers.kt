package me.nobaboy.nobaaddons.config.utils

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.ValueFormatter
import dev.isxander.yacl3.gui.controllers.cycling.EnumController
import net.minecraft.text.Text
import net.minecraft.util.TranslatableOption

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

fun OptionBuilder<Boolean>.boolean() {
	controller = { BooleanControllerBuilder.create(it).coloured(true) }
}

fun <T : Enum<T>> OptionBuilder<T>.enumController(cls: Class<T>) {
	controller = { EnumControllerBuilder.create(it).enumClass(cls) }
}

inline fun <reified T : Enum<T>> OptionBuilder<T>.enumController() {
	controller = { EnumControllerBuilder.create(it).enumClass(T::class.java) }
}

inline fun <reified T : Enum<T>> OptionBuilder<T>.enumController(onlyInclude: Array<T>) {
	controller = { LimitedEnumControllerBuilder(it, onlyInclude) }
}