package me.nobaboy.nobaaddons.features.fishing

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.tr

enum class SeaCreatureType : NameableEnum {
	RARITY,
	HOTSPOT,
	RARE,
	HOTSPOT_RARE;

	override fun getDisplayName() = when(this) {
		RARITY -> tr("nobaaddons.label.seaCreatureType.rarity", "Rarity")
		HOTSPOT -> tr("nobaaddons.label.enchantDisplayMode.hotspot", "Hotspot")
		RARE -> tr("nobaaddons.label.enchantDisplayMode.rare", "Rare")
		HOTSPOT_RARE -> tr("nobaaddons.label.enchantDisplayMode.hotspotRare", "Hotspot & Rare")
	}
}