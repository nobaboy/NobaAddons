package me.nobaboy.nobaaddons.config.controllers

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.ControllerBuilder
import net.minecraft.text.Text

abstract class ControllerHelper<T>(private val option: Option<T>) : Controller<T> {
	override fun option(): Option<T> = option

	override fun formatValue(): Text = Text.empty()

	companion object {
		fun <T : Any> createOption(
			name: String,
			controllerBuilder: (Option<T>) -> ControllerBuilder<T>,
			get: () -> T,
			set: (T) -> Unit,
		): Option<T> {
			return Option.createBuilder<T>()
				.name(Text.literal(name))
				.binding(get(), get, set)
				.instant(true)
				.controller(controllerBuilder)
				.build()
		}
	}
}