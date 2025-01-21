package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.core.fishing.TrophyFish
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.core.profile.ProfileData
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.InventoryEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import java.util.EnumMap

object TrophyFishAPI {
	val trophyFish: MutableMap<String, EnumMap<TrophyFishRarity, Int>> get() = ProfileData.PROFILE.trophyFish

	private val ODGER_RARITY_REGEX by Regex("(?<rarity>Bronze|Silver|Gold|Diamond) [✔✖](?: \\((?<amount>[\\d,]+)\\))?").fromRepo("trophy_fish.odger")
	// .* is required to catch any extra text added afterward from compact chat mods like Compacting
	private val TROPHY_FISH_REGEX by Regex("^TROPHY FISH! You caught an? (?<fish>[A-z 0-9]+) (?<rarity>BRONZE|SILVER|GOLD|DIAMOND)\\..*").fromRepo("trophy_fish.catch")
	private val inventorySlots = 10..31

	fun init() {
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message) }
		InventoryEvents.OPEN.register(this::onInventoryOpen)
	}

	fun parseFromChatMessage(message: String): Pair<TrophyFish, TrophyFishRarity>? {
		val match = TROPHY_FISH_REGEX.matchEntire(message) ?: return null
		val fish = TrophyFish.get(match.groups["fish"]?.value ?: return null) ?: return null
		val rarity = TrophyFishRarity.get(match.groups["rarity"]?.value ?: return null) ?: return null
		return fish to rarity
	}

	private fun onInventoryOpen(event: InventoryEvents.Open) {
		if(event.inventory.title != "Trophy Fishing") return
		inventorySlots.forEach {
			val item = event.inventory.items[it] ?: return@forEach
			if(item.item != Items.PLAYER_HEAD) return@forEach

			val trophy = TrophyFish.get(item.name.string.cleanFormatting()) ?: return@forEach
			val rarities = getCountFromOdgerStack(item).toMutableMap()
				.also { TrophyFishRarity.entries.forEach { e -> if(e !in it) it[e] = 0 } }
				.let { EnumMap(it) }
			trophyFish[trophy.id] = rarities
		}
	}

	private fun onChatMessage(message: Text) {
		val (fish, rarity) = parseFromChatMessage(message.string) ?: return
		val rarities = trophyFish.getOrPut(fish.id) { EnumMap(TrophyFishRarity::class.java) }
		rarities[rarity] = (rarities[rarity] ?: 0) + 1
	}

	fun getCountFromOdgerStack(item: ItemStack): Map<TrophyFishRarity, Int> {
		val fish = item.lore.stringLines.mapNotNull { ODGER_RARITY_REGEX.matchEntire(it) }
		if(fish.isEmpty()) return emptyMap()

		return fish.associate {
			// hypixel doesn't currently add commas to this, but just in case...
			val quantity = it.groups["amount"]?.value?.replace(",", "")?.toInt() ?: 0
			val rarity = it.groups["rarity"]!!.value
			TrophyFishRarity.entries.first { it.name.lowercaseEquals(rarity) } to quantity
		}
	}
}