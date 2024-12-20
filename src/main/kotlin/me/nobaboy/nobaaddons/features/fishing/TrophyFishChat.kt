package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.TrophyFishAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.fishing.TrophyFish
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.events.LateChatMessageEvent
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.NumberUtils.ordinalSuffix
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.literal
import me.nobaboy.nobaaddons.utils.TextUtils.translatable
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object TrophyFishChat {
	private val config get() = NobaConfigManager.config.fishing.trophyFishing

	fun init() {
		LateChatMessageEvent.EVENT.register(this::modifyChatMessage)
	}

	fun format(fish: TrophyFish, rarity: TrophyFishRarity, count: Int, total: Int) = buildText {
		translatable("nobaaddons.trophyFishing.prefix") { formatted(Formatting.GOLD, Formatting.BOLD) }
		literal(" ")
		translatable(
			"nobaaddons.trophyFishing.caught",
			"${count.addSeparators()}${count.ordinalSuffix()}",
			fish.displayName,
			Text.literal(rarity.name).formatted(rarity.formatting, Formatting.BOLD)
		) { formatted(Formatting.AQUA) }
		literal(" ")
		translatable("nobaaddons.trophyFishing.total", "${total.addSeparators()}${total.ordinalSuffix()}").formatted(Formatting.GRAY)
	}

	private fun modifyChatMessage(event: LateChatMessageEvent) {
		if(!config.modifyChatMessages) return
		val (fish, rarity) = TrophyFishAPI.parseFromChatMessage(event.message.string) ?: return

		val count: Int = TrophyFishAPI.trophyFish[fish]?.let { it[rarity] } ?: -1
		val total: Int = TrophyFishAPI.trophyFish[fish]?.values?.sum() ?: -1

		event.message = format(fish, rarity, count, total)
	}
}