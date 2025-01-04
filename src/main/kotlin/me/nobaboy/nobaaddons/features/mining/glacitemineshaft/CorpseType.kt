package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class CorpseType(
	val displayName: Text,
	val color: NobaColor,
	private val helmetId: String,
	private val keyId: String? = null
) {
	LAPIS(tr("nobaaddons.config.mining.glaciteMineshaft.corpseType.lapis", "Lapis Corpse"), NobaColor.BLUE, "LAPIS_ARMOR_HELMET"),
	UMBER(tr("nobaaddons.config.mining.glaciteMineshaft.corpseType.umber", "Umber Corpse"), NobaColor.GOLD, "ARMOR_OF_YOG_HELMET", "UMBER_KEY"),
	TUNGSTEN(tr("nobaaddons.config.mining.glaciteMineshaft.corpseType.tungsten", "Tungsten Corpse"), NobaColor.GRAY, "MINERAL_HELMET", "UMBER_KEY"),
	VANGUARD(tr("nobaaddons.config.mining.glaciteMineshaft.corpseType.vanguard", "Vanguard Corpse"), NobaColor.AQUA, "VANGUARD_HELMET", "SKELETON_KEY");

	val formattedDisplayName = displayName.copy().formatted(color.formatting)

	companion object {
		fun getByHelmetOrNull(helmetId: String): CorpseType? = entries.firstOrNull { it.helmetId == helmetId }
		fun getByKeyOrNull(keyId: String): CorpseType? = entries.firstOrNull { it.keyId == keyId }
	}
}