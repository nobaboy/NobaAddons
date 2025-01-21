package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.core.PersistentCache
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.SkyBlockProfile
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.InventoryEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.HypixelUtils
import me.nobaboy.nobaaddons.utils.ModAPIUtils.listen
import me.nobaboy.nobaaddons.utils.ModAPIUtils.subscribeToEvent
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.forEachFullMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.getGroupFromFullMatch
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.SkyBlockSeason
import me.nobaboy.nobaaddons.utils.SkyBlockTime
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.hypixel.data.type.GameType
import net.hypixel.data.type.ServerType
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import java.util.UUID
import kotlin.jvm.optionals.getOrNull

object SkyBlockAPI {
	private val profileTypePattern by Regex("^. (?<type>Ironman|Stranded|Bingo)").fromRepo("skyblock.profile_type")
	private val profileIdPattern by Regex("^Profile ID: (?<id>${CommonPatterns.UUID_PATTERN})").fromRepo("skyblock.profile_id")
	private val levelPattern by Regex("^Your SkyBlock Level: \\[(?<level>\\d+)]").fromRepo("skyblock.level")
	private val xpPattern by Regex("^\\s+(?<xp>\\d+)/100 XP").fromRepo("skyblock.xp")
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
	var currentSeason: SkyBlockSeason? = null
		private set
	var currentZone: String? = null
		private set

	var currentProfile: UUID? = null
		private set(value) {
			field = value
			PersistentCache.lastProfile = value
		}

	var profileType: SkyBlockProfile = SkyBlockProfile.CLASSIC
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

	fun SkyBlockIsland.inIsland(): Boolean = profileType == SkyBlockProfile.STRANDED || inSkyBlock && currentIsland == this
	fun SkyBlockSeason.isSeason(): Boolean = inSkyBlock && currentSeason == this
	fun inZone(zone: String): Boolean = inSkyBlock && currentZone == zone

	fun init() {
		TickEvents.everySecond { update() }
		InventoryEvents.OPEN.register(this::onInventoryOpen)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		HypixelModAPI.getInstance().subscribeToEvent<ClientboundLocationPacket>()
		HypixelModAPI.getInstance().listen<ClientboundLocationPacket>(SkyBlockAPI::onLocationPacket)
		currentProfile = PersistentCache.lastProfile
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		val profileId = UUID.fromString(profileIdPattern.getGroupFromFullMatch(event.message.string.cleanFormatting(), "id") ?: return)
		if(profileId != currentProfile) {
			currentProfile = profileId
			SkyBlockEvents.PROFILE_CHANGE.invoke(SkyBlockEvents.ProfileChange(profileId))
		}
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
		if(!inSkyBlock) return

		val month = SkyBlockTime.now().month
		currentSeason = SkyBlockSeason.entries[(month - 1) / 3]

		val scoreboard = ScoreboardUtils.getScoreboardLines()

		// I originally planned to make an enum including all the zones but after realising
		// that Skyblock has more than 227 zones, which is what I counted, yea maybe not.
		zonePattern.firstFullMatch(scoreboard) {
			currentZone = groups["zone"]?.value
		}

		profileTypePattern.firstFullMatch(scoreboard) {
			val type = groups["type"]?.value ?: return@firstFullMatch
			profileType = SkyBlockProfile.getByName(type)
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

	fun getSkyBlockLevelColor(level: Int? = null): NobaColor {
		val level = level ?: this.level ?: 0

		return when(level) {
			in 0 until 40 -> NobaColor.GRAY
			in 40 until 80 -> NobaColor.WHITE
			in 80 until 120 -> NobaColor.YELLOW
			in 120 until 160 -> NobaColor.GREEN
			in 160 until 200 -> NobaColor.DARK_GREEN
			in 200 until 240 -> NobaColor.AQUA
			in 240 until 280 -> NobaColor.DARK_AQUA
			in 280 until 320 -> NobaColor.BLUE
			in 320 until 360 -> NobaColor.LIGHT_PURPLE
			in 360 until 400 -> NobaColor.DARK_PURPLE
			in 400 until 440 -> NobaColor.GOLD
			in 440 until 480 -> NobaColor.RED
			in 480 until 520 -> NobaColor.DARK_RED
			else -> NobaColor.BLACK
		}
	}
}