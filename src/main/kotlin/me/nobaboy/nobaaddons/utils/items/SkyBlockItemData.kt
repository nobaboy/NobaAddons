package me.nobaboy.nobaaddons.utils.items

import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.attributes.Attribute
import me.nobaboy.nobaaddons.core.enchants.Enchant
import me.nobaboy.nobaaddons.core.enchants.EnchantBase
import me.nobaboy.nobaaddons.core.enchants.StackingEnchant
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.nbt
import me.nobaboy.nobaaddons.utils.properties.CacheOf
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import java.lang.ref.WeakReference

private val ITEM_TAG_REGEX by Regex("^(?:a )?(?<rarity>(?:UN)?COMMON|RARE|EPIC|LEGENDARY|MYTHIC|DIVINE|ULTIMATE|(?:VERY )?SPECIAL) ?(?<type>[A-Z ]+)?(?: a)?$").fromRepo("skyblock.item_tag")

class SkyBlockItemData(private val item: WeakReference<ItemStack>) {
	private val nbt: NbtCompound get() = item.get()!!.nbt.nbt
	private val lore: LoreComponent get() = item.get()!!.lore

	val attributes: Map<Attribute, Int> by CacheOf(this::nbt) {
		val compound = nbt.getCompound("attributes")
		buildMap {
			compound.keys.forEach { key ->
				Attribute.getById(key)?.let { put(it, compound.getInt(key)) }
			}
		}
	}

	val enchantments: Map<EnchantBase, Int> by CacheOf(this::nbt) {
		val compound = nbt.getCompound("enchantments")
		buildMap {
			compound.keys.forEach { key ->
				Enchant.getById(key)?.let { put(it, compound.getInt(key)) }
			}
		}
	}

	// (level, currentValue, nextTier?)
	// e.g. { stackingEnchant = (1, 0, 100_000) }
	val stackingEnchantProgress: Map<StackingEnchant, Triple<Int, Int, Int?>> by CacheOf(this::nbt) {
		enchantments.mapNotNull {
			val enchant = it.key as? StackingEnchant ?: return@mapNotNull null
			val progress = nbt.getInt(enchant.nbtKey)
			val nextTier = enchant.tiers.firstOrNull { it > progress }
			enchant to Triple(it.value, progress, nextTier)
		}.toMap()
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

	private val itemTagLine: MatchResult? by CacheOf(this::lore) {
		lore.lines()
			.reversed()
			.asSequence()
			.map { it.string }
			.firstNotNullOfOrNull(ITEM_TAG_REGEX::matchEntire)
	}

	val rarity: Rarity by CacheOf(this::lore) {
		Rarity.getRarity(itemTagLine?.groups["rarity"]?.value ?: return@CacheOf Rarity.UNKNOWN)
	}

	val id: String by CacheOf(this::nbt) { nbt.getString("id") }
	val uuid: String? by CacheOf(this::nbt) { nbt.get("uuid")?.asString() }
	val timestamp: Timestamp? by CacheOf(this::nbt) { if(nbt.contains("timestamp")) Timestamp(nbt.getLong("timestamp")) else null }
	val donatedToMuseum: Boolean by CacheOf(this::nbt) { nbt.getBoolean("donated_museum") }

	// Potion
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

	// Transmission
	val ethermerge: Boolean by CacheOf(this::nbt) { nbt.getBoolean("ethermerge") }
	val tunedTransmission: Int by CacheOf(this::nbt) { nbt.getInt("tuned_transmission") }

	// Pets
	val petInfo: String? by CacheOf(this::nbt) { nbt.get("petInfo")?.asString() }

	val newYearsCake: Int by CacheOf(this::nbt) { nbt.getInt("new_years_cake") }

	val runes: Map<String, Int> by CacheOf(this::nbt) {
		nbt.getCompound("runes").let { compound -> compound.keys.associate { it to compound.getInt(it) } }
	}

	val ranchersSpeed: Int by CacheOf(this::nbt) { nbt.getInt("ranchers_speed") }

	override operator fun equals(other: Any?): Boolean = other is SkyBlockItemData && id == other.id && uuid == other.uuid
	override fun hashCode(): Int = item.get().hashCode()

	data class Gemstone(val type: String, val tier: String)
	data class Potion(val name: String, val level: Int, val ticks: Int)
}