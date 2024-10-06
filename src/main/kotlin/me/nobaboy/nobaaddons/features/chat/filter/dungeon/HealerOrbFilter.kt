package me.nobaboy.nobaaddons.features.chat.filter.dungeon

import me.nobaboy.nobaaddons.api.SkyblockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.impl.ChatFilterOption
import me.nobaboy.nobaaddons.utils.NumberUtils.formatDouble
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.clean
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.regex.Pattern

object HealerOrbFilter {
	private val config get() = NobaConfigManager.get().chat.filter

	private val otherPickupUserOrbPattern: Pattern = Pattern.compile("◕ [A-z0-9_]+ picked up your (?<orb>[A-z ]+)!")
	private val userPickupHealerOrbPattern: Pattern = Pattern.compile(
		"^◕ You picked up a (?<orb>[A-z ]+) from (?<player>[A-z0-9_]+) healing you for (?<health>[0-9,.]+)❤ and granting you (?<buff>[A-z0-9+% ]+) for 10 seconds\\."
	)

	fun init() {
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ ->
			return@register processMessage(message.string.clean())
		}
	}

	private fun processMessage(message: String): Boolean {
		if(!isEnabled()) return true

		userPickupHealerOrbPattern.matchMatcher(message) {
			val option = config.healerOrbMessage
			return when(option) {
				ChatFilterOption.SHOWN -> true
				ChatFilterOption.COMPACT, ChatFilterOption.ACTION_BAR -> {

					val orbType = group("orb")
					val playerName = group("player")
					val healthAmount = group("health")
					val buffDetail = group("buff")

					val healthMessage = if(healthAmount.formatDouble() > 0.0) {
						Text.empty()
							.append(Text.literal("$healthAmount❤ ").setStyle(Style.EMPTY.withColor(Formatting.RED)))
							.append(Text.literal("and ").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))
					} else {
						Text.empty()
					}

					val formattedMessage = Text.empty()
						.append(Text.literal("HEALER ORB! ").setStyle(Style.EMPTY.withColor(Formatting.YELLOW).withBold(true)))
						.append(healthMessage)
						.append(Text.literal(buffDetail).setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
						.append(Text.literal(" for picking up $playerName's $orbType.").setStyle(Style.EMPTY.withColor(Formatting.GRAY)))

					ChatUtils.addMessage(formattedMessage, false, option == ChatFilterOption.ACTION_BAR)

					false
				}
				ChatFilterOption.HIDDEN -> false
			}
		}

		otherPickupUserOrbPattern.matchMatcher(message) {
			val option = config.healerOrbMessage
			return when(option) {
				ChatFilterOption.SHOWN -> true
				else -> false
			}
		}

		return true
	}

	private fun isEnabled() = IslandType.DUNGEONS.inIsland()
}