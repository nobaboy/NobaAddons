package me.nobaboy.nobaaddons.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.core.Rarity

@Serializable
data class PetData(
	val name: String,
	val id: String,
	// TODO make xp update from xp gain
	val xp: Double,
	val rarity: Rarity,
	val candy: Int = 0,
	@Transient val active: Boolean = false,
	val heldItem: String? = null,
	val uuid: String? = null,
) {
	val xpRarity: Rarity get() = if(id == "BINGO") Rarity.COMMON else rarity // https://wiki.hypixel.net/Pets#Leveling
	val level: Int get() = PetAPI.levelFromXp(xp, xpRarity, maxLevel)
	val maxLevel: Int get() = if(id == "GOLDEN_DRAGON") 200 else 100
}