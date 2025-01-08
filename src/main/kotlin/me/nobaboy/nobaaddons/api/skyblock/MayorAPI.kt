package me.nobaboy.nobaaddons.api.skyblock

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.core.mayor.Mayor
import me.nobaboy.nobaaddons.core.mayor.MayorPerk
import me.nobaboy.nobaaddons.data.json.MayorJson
import me.nobaboy.nobaaddons.events.ChatMessageEvents
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.HTTPUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.mapFullMatch
import me.nobaboy.nobaaddons.utils.SkyBlockTime
import me.nobaboy.nobaaddons.utils.SkyBlockTime.Companion.SKYBLOCK_YEAR_MILLIS
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.runCommand
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.asTimestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

object MayorAPI {
	private const val ELECTION_API_URL = "https://api.hypixel.net/v2/resources/skyblock/election"

	// SkyBlock elections end on the 27th of Late Spring
	private const val ELECTION_END_MONTH = 3
	private const val ELECTION_END_DAY = 27

	private val electionEndMessage by "The election room is now closed. Clerk Seraphine is doing a final count of the votes...".fromRepo("mayor.election_end")
	private val mayorHeadPattern by Regex("Mayor (?<name>[A-z]+)").fromRepo("mayor.skull_item")

	val foxyExtraEventPattern by Regex("Schedules an extra §.(?<event>[A-z ]+) §.event during the year\\.").fromRepo("mayor.foxy_event")

	var currentMayor: Mayor = Mayor.UNKNOWN
		private set
	var currentMinister: Mayor = Mayor.UNKNOWN
		private set

	var jerryMayor: Pair<Mayor, Timestamp> = Mayor.UNKNOWN to Timestamp.distantPast()
		private set

	private var lastMayor: Mayor? = null

	private var lastApiUpdate = Timestamp.distantPast()
	private val shouldUpdateMayor: Boolean
		get() = lastApiUpdate.elapsedSince() > 20.minutes

	var nextMayorTimestamp = Timestamp.distantPast()
		private set

	fun init() {
		SecondPassedEvent.EVENT.register { onSecondPassed() }
		InventoryEvents.OPEN.register(this::onInventoryOpen)
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onSecondPassed() {
		if(!SkyBlockAPI.inSkyBlock) return

		if(shouldUpdateMayor) {
			NobaAddons.runAsync { getCurrentMayor() }
		}
		getNextMayorTimestamp()

		if(!Mayor.JERRY.isElected()) return
		if(jerryMayor.first == Mayor.UNKNOWN || jerryMayor.second.isFuture()) return

		jerryMayor = Mayor.UNKNOWN to Timestamp.distantPast()

		ChatUtils.addMessage(tr("nobaaddons.mayorApi.jerryMayorExpired", "The Perkpocalypse mayor has expired! Click here to get the new mayor").runCommand("/calendar"))
	}

	private fun onInventoryOpen(event: InventoryEvents.Open) {
		if(!SkyBlockAPI.inSkyBlock) return
		if(event.inventory.title != "Calendar and Events") return

		val item = event.inventory.items.values.firstOrNull {
			mayorHeadPattern.mapFullMatch(it.name.string.cleanFormatting()) {
				groups["name"]?.value == "Jerry"
			} == true
		} ?: return

		val lore = item.lore.lines.map { it.string.cleanFormatting() }

		val perk = lore.nextAfter("Perkpocalypse Perks:", 2) ?: return
		val extraMayor = Mayor.getByPerk(MayorPerk.getByName(perk) ?: return)?.activateAllPerks() ?: return

		val lastMayorTimestamp = nextMayorTimestamp - SKYBLOCK_YEAR_MILLIS.milliseconds

		// The maximum amount of extra mayors we get while Jerry is elected is 21
		val expirationTime = (1..21).map { lastMayorTimestamp + (6.hours * it) }
			.firstOrNull { it.isFuture() }
			?.coerceAtMost(nextMayorTimestamp) ?: return

		jerryMayor = extraMayor to expirationTime
	}

	private fun onChatMessage(message: String) {
		if(!SkyBlockAPI.inSkyBlock) return
		
		if(electionEndMessage == message) {
			lastMayor = currentMayor
			currentMayor = Mayor.UNKNOWN
			currentMinister = Mayor.UNKNOWN
		}
	}

	fun Mayor.isElected(): Boolean = currentMayor == this

	private suspend fun getCurrentMayor() {
		if(!shouldUpdateMayor) return
		lastApiUpdate = Timestamp.now()

		val mayorJson = HTTPUtils.fetchJson<MayorJson>(ELECTION_API_URL).await()
		val mayor = mayorJson.mayor

		val currentMayorName = mayor.name
		if(lastMayor?.name != currentMayorName) {
			MayorPerk.disableAll()
			currentMayor = Mayor.getMayor(currentMayorName, mayor.perks)
			currentMinister = mayor.minister?.let { Mayor.getMayor(it.name, listOf(it.perk)) } ?: Mayor.UNKNOWN
		}
	}

	private fun getNextMayorTimestamp() {
		val now = SkyBlockTime.now()
		nextMayorTimestamp = SkyBlockTime(now.getElectionYear() + 1, ELECTION_END_MONTH, day = ELECTION_END_DAY).asTimestamp()
	}

	private fun SkyBlockTime.getElectionYear(): Int {
		var mayorYear = year

		if(month < ELECTION_END_MONTH || (day < ELECTION_END_DAY && month == ELECTION_END_MONTH)) mayorYear--
		return mayorYear
	}

	private fun List<String>.nextAfter(after: String, skip: Int = 1): String? {
		val index = this.indexOf(after)
		if(index == -1) return null
		return this.getOrNull(index + skip)
	}
}