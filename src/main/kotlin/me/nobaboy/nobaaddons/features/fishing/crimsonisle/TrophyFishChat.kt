package me.nobaboy.nobaaddons.features.fishing.crimsonisle

import me.nobaboy.nobaaddons.api.skyblock.fishing.TrophyFishAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.fishing.TrophyFish
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.NumberUtils.ordinalSuffix
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.aqua
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.gold
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.TextUtils.literal
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.Message
import me.nobaboy.nobaaddons.utils.tr
import me.owdding.ktmodules.Module
import net.minecraft.text.Text

@Module
object TrophyFishChat {
	private val config get() = NobaConfig.fishing.trophyFishing
	private val includeIcon get() = !NobaConfig.fishing.catchMessages.revertTreasureMessages

	private val lastMessage: MutableMap<Pair<TrophyFish, TrophyFishRarity>, Message> = mutableMapOf()

	init {
		ChatMessageEvents.ALLOW.register(this::modifyChatMessage)
	}

	fun format(name: Text, rarity: TrophyFishRarity, count: Int, total: Int) = buildText {
		if(includeIcon) {
			literal("♔ ") { gold() }
		}
		append(tr("nobaaddons.fishing.trophyFishing.prefix", "TROPHY FISH!").gold().bold())
		append(" ")
		val count = "${count.addSeparators()}${count.ordinalSuffix()}"
		append(tr("nobaaddons.fishing.trophyFishing.caught", "You caught your $count $name ${rarity.getDisplayName()}").aqua())
		append(" ")
		val total = "${total.addSeparators()}${total.ordinalSuffix()}"
		append(tr("nobaaddons.fishing.trophyFishing.total", "($total total)").gray())
	}

	private fun shouldCompact(rarity: TrophyFishRarity): Boolean {
		if(!config.compactMessages) return false
		return rarity <= config.compactMaxRarity
	}

	private fun modifyChatMessage(event: ChatMessageEvents.Allow) {
		if(!config.modifyChatMessages) return
		val (fish, rarity) = TrophyFishAPI.parseFromChatMessage(event.message.string.cleanFormatting()) ?: return

		val count: Int = TrophyFishAPI.trophyFish[fish.id]?.let { it[rarity] } ?: -1
		val total: Int = TrophyFishAPI.trophyFish[fish.id]?.values?.sum() ?: -1

		event.cancel()
		if(shouldCompact(rarity)) {
			lastMessage[fish to rarity]?.remove()
		}
		lastMessage[fish to rarity] = ChatUtils.addAndCaptureMessage(
			message = format(fish.displayName, rarity, count, total),
			prefix = false,
			color = null,
		)
	}
}