package me.nobaboy.nobaaddons.utils.items

import me.nobaboy.nobaaddons.core.ItemRarity
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.nbtCompound
import me.nobaboy.nobaaddons.utils.properties.CacheOf
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import java.lang.ref.WeakReference
import kotlin.text.get

private val RARITY_PATTERN = Regex("^(?:. )?(?<RARITY>(?:UN)?COMMON|RARE|EPIC|LEGENDARY|MYTHIC|DIVINE|ULTIMATE|(?:VERY )?SPECIAL).*")

class SkyBlockItemData(private val item: WeakReference<ItemStack>) {
	private val nbt: NbtCompound get() = item.get()!!.nbtCompound.nbt
	private val lore: LoreComponent get() = item.get()!!.lore

	val enchantments: Map<String, Int> by CacheOf(this::nbt) {
		buildMap {
			val enchants = nbt.getCompound("enchantments")
			enchants.keys.forEach { put(it, enchants.getInt(it)) }
		}
	}

	val gemstones: List<Gemstone> by CacheOf(this::nbt) {
		buildList {
			val gems = nbt.getCompound("gems")
			gems.keys.forEach { add(Gemstone(it.split('_')[0], gems.getString(it))) }
		}
	}

	val reforge: String? by CacheOf(this::nbt) { nbt.get("modifier")?.asString() }
	val recombobulated: Boolean by CacheOf(this::nbt) { nbt.getInt("rarity_upgrades") > 0 }
	val stars: Int? by CacheOf(this::nbt) { nbt.getInt("dungeon_item_level") }
	val powerScroll: String? by CacheOf(this::nbt) { nbt.get("power_ability_scroll")?.asString() }

	val rarity: ItemRarity by CacheOf(this::nbt) {
		val match = lore.lines()
			.reversed()
			.asSequence()
			.map { it.string }
			.firstNotNullOfOrNull(RARITY_PATTERN::matchEntire)
			?: return@CacheOf ItemRarity.UNKNOWN
		ItemRarity.rarities[match.groups["RARITY"]!!.value] ?: ItemRarity.UNKNOWN
	}

	val id: String by CacheOf(this::nbt) { nbt.getString("id") }
	val uuid: String? by CacheOf(this::nbt) { nbt.get("uuid")?.asString() }
	val timestamp: Timestamp? by CacheOf(this::nbt) { if(nbt.contains("timestamp")) Timestamp(nbt.getLong("timestamp")) else null }
	val donatedToMuseum: Boolean by CacheOf(this::nbt) { nbt.getBoolean("donated_museum") }

	val ethermerge: Boolean by CacheOf(this::nbt) { nbt.getBoolean("ethermerge") }
	val tunedTransmission: Int by CacheOf(this::nbt) { nbt.getInt("tuned_transmission") }

	data class Gemstone(val type: String, val tier: String)
}