package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.HypixelUtils
import me.nobaboy.nobaaddons.utils.ModAPIUtils.listen
import me.nobaboy.nobaaddons.utils.ModAPIUtils.subscribeToEvent
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.forEachFullMatch
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.hypixel.data.type.GameType
import net.hypixel.data.type.ServerType
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import kotlin.jvm.optionals.getOrNull

object SkyBlockAPI {
	private val levelPattern by Regex("").fromRepo("skyblock.level")
	private val xpPattern by Regex("").fromRepo("skyblock.xp")
	private val currencyPattern by Regex("^(?<currency>[A-z]+): (?<amount>[\\d,]+).*").fromRepo("skyblock.currency")
	private val zonePattern by Regex("^[⏣ф] (?<zone>[A-z-'\" ]+)(?: ൠ x\\d)?\$").fromRepo("skyblock.zone")

	var currentGame: ServerType? = null
		private set

	@get:JvmStatic
	@get:JvmName("inSkyBlock")
	val inSkyBlock: Boolean
		get() = HypixelUtils.onHypixel && currentGame == GameType.SKYBLOCK
	var currentIsland: SkyBlockIsland = SkyBlockIsland.UNKNOWN
		private set
	var currentZone: String? = null
		private set

	val prefixedZone: String?
		get() = currentZone?.let {
			val symbol = if(currentIsland == SkyBlockIsland.RIFT) "ф" else "⏣"
			"$symbol $it"
		}

	var level: Int? = null
		private set
	var xp: Int? = null
		private set

	var coins: Long? = null
		private set
	var bits: Long? = null
		private set

	fun SkyBlockIsland.inIsland(): Boolean = inSkyBlock && currentIsland == this
	fun inZone(zone: String): Boolean = inSkyBlock && currentZone == zone

	fun init() {
		SecondPassedEvent.EVENT.register { onSecondPassed() }
		InventoryEvents.OPEN.register(this::onInventoryOpen)
		HypixelModAPI.getInstance().subscribeToEvent<ClientboundLocationPacket>()
		HypixelModAPI.getInstance().listen<ClientboundLocationPacket>(SkyBlockAPI::onLocationPacket)
	}

	private fun onSecondPassed() {
		if(!inSkyBlock) return

		update()
	}

	private fun onInventoryOpen(event: InventoryEvents.Open) {
		if(event.inventory.title != "SkyBlock Menu") return

		val itemStack = event.inventory.items.values.firstOrNull {
			it.name.string == "SkyBlock Leveling"
		} ?: return

		val lore = itemStack.lore.stringLines

		levelPattern.firstFullMatch(lore) {
			level = groups["level"]?.value?.toInt()
		}

		xpPattern.firstFullMatch(lore) {
			xp = groups["xp"]?.value?.toInt()
		}
	}

	private fun onLocationPacket(packet: ClientboundLocationPacket) {
		currentGame = packet.serverType.getOrNull()
		currentIsland = packet.mode.map(SkyBlockIsland::getSkyBlockIsland).orElse(SkyBlockIsland.UNKNOWN)
		if(currentIsland != SkyBlockIsland.UNKNOWN) SkyBlockEvents.ISLAND_CHANGE.invoke(SkyBlockEvents.IslandChange(currentIsland))
	}

	private fun update() {
		val scoreboard = ScoreboardUtils.getScoreboardLines()

		// I originally planned to make an enum including all the zones but after realising
		// that Skyblock has more than 227 zones, which is what I counted, yea maybe not.
		zonePattern.firstFullMatch(scoreboard) {
			currentZone = groups["zone"]?.value
		}

		// This can be further expanded to include other types like Pelts, North Stars, etc.
		currencyPattern.forEachFullMatch(scoreboard) {
			val currency = groups["currency"]?.value ?: return@forEachFullMatch
			val amount = (groups["amount"]?.value ?: "0").replace(",", "").toLongOrNull()

			when(currency) {
				"Purse", "Piggy" -> coins = amount
				"Bits" -> bits = amount
			}
		}
	}
}