package me.nobaboy.nobaaddons.config.ui.controllers.helpers

import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.controller.ControllerBuilder
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text

abstract class ControllerHelper<T>(private val option: Option<T>) : Controller<T> {
	override fun option(): Option<T> = option
	override fun formatValue(): Text = Text.empty()

	companion object {
		inline fun <reified T : Any> createOption(
			name: String,
			noinline controllerBuilder: (Option<T>) -> ControllerBuilder<T>,
			noinline get: () -> T,
			noinline set: (T) -> Unit
		): Option<T> {
			return Option.createBuilder<T>()
				.name(name.toText())
				.binding(get(), get, set)
				.instant(true)
				.controller(controllerBuilder)
				.build()
		}
	}
}