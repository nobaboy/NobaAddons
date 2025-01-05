package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.TrophyFishAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.events.LateChatMessageEvent
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.NumberUtils.ordinalSuffix
import me.nobaboy.nobaaddons.utils.TextUtils.aqua
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.gold
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object TrophyFishChat {
	private val config get() = NobaConfigManager.config.fishing.trophyFishing

	fun init() {
		LateChatMessageEvent.EVENT.register(this::modifyChatMessage)
	}

	fun format(name: Text, rarity: TrophyFishRarity, count: Int, total: Int) = buildText {
		append(tr("nobaaddons.fishing.trophyFishing.prefix", "TROPHY FISH!").gold().bold())
		append(" ")
		val count = "${count.addSeparators()}${count.ordinalSuffix()}"
		val rarity = Text.literal(rarity.name).formatted(rarity.formatting, Formatting.BOLD)
		append(tr("nobaaddons.fishing.trophyFishing.caught", "You caught your $count $name $rarity").aqua())
		append(" ")
		val total = "${total.addSeparators()}${total.ordinalSuffix()}"
		append(tr("nobaaddons.fishing.trophyFishing.total", "($total total)").gray())
	}

	private fun modifyChatMessage(event: LateChatMessageEvent) {
		if(!config.modifyChatMessages) return
		val (fish, rarity) = TrophyFishAPI.parseFromChatMessage(event.message.string) ?: return

		val count: Int = TrophyFishAPI.trophyFish[fish.id]?.let { it[rarity] } ?: -1
		val total: Int = TrophyFishAPI.trophyFish[fish.id]?.values?.sum() ?: -1

		event.message = format(fish.displayName, rarity, count, total)
	}
}