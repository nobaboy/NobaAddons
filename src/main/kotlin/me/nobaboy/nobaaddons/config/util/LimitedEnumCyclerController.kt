package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.NameableEnum
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import dev.isxander.yacl3.api.controller.ValueFormatter
import dev.isxander.yacl3.gui.controllers.cycling.EnumController
import net.minecraft.text.Text
import net.minecraft.util.TranslatableOption

class LimitedEnumCyclerController<E : Enum<E>>(val option: Option<E>, val onlyInclude: Array<E>) : EnumControllerBuilder<E> {
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