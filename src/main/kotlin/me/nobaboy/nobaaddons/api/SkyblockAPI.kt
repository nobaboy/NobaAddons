package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.ScoreboardUtils.cleanScoreboard
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseContains
import me.nobaboy.nobaaddons.utils.Utils
import net.hypixel.data.type.GameType
import net.hypixel.data.type.ServerType
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import java.util.regex.Pattern
import kotlin.jvm.optionals.getOrNull

object SkyblockAPI {
	private val currencyPattern = Pattern.compile("^[A-z]+: (?<currency>[\\d,]+).*")

	val inSkyblock: Boolean
		get() = Utils.onHypixel && currentGame == GameType.SKYBLOCK
	var currentIsland: IslandType = IslandType.UNKNOWN
		private set

	var currentGame: ServerType? = null
		private set

	var purse: Long? = null
	var bits: Long? = null

	fun isIn(island: IslandType): Boolean = inSkyblock && currentIsland == island
	fun IslandType.inIsland(): Boolean = inSkyblock && currentIsland == this

	// I originally planned to make an enum including all the zones but after realising that Skyblock has more than
	// 227 zones, which is what I counted, yea maybe not.
	fun inZone(zone: String): Boolean {
		if(!inSkyblock) return false

		val scoreboard = ScoreboardUtils.getSidebarLines()
		for(line in scoreboard) {
			val cleanedLine = line.cleanScoreboard()
			if(!cleanedLine.contains("⏣")) continue

			return cleanedLine.contains(zone)
		}
		return false
	}

	fun getPurse() {
		if(!inSkyblock) return

		val scoreboard = ScoreboardUtils.getSidebarLines()
		for(line in scoreboard) {
			val cleanedLine = line.cleanScoreboard()
			if(!cleanedLine.lowercaseContains(listOf("Purse:", "Piggy:"))) continue

			currencyPattern.matchMatcher(cleanedLine) {
				purse = group("currency").replace(",", "").toLongOrNull()
			}
		}
	}

	fun getBits() {
		if(!inSkyblock) return

		val scoreboard = ScoreboardUtils.getSidebarLines()
		for(line in scoreboard) {
			val cleanedLine = line.cleanScoreboard()
			if(!cleanedLine.contains("Bits:")) continue

			currencyPattern.matchMatcher(cleanedLine) {
				bits = group("currency").replace(",", "").toLongOrNull()
			}
		}
	}

	fun update() {
		getPurse()
		getBits()
	}

	fun onLocationPacket(packet: ClientboundLocationPacket) {
		currentGame = packet.serverType.getOrNull()
		currentIsland = packet.mode.map(IslandType::getIslandType).orElse(IslandType.UNKNOWN)
	}
}