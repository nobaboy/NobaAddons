package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.TrophyFishAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.events.LateChatMessageEvent
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.NumberUtils.ordinalSuffix
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.translatable
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object TrophyFishChat {
	private val config get() = NobaConfigManager.config.fishing.trophyFishing

	fun init() {
		LateChatMessageEvent.EVENT.register(this::modifyChatMessage)
	}

	fun format(name: Text, rarity: TrophyFishRarity, count: Int, total: Int) = buildText {
		translatable("nobaaddons.trophyFishing.prefix") { formatted(Formatting.GOLD, Formatting.BOLD) }
		append(" ")
		translatable(
			"nobaaddons.trophyFishing.caught",
			"${count.addSeparators()}${count.ordinalSuffix()}",
			name,
			Text.literal(rarity.name).formatted(rarity.formatting, Formatting.BOLD)
		) { formatted(Formatting.AQUA) }
		append(" ")
		translatable("nobaaddons.trophyFishing.total", "${total.addSeparators()}${total.ordinalSuffix()}").formatted(Formatting.GRAY)
	}

	private fun modifyChatMessage(event: LateChatMessageEvent) {
		if(!config.modifyChatMessages) return
		val (fish, rarity) = TrophyFishAPI.parseFromChatMessage(event.message.string) ?: return

		val count: Int = TrophyFishAPI.trophyFish[fish.id]?.let { it[rarity] } ?: -1
		val total: Int = TrophyFishAPI.trophyFish[fish.id]?.values?.sum() ?: -1

		event.message = format(fish.displayName, rarity, count, total)
	}
}