package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import net.minecraft.text.MutableText
import net.minecraft.util.Formatting

object CatchMessageModifications {
	private val config by NobaConfig.INSTANCE.fishing::catchMessages

	private val TREASURE_CATCH_MESSAGE by Regex("^⛃ (?<rarity>GOOD|GREAT|OUTSTANDING)(?<treasureType> JUNK)? CATCH! You caught .+").fromRepo("fishing.treasure_catch")

	private val colorOverride = mapOf(
		"GOOD" to Formatting.GOLD,
		"GREAT" to Formatting.DARK_PURPLE,
	)

	fun init() {
		ChatMessageEvents.MODIFY.register(::modifyMessage)
	}

	/*
	Good catch:
	empty[style={!italic}, siblings=[
		literal{⛃ }[style={color=dark_purple}],
		literal{GOOD CATCH! }[style={color=dark_purple,bold}],
		literal{You caught }[style={color=white}],
		literal{34,119 Coins}[style={color=gold}],
		literal{!}[style={color=white}]
	]]

	Good junk catch:
	empty[style={!italic}, siblings=[
		literal{⛃ }[style={color=dark_purple}],
		literal{GOOD }[style={color=dark_purple,bold}],
		literal{JUNK}[style={color=dark_green,bold}],
		literal{ CATCH! }[style={color=dark_purple,bold}],
		literal{You caught a }[style={color=white}],
		literal{Rusty Coin}[style={color=green}],
		literal{!}[style={color=white}]
	]]
	 */

	private fun modifyMessage(event: ChatMessageEvents.Modify) {
		if(!config.revertTreasureMessages) return

		val message = event.message
		val match = TREASURE_CATCH_MESSAGE.matchEntire(message.string.cleanFormatting()) ?: return
		val siblings = message.siblings.toMutableList()
		siblings.removeFirst() // drop the icon

		val rarity = match.groups["rarity"]!!.value
		val overrideColor = colorOverride[rarity].also(::println) ?: return
		val isJunk = match.groups["treasureType"] != null

		(siblings[0] as MutableText).styled { it.withColor(overrideColor) }
		if(isJunk) {
			(siblings[2] as MutableText).styled { it.withColor(overrideColor) }
		}

		event.message = buildText {
			siblings.forEach(::append)
			style = message.style
		}
	}
}