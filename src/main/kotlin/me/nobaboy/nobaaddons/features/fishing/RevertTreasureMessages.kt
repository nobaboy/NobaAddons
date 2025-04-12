package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import net.minecraft.text.MutableText
import net.minecraft.util.Formatting

object RevertTreasureMessages {
	private val config get() = NobaConfig.fishing.catchMessages
	private val enabled: Boolean get() = config.revertTreasureMessages && !SkyBlockAPI.inSkyBlock

	private val TREASURE_CATCH_REGEX by Repo.regex(
		"fishing.treasure_catch",
		"^⛃ (?<rarity>GOOD|GREAT|OUTSTANDING)(?<treasureType> JUNK)? CATCH! You caught .+"
	)

	private val treasureCatchColors = mapOf(
		"GOOD" to Formatting.GOLD,
		"GREAT" to Formatting.DARK_PURPLE,
	)

	fun init() {
		ChatMessageEvents.MODIFY.register(this::modifyMessage)
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
		if(!enabled) return

		val message = event.message
		val match = TREASURE_CATCH_REGEX.matchEntire(message.string.cleanFormatting()) ?: return
		val siblings = message.siblings.toMutableList()
		siblings.removeFirst() // drop the icon

		val rarity = match.groups["rarity"]!!.value
		val catchColor = treasureCatchColors[rarity] ?: return
		val isJunk = match.groups["treasureType"] != null

		// revert the color of the catch tier
		(siblings[0] as MutableText).styled { it.withColor(catchColor) }
		if(isJunk) (siblings[2] as MutableText).styled { it.withColor(catchColor) }

		event.message = buildText {
			siblings.forEach(::append)
			style = message.style
		}
	}
}