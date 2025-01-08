package me.nobaboy.nobaaddons.features.inventory.enchants

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.tr

enum class EnchantmentDisplayMode : NameableEnum {
	NORMAL,
	COMPACT,
	LINES;

	override fun getDisplayName() = when(this) {
		NORMAL -> tr("nobaaddons.config.inventory.enchantmentTooltips.enchantDisplayMode.normal", "Default")
		COMPACT -> tr("nobaaddons.config.inventory.enchantmentTooltips.enchantDisplayMode.compact", "Compact")
		LINES -> tr("nobaaddons.config.inventory.enchantmentTooltips.enchantDisplayMode.lines", "One per line")
	}
}