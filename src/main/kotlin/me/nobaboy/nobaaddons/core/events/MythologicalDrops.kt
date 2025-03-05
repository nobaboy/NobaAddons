package me.nobaboy.nobaaddons.core.events

import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text

enum class MythologicalDrops(val displayName: String, val rarity: Rarity) {
	// Treasures
	COINS("Coins", Rarity.LEGENDARY),
	GRIFFIN_FEATHER("Griffin Feather", Rarity.RARE),
	WASHED_UP_SOUVENIR("Washed-up Souvenir", Rarity.LEGENDARY),
	CROWN_OF_GREED("Crown of Greed", Rarity.LEGENDARY),

	// Mob drops
	ANCIENT_CLAW("Ancient Claw", Rarity.RARE),
	ANTIQUE_REMEDIES("Antique Remedies", Rarity.EPIC),
	CROCHET_TIGER_PLUSHIE("Crochet Tiger Plushie", Rarity.EPIC),
	DWARF_TURTLE_SHELMET("Dwarf Turtle Shelmet", Rarity.RARE),
	DAEDALUS_STICK("Daedalus Stick", Rarity.LEGENDARY),
	MINOS_RELIC("Minos Relic", Rarity.EPIC),
	ENCHANTED_BOOK("Chimera", Rarity.COMMON);

	fun toText(): Text = displayName.toText().formatted(rarity.formatting)

	companion object {
		fun getByName(name: String): MythologicalDrops? = entries.firstOrNull { it.displayName == name }
	}
}