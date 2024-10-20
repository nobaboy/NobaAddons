package me.nobaboy.nobaaddons.utils.items

import me.nobaboy.nobaaddons.utils.Timestamp
import net.minecraft.nbt.NbtCompound

class SkyBlockItemData(private val nbt: NbtCompound) {
	val id: String by lazy {
		check(nbt.contains("id")) { "Item NBT lacks an ID" }
		nbt.getString("id")
	}

	val enchantments: Map<String, Int> by lazy {
		buildMap {
			val enchants = nbt.getCompound("enchantments")
			enchants.keys.forEach { put(it, enchants.getInt(it)) }
		}
	}

	val gemstones: List<Gemstone> by lazy {
		buildList {
			val gems = nbt.getCompound("gems")
			gems.keys.forEach { add(Gemstone(it.split('_')[0], gems.getString(it))) }
		}
	}

	val reforge: String? by lazy { nbt.get("modifier")?.asString() }
	val recombobulated: Boolean by lazy { nbt.getInt("rarity_upgrades") > 0 }
	val stars: Int? by lazy { nbt.getInt("dungeon_item_level") }
	val powerScroll: String? by lazy { nbt.get("power_ability_scroll")?.asString() }

	val uuid: String? by lazy { nbt.get("uuid")?.asString() }
	val timestamp: Timestamp? by lazy { if(nbt.contains("timestamp")) Timestamp(nbt.getLong("timestamp")) else null }
	val donatedToMuseum: Boolean by lazy { nbt.getBoolean("donated_museum") }

	val ethermerge: Boolean by lazy { nbt.getBoolean("ethermerge") }
	val tunedTransmission: Int by lazy { nbt.getInt("tuned_transmission") }

	data class Gemstone(val type: String, val tier: String)
}