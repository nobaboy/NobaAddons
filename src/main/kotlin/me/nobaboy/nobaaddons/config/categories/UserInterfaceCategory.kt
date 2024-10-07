package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.ListOption
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import me.nobaboy.nobaaddons.config.NobaConfig
import net.minecraft.text.Text

object UserInterfaceCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.userInterface"))
			.group(ListOption.createBuilder<String>()
				.name(Text.translatable("nobaaddons.config.userInterface.infoBoxes"))
				.binding(defaults.userInterface.infoBoxes, config.userInterface::infoBoxes) { config.userInterface.infoBoxes.replaceWith(it) }
				.controller(StringControllerBuilder::create)
				.maximumNumberOfEntries(10)
				.collapsed(true)
				.initial("")
				.build()
			)
			.build()
	}
}

private fun <T> MutableList<T>.replaceWith(with: MutableList<T>) {
	clear()
	addAll(with)
}