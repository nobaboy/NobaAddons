package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class CorpseType(val color: NobaColor, private val helmetId: String, private val keyId: String? = null) {
	LAPIS(NobaColor.BLUE, "LAPIS_ARMOR_HELMET"),
	UMBER(NobaColor.GOLD, "ARMOR_OF_YOG_HELMET", "UMBER_KEY"),
	TUNGSTEN(NobaColor.GRAY, "MINERAL_HELMET", "UMBER_KEY"),
	VANGUARD(NobaColor.AQUA, "VANGUARD_HELMET", "SKELETON_KEY");

	override fun toString(): String = name.title()

	companion object {
		fun getByHelmetOrNull(helmetId: String): CorpseType? = entries.firstOrNull { it.helmetId == helmetId }
		fun getByKeyOrNull(keyId: String): CorpseType? = entries.firstOrNull { it.keyId == keyId }
	}
}