package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.HypixelUtils
import me.nobaboy.nobaaddons.utils.ModAPIUtils.listen
import me.nobaboy.nobaaddons.utils.ModAPIUtils.subscribeToEvent
import me.nobaboy.nobaaddons.utils.RegexUtils.matchAll
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import net.hypixel.data.type.GameType
import net.hypixel.data.type.ServerType
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import java.util.regex.Pattern
import kotlin.jvm.optionals.getOrNull

object SkyBlockAPI {
	private val currencyPattern = Pattern.compile("^(?<currency>[A-z]+): (?<amount>[\\d,]+).*")

	var currentGame: ServerType? = null
		private set

	val inSkyBlock: Boolean
		get() = HypixelUtils.onHypixel && currentGame == GameType.SKYBLOCK
	var currentIsland: SkyBlockIsland = SkyBlockIsland.UNKNOWN
		private set
	var currentZone: String? = null
		private set

	val prefixedZone: String?
		get() = if(currentZone != null) "⏣ $currentZone" else null

	var purse: Long? = null
		private set
	var bits: Long? = null
		private set
//	var copper: Long? = null
//		private set
//	var motes: Long? = null
//		private set

	fun init() {
		SecondPassedEvent.EVENT.register { update() }
		HypixelModAPI.getInstance().subscribeToEvent<ClientboundLocationPacket>()
		HypixelModAPI.getInstance().listen<ClientboundLocationPacket>(SkyBlockAPI::onLocationPacket)
	}

	fun SkyBlockIsland.inIsland(): Boolean = inSkyBlock && currentIsland == this
	fun inZone(zone: String): Boolean = inSkyBlock && currentZone == zone

	// I originally planned to make an enum including all the zones but after realising
	// that Skyblock has more than 227 zones, which is what I counted, yea maybe not.
	private fun getZone() {
		val scoreboard = ScoreboardUtils.getSidebarLines()
		val line = scoreboard.firstOrNull { it.contains("⏣") }
		currentZone = line?.replace("⏣", "")?.trim() ?: return
	}

	// This can be further expanded to include other types like Pelts, North Stars, etc.
	private fun getCurrencies() {
		val scoreboard = ScoreboardUtils.getSidebarLines()
		currencyPattern.matchAll(scoreboard) {
			val currency = group("currency")
			val amount = group("amount").replace(",", "").toLongOrNull()

			when(currency) {
				"Bits" -> bits = amount
				"Purse", "Piggy" -> purse = amount
//				"Copper" -> copper = amount
//				"Motes" -> motes = amount
			}
		}
	}

	private fun update() {
		if(!inSkyBlock) return

		getZone()
		getCurrencies()
	}

	private fun onLocationPacket(packet: ClientboundLocationPacket) {
		currentGame = packet.serverType.getOrNull()
		currentIsland = packet.mode.map(SkyBlockIsland::getIslandType).orElse(SkyBlockIsland.UNKNOWN)
		if(currentIsland != SkyBlockIsland.UNKNOWN) SkyBlockEvents.ISLAND_CHANGE.invoke(SkyBlockEvents.IslandChange(currentIsland))
	}
}