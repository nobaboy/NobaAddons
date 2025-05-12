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
import me.nobaboy.nobaaddons.utils.RegexUtils.getGroupFromFirstFullMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.getGroupFromFullMatch
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.SkyBlockSeason
import me.nobaboy.nobaaddons.utils.SkyBlockTime
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import net.hypixel.data.type.GameType
import net.hypixel.data.type.ServerType
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import java.util.UUID
import kotlin.jvm.optionals.getOrNull
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.toJavaUuid
import kotlin.uuid.toKotlinUuid

object SkyBlockAPI {
	private val PROFILE_ID_REGEX by Regex("^Profile ID: (?<id>${CommonPatterns.UUID_PATTERN_STRING})").fromRepo("skyblock.profile_id")
	private val PROFILE_TYPE_REGEX by Regex("^. (?<type>Ironman|Stranded|Bingo)").fromRepo("skyblock.profile_type")

	private val SKYBLOCK_LEVEL_REGEX by Regex("^Your SkyBlock Level: \\[(?<level>\\d+)]").fromRepo("skyblock.level")
	private val SKYBLOCK_XP_REGEX by Regex("^\\s+(?<xp>\\d+)/100 XP").fromRepo("skyblock.xp")

	private val ZONE_REGEX by Regex("^[⏣ф] (?<zone>[A-z-'\" ]+)(?: .*)?\$").fromRepo("skyblock.zone")
	private val CURRENCY_REGEX by Regex("^(?<currency>[A-z]+): (?<amount>[\\d,]+).*").fromRepo("skyblock.currency")

	var currentServer: ServerType? = null
		private set

	@get:JvmStatic
	@get:JvmName("inSkyBlock")
	val inSkyBlock: Boolean
		get() = HypixelUtils.onHypixel && currentServer == GameType.SKYBLOCK

	@OptIn(ExperimentalUuidApi::class)
	var currentProfile: UUID? = null
		private set(value) {
			field = value
			PersistentCache.lastProfile = value?.toKotlinUuid()
		}
	var profileType: SkyBlockProfile = SkyBlockProfile.CLASSIC
		private set

	var level: Int? = null
		private set
	var xp: Int? = null
		private set

	var currentIsland: SkyBlockIsland = SkyBlockIsland.UNKNOWN
		private set
	var currentSeason: SkyBlockSeason? = null
		private set

	var currentZone: String? = null
		private set
	val prefixedZone: String?
		get() = currentZone?.let {
			val symbol = if(currentIsland == SkyBlockIsland.RIFT) "ф" else "⏣"
			"$symbol $it"
		}

	var coins: Long? = null
		private set
	var bits: Long? = null
		private set

	fun SkyBlockIsland.inIsland(): Boolean = inSkyBlock && (profileType == SkyBlockProfile.STRANDED || currentIsland == this)
	fun SkyBlockSeason.isSeason(): Boolean = inSkyBlock && currentSeason == this
	fun inZone(zone: String): Boolean = inSkyBlock && currentZone == zone

	init {
		HypixelModAPI.getInstance().subscribeToEvent<ClientboundLocationPacket>()
		HypixelModAPI.getInstance().listen<ClientboundLocationPacket>(SkyBlockAPI::onLocationPacket)
		TickEvents.everySecond { update() }
		InventoryEvents.OPEN.register(this::onInventoryOpen)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		@OptIn(ExperimentalUuidApi::class)
		currentProfile = PersistentCache.lastProfile?.toJavaUuid()
	}

	private fun onLocationPacket(packet: ClientboundLocationPacket) {
		currentServer = packet.serverType.getOrNull()
		val newIsland = packet.mode.map(SkyBlockIsland::getByName).orElse(SkyBlockIsland.UNKNOWN)

		if(newIsland != currentIsland) Scheduler.schedule(2) {
			SkyBlockEvents.ISLAND_CHANGE.dispatch(SkyBlockEvents.IslandChange(newIsland))
			currentIsland = newIsland
		}
	}

	private fun onInventoryOpen(event: InventoryEvents.Open) {
		if(event.inventory.title != "SkyBlock Menu") return

		val itemStack = event.inventory.items.values.firstOrNull {
			it.name.string == "SkyBlock Leveling"
		} ?: return

		val lore = itemStack.lore.stringLines

		SKYBLOCK_LEVEL_REGEX.firstFullMatch(lore) {
			level = groups["level"]?.value?.toInt()
		}

		SKYBLOCK_XP_REGEX.firstFullMatch(lore) {
			xp = groups["xp"]?.value?.toInt()
		}
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		val profileId = UUID.fromString(PROFILE_ID_REGEX.getGroupFromFullMatch(event.cleaned, "id") ?: return)
		if(profileId == currentProfile) return

		SkyBlockEvents.PROFILE_CHANGE.dispatch(SkyBlockEvents.ProfileChange(profileId))
		currentProfile = profileId
	}

	private fun update() {
		if(!inSkyBlock) return

		val month = SkyBlockTime.now().month
		currentSeason = SkyBlockSeason.entries[(month - 1) / 3]

		val scoreboard = ScoreboardUtils.getScoreboardLines()

		val newProfileType = PROFILE_TYPE_REGEX.getGroupFromFirstFullMatch(scoreboard, "type")
		profileType = newProfileType?.let { SkyBlockProfile.getByName(it) } ?: SkyBlockProfile.CLASSIC

		// I originally planned to make an enum including all the zones but after realising
		// that Skyblock has more than 227 zones, which is what I counted, yea maybe not.
		ZONE_REGEX.firstFullMatch(scoreboard) {
			currentZone = groups["zone"]?.value
		}

		// This can be further expanded to include other types like Pelts, North Stars, etc.
		CURRENCY_REGEX.forEachFullMatch(scoreboard) {
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