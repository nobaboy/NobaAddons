package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.utils.RegexUtils.matchAll
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.ScoreboardUtils.cleanScoreboard
import me.nobaboy.nobaaddons.utils.Utils
import net.hypixel.data.type.GameType
import net.hypixel.data.type.ServerType
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import java.util.regex.Pattern
import kotlin.jvm.optionals.getOrNull

object SkyBlockAPI {
	private val currencyPattern = Pattern.compile("^(?<currency>[A-z]+): (?<amount>[\\d,]+).*")

	var currentGame: ServerType? = null
		private set

	val inSkyblock: Boolean
		get() = Utils.onHypixel && currentGame == GameType.SKYBLOCK
	var currentIsland: IslandType = IslandType.UNKNOWN
		private set
	var currentZone: String? = null
		private set

	var purse: Long? = null
	var bits: Long? = null
//	var copper: Long? = null
//	var motes: Long? = null

	fun IslandType.inIsland(): Boolean = inSkyblock && currentIsland == this
	fun inZone(zone: String): Boolean = inSkyblock && currentZone == zone

	// I originally planned to make an enum including all the zones but after realising
	// that Skyblock has more than 227 zones, which is what I counted, yea maybe not.
	fun getZone() {
		if(!inSkyblock) return

		val scoreboard = ScoreboardUtils.getSidebarLines()
		val line = scoreboard.firstOrNull { it.contains("⏣")}
		currentZone = line?.replace("⏣", "")?.trim() ?: return
	}

	// This can be further expanded to include other types like Pelts, North Stars, etc.
	fun getCurrencies() {
		if(!inSkyblock) return

		val scoreboard = ScoreboardUtils.getSidebarLines()
		currencyPattern.matchAll(scoreboard) {
			val currency = group("currency")
			val amount = group("amount").replace(",", "").toLongOrNull()

			when (currency) {
				"Bits" -> bits = amount
				"Purse", "Piggy" -> purse = amount
//				"Copper" -> copper = amount
//				"Motes" -> motes = amount
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
		if(!inSkyblock) return

		getZone()
		getCurrencies()
	}

	fun onLocationPacket(packet: ClientboundLocationPacket) {
		currentGame = packet.serverType.getOrNull()
		currentIsland = packet.mode.map(IslandType::getIslandType).orElse(IslandType.UNKNOWN)
	}
}