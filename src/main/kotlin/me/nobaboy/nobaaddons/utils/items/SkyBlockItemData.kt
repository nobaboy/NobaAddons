package me.nobaboy.nobaaddons.utils.items

import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.utils.Timestamp
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.LoreComponent
import net.minecraft.component.type.NbtComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import java.lang.ref.WeakReference

class SkyBlockItemData(private val item: WeakReference<ItemStack>) {
	private val RARITY_PATTERN = Regex("^(?:. )?(?<RARITY>(?:UN)?COMMON|RARE|EPIC|LEGENDARY|MYTHIC|DIVINE|ULTIMATE|(?:VERY )?SPECIAL).*")

	private val nbt: NbtCompound
		get() = item.get()!!.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).nbt
	private val lore: LoreComponent
		get() = item.get()!!.getOrDefault(DataComponentTypes.LORE, LoreComponent.DEFAULT)

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

	val rarity: Rarity by lazy {
		val match = lore.lines()
			.reversed()
			.asSequence()
			.map { it.string }
			.firstNotNullOfOrNull(RARITY_PATTERN::matchEntire)
			?: return@lazy Rarity.UNKNOWN
		Rarity.rarities[match.groups["RARITY"]!!.value] ?: Rarity.UNKNOWN
	}

	val id: String by lazy { nbt.getString("id") }
	val uuid: String? by lazy { nbt.get("uuid")?.asString() }
	val timestamp: Timestamp? by lazy { if(nbt.contains("timestamp")) Timestamp(nbt.getLong("timestamp")) else null }
	val donatedToMuseum: Boolean by lazy { nbt.getBoolean("donated_museum") }

	val ethermerge: Boolean by lazy { nbt.getBoolean("ethermerge") }
	val tunedTransmission: Int by lazy { nbt.getInt("tuned_transmission") }

	data class Gemstone(val type: String, val tier: String)
}