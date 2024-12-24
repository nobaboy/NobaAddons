package me.nobaboy.nobaaddons.utils.items

import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.attributes.Attribute
import me.nobaboy.nobaaddons.core.enchants.EnchantBase
import me.nobaboy.nobaaddons.core.enchants.Enchant
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.nbtCompound
import me.nobaboy.nobaaddons.utils.properties.CacheOf
import net.minecraft.component.type.LoreComponent
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtElement
import java.lang.ref.WeakReference

private val RARITY_PATTERN by Regex("^(?:a )?(?<rarity>(?:UN)?COMMON|RARE|EPIC|LEGENDARY|MYTHIC|DIVINE|ULTIMATE|(?:VERY )?SPECIAL) ?(?<type>[A-Z ]+)?(?: a)?$").fromRepo("item_tag")

class SkyBlockItemData(private val item: WeakReference<ItemStack>) {
	private val nbt: NbtCompound get() = item.get()!!.nbtCompound.nbt
	private val lore: LoreComponent get() = item.get()!!.lore

	val attributes: Map<Attribute, Int> by CacheOf(this::nbt) {
		extractIntMap("attributes", Attribute::getById)
	}

	val enchantments: Map<EnchantBase, Int> by CacheOf(this::nbt) {
		extractIntMap("enchantments", Enchant::getById)
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

	private val rarityLine: MatchResult? by CacheOf(this::lore) {
		lore.lines()
			.reversed()
			.asSequence()
			.map { it.string }
			.firstNotNullOfOrNull(RARITY_PATTERN::matchEntire)
	}

	val rarity: Rarity by CacheOf(this::lore) {
		Rarity.getRarity(rarityLine?.groups["rarity"]?.value ?: return@CacheOf Rarity.UNKNOWN)
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
	val petInfo: String by CacheOf(this::nbt) { nbt.getString("petInfo") }

	val newYearsCake: Int by CacheOf(this::nbt) { nbt.getInt("new_years_cake") }

	val ranchersSpeed: Int by CacheOf(this::nbt) { nbt.getInt("ranchers_speed") }

	private inline fun <T> extractIntMap(
		compoundName: String,
		getById: (String) -> T?
	): Map<T, Int> {
		val compound = nbt.getCompound(compoundName)
		return compound.keys.mapNotNull { id ->
			getById(id)?.let { it to compound.getInt(id) }
		}.toMap()
	}

	override operator fun equals(other: Any?): Boolean = other is SkyBlockItemData && id == other.id && uuid == other.uuid
	override fun hashCode(): Int = item.get().hashCode()

	data class Gemstone(val type: String, val tier: String)
	data class Potion(val name: String, val level: Int, val ticks: Int)
}