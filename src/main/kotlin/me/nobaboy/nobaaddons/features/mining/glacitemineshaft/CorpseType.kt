package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class CorpseType(val color: NobaColor, private val helmetId: String, private val keyId: String? = null) {
	LAPIS(NobaColor.DARK_BLUE, "LAPIS_ARMOR_HELMET"),
	UMBER(NobaColor.GOLD, "ARMOR_OF_YOG_HELMET", "UMBER_KEY"),
	TUNGSTEN(NobaColor.GRAY, "MINERAL_HELMET", "UMBER_KEY"),
	VANGUARD(NobaColor.AQUA, "VANGUARD_HELMET", "SKELETON_KEY");

	val displayName: String = name.title()

	companion object {
		fun getByHelmetOrNull(id: String): CorpseType? {
			return CorpseType.entries.firstOrNull { it.helmetId == id }
		}
	}
}