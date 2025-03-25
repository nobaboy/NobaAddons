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
import net.minecraft.nbt.NbtInt
import java.lang.ref.WeakReference
import kotlin.jvm.optionals.getOrNull

private val ITEM_TAG_REGEX by Regex("^(?:a )?(?<rarity>(?:UN)?COMMON|RARE|EPIC|LEGENDARY|MYTHIC|DIVINE|ULTIMATE|(?:VERY )?SPECIAL) ?(?<type>[A-Z ]+)?(?: a)?$").fromRepo("skyblock.item_tag")

class SkyBlockItemData(private val item: WeakReference<ItemStack>) {
	private val realNbt: NbtCompound get() = item.get()!!.nbt.nbt
	private val nbt: NbtCompoundWrapper by CacheOf(this::realNbt) { NbtCompoundWrapper(realNbt) }
	private val lore: LoreComponent get() = item.get()!!.lore

	val attributes: Map<Attribute, Int> by CacheOf(this::realNbt) {
		val compound = nbt.getCompound("attributes") ?: return@CacheOf emptyMap()
		buildMap {
			 for((key, value) in compound.entries) {
				val attribute = Attribute.getById(key) ?: continue
				put(attribute, (value as? NbtInt)?./*? if >=1.21.5-pre2 {*//*value*//*?} else {*/intValue()/*?}*/ ?: continue)
			}
		}
	}

	val enchantments: Map<EnchantBase, Int> by CacheOf(this::realNbt) {
		val compound = nbt.getCompound("enchantments") ?: return@CacheOf emptyMap()
		buildMap {
			for((key, value) in compound.entries) {
				val enchant = Enchant.getById(key) ?: continue
				put(
					enchant,
					/*? if >=1.21.5-pre2 {*//*value.asInt()?.getOrNull()*//*?} else {*/(value as? NbtInt)?.intValue()/*?}*/ ?: continue
				)
			}
		}
	}

	// (level, currentValue, nextTier?)
	// e.g. { stackingEnchant = (1, 0, 100_000) }
	val stackingEnchantProgress: Map<StackingEnchant, Triple<Int, Int, Int?>> by CacheOf(this::realNbt) {
		enchantments.mapNotNull {
			val enchant = it.key as? StackingEnchant ?: return@mapNotNull null
			val progress = nbt.getInt(enchant.nbtKey) ?: 0
			val nextTier = enchant.tiers.firstOrNull { it > progress }
			enchant to Triple(it.value, progress, nextTier)
		}.toMap()
	}

	// TODO: Fix this as Hypixel changed how gemstones are stored in the nbt
	val gemstones: List<Gemstone> by CacheOf(this::nbt) {
		emptyList()
		/*buildList {
			val gems = nbt.getCompound("gems")
			gems.keys.forEach { add(Gemstone(it.split('_')[0], gems.getString(it))) }
		}*/
	}

	val reforge: String? by CacheOf(this::realNbt) { nbt.getString("modifier") }
	val recombobulated: Boolean by CacheOf(this::realNbt) { (nbt.getInt("rarity_upgrades") ?: 0) > 0 }
	val stars: Int by CacheOf(this::realNbt) { nbt.getInt("upgrade_level") ?: 0 }
	val powerScroll: String? by CacheOf(this::realNbt) { nbt.getString("power_ability_scroll") }

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

	val id: String by CacheOf(this::realNbt) { nbt.getString("id")!! }
	val uuid: String? by CacheOf(this::realNbt) { nbt.getString("uuid") }
	val timestamp: Timestamp? by CacheOf(this::realNbt) { nbt.getLong("timestamp")?.let(::Timestamp) }
	val donatedToMuseum: Boolean by CacheOf(this::realNbt) { nbt.getBoolean("donated_museum") == true }

	// Potion
	val potion: String? by CacheOf(this::realNbt) { nbt.getString("potion") }
	val potionLevel: Int? by CacheOf(this::realNbt) { nbt.getInt("potion_level") }
	val effects: List<Potion> by CacheOf(this::realNbt) {
		val compounds = nbt.getList("effects") ?: return@CacheOf emptyList()
		buildList {
			compounds.forEach {
				it as? NbtCompound ?: return@forEach
				val name = it.getString("effect")/*? if >=1.21.5-pre2 {*//*.orElseThrow()*//*?}*/
				val level = it.getInt("level")/*? if >=1.21.5-pre2 {*//*.orElseThrow()*//*?}*/
				val duration = it.getInt("duration_ticks")/*? if >=1.21.5-pre2 {*//*.orElseThrow()*//*?}*/

				add(Potion(name, level, duration))
			}
		}
	}

	// Transmission
	val ethermerge: Boolean? by CacheOf(this::realNbt) { nbt.getBoolean("ethermerge") }
	val tunedTransmission: Int? by CacheOf(this::realNbt) { nbt.getInt("tuned_transmission") }

	// Pets
	val petInfo: String? by CacheOf(this::realNbt) { nbt.getString("petInfo") }

	val newYearsCake: Int? by CacheOf(this::realNbt) { nbt.getInt("new_years_cake") }

	val runes: Map<String, Int> by CacheOf(this::realNbt) {
		nbt.getCompound("runes")?.let {
			it.entries.mapValues {
				//? if >=1.21.5-pre2 {
				/*it.value.asInt().orElseThrow()*/
				//?} else {
				(it.value as NbtInt).intValue()
				//?}
			}
		} ?: emptyMap()
	}

	val ranchersSpeed: Int? by CacheOf(this::realNbt) { nbt.getInt("ranchers_speed") }

	override operator fun equals(other: Any?): Boolean = other is SkyBlockItemData && id == other.id && uuid == other.uuid
	override fun hashCode(): Int = item.get().hashCode()

	data class Gemstone(val type: String, val tier: String)
	data class Potion(val name: String, val level: Int, val ticks: Int)
}