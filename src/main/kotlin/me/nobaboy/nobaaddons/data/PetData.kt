package me.nobaboy.nobaaddons.data

import com.google.gson.annotations.Expose
import me.nobaboy.nobaaddons.core.ItemRarity

data class PetData(
	@Expose val name: String,
	@Expose val id: String,
	// TODO make xp and level update from xp gain
	@Expose val level: Int,
	@Expose val xp: Double,
	@Expose val rarity: ItemRarity,
	@Expose val candy: Int = 0,
	@Expose val active: Boolean = false,
	@Expose val heldItem: String? = null,
	@Expose val uuid: String? = null
) {
	val maxLevel: Int get() = if(id == "GOLDEN_DRAGON") 200 else 100
}