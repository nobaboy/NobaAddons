package me.nobaboy.nobaaddons.data

import com.google.gson.annotations.Expose
import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.core.ItemRarity

data class PetData(
	@Expose val name: String,
	@Expose val id: String,
	// TODO make xp update from xp gain
	@Expose val xp: Double,
	@Expose val rarity: ItemRarity,
	@Expose val candy: Int = 0,
	val active: Boolean = false,
	@Expose val heldItem: String? = null,
	@Expose val uuid: String? = null,
) {
	val xpRarity: ItemRarity get() = if(id == "BINGO") ItemRarity.COMMON else rarity // https://wiki.hypixel.net/Pets#Leveling
	val level: Int get() = PetAPI.levelFromXp(xp, xpRarity, maxLevel)
	val maxLevel: Int get() = if(id == "GOLDEN_DRAGON") 200 else 100
}