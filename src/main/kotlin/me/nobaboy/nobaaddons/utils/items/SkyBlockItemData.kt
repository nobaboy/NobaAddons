package me.nobaboy.nobaaddons.utils.items

import me.nobaboy.nobaaddons.core.ItemRarity
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.nbtCompound
import me.nobaboy.nobaaddons.utils.properties.CacheOf
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import java.lang.ref.WeakReference

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

	// TODO: Fix this as Hypixel changed how gemstones are stored in the nbt
	val gemstones: List<Gemstone> by CacheOf(this::nbt) {
		buildList {
			val gems = nbt.getCompound("gems")
			gems.keys.forEach { add(Gemstone(it.split('_')[0], gems.getString(it))) }
		}
	}

	val reforge: String? by CacheOf(this::nbt) { nbt.get("modifier")?.asString() }
	val recombobulated: Boolean by CacheOf(this::nbt) { nbt.getInt("rarity_upgrades") > 0 }
	val stars: Int by CacheOf(this::nbt) { nbt.getInt("upgrade_level") }
	val powerScroll: String? by CacheOf(this::nbt) { nbt.get("power_ability_scroll")?.asString() }

	val rarity: ItemRarity by CacheOf(this::lore) {
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

	val potion: String? by CacheOf(this::nbt) { nbt.get("potion")?.asString() }
	val potionLevel: Int by CacheOf(this::nbt) { nbt.getInt("potion_level") }
	val effects: List<Potion> by CacheOf(this::nbt) {
		buildList {
			val compounds = nbt.getList("effects", NbtElement.COMPOUND_TYPE.toInt())?.map { it as NbtCompound } ?: emptyList()
			compounds.forEach {
				val name = it.getString("effect")
				val level = it.getInt("level")
				val duration = it.getInt("duration_ticks")

				add(Potion(name, level, duration))
			}
		}
	}

	val ethermerge: Boolean by CacheOf(this::nbt) { nbt.getBoolean("ethermerge") }
	val tunedTransmission: Int by CacheOf(this::nbt) { nbt.getInt("tuned_transmission") }

	// Pets
	val petInfo: String by CacheOf(this::nbt) { nbt.getString("petInfo") }

	val newYearsCake: Int by CacheOf(this::nbt) { nbt.getInt("new_years_cake") }

	override operator fun equals(other: Any?): Boolean = other is SkyBlockItemData && id == other.id && uuid == other.uuid

	data class Gemstone(val type: String, val tier: String)
	data class Potion(val name: String, val level: Int, val ticks: Int)
}