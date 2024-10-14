package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.ListOption
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.controllers.infobox.InfoBox
import me.nobaboy.nobaaddons.config.controllers.infobox.InfoBoxController
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxHud
import net.minecraft.text.Text

object UserInterfaceCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.userInterface"))
			.group(ListOption.createBuilder<InfoBox>()
				.name(Text.translatable("nobaaddons.config.userInterface.infoBoxes"))
				.binding(defaults.userInterface.infoBoxes, config.userInterface::infoBoxes) { config.userInterface.infoBoxes.replaceWith(it) }
				.controller(InfoBoxController.Builder::create)
				.initial(InfoBoxHud::newInfoBox)
				.maximumNumberOfEntries(10)
				.collapsed(true)
				.build()
			)
			.build()
	}
}

private fun <T> MutableList<T>.replaceWith(with: List<T>) {
	clear()
	addAll(with)
}