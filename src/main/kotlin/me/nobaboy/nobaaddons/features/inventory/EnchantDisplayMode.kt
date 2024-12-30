package me.nobaboy.nobaaddons.features.inventory

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.tr

enum class EnchantDisplayMode : NameableEnum {
	NORMAL,
	COMPACT,
	LINES;

	override fun getDisplayName() = when(this) {
		NORMAL -> tr("nobaaddons.config.uiAndVisuals.enchants.displayMode.normal", "Default")
		COMPACT -> tr("nobaaddons.config.uiAndVisuals.enchants.displayMode.compact", "Compact")
		LINES -> tr("nobaaddons.config.uiAndVisuals.enchants.displayMode.lines", "One per line")
	}
}