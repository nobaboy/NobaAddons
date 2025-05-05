package me.nobaboy.nobaaddons.api.skyblock

import kotlinx.datetime.Instant
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.core.mayor.Mayor
import me.nobaboy.nobaaddons.core.mayor.MayorPerk
import me.nobaboy.nobaaddons.data.json.Election
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.InventoryEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.collections.CollectionUtils.nextAfter
import me.nobaboy.nobaaddons.utils.HTTPUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.getGroupFromFullMatch
import me.nobaboy.nobaaddons.utils.hypixel.SkyBlockTime
import me.nobaboy.nobaaddons.utils.hypixel.SkyBlockTime.Companion.SKYBLOCK_YEAR_MILLIS
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.mc.TextUtils.runCommand
import me.nobaboy.nobaaddons.utils.TimeUtils.elapsedSince
import me.nobaboy.nobaaddons.utils.TimeUtils.isFuture
import me.nobaboy.nobaaddons.utils.TimeUtils.now
import me.nobaboy.nobaaddons.utils.mc.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import me.nobaboy.nobaaddons.utils.tr
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

// TODO: Use repo for mayor descriptions except for Foxy
object MayorAPI {
	private const val ELECTION_API_URL = "https://api.hypixel.net/v2/resources/skyblock/election"

	private const val ELECTION_END_MONTH = 3
	private const val ELECTION_END_DAY = 27

	private val MAYOR_NAME_REGEX by Regex("Mayor (?<name>[A-z]+)").fromRepo("mayor.name")
	private val ELECTION_END_MESSAGE by "The election room is now closed. Clerk Seraphine is doing a final count of the votes...".fromRepo("mayor.election_end")

	val FOXY_EVENT_REGEX by Regex("Schedules an extra §.(?<event>[A-z ]+) §.event during the year\\.").fromRepo("mayor.foxy_event")

	var currentMayor: ActiveMayor = Mayor.UNKNOWN.withNone()
		private set
	var currentMinister: ActiveMayor = Mayor.UNKNOWN.withNone()

	var jerryMayor: Pair<ActiveMayor, Instant> = Mayor.UNKNOWN.withNone() to Instant.DISTANT_PAST
		private set

	private var lastMayor: ActiveMayor? = null
	private var nextMayorTimestamp = Instant.DISTANT_PAST

	private var lastUpdate = Instant.DISTANT_PAST
	private val shouldUpdate: Boolean get() = lastUpdate.elapsedSince() > 20.minutes

	fun Mayor.isElected(): Boolean = currentMayor.mayor == this
	fun MayorPerk.isActive(): Boolean = (currentMayor.perks + currentMinister.perks).contains(this)

	private fun SkyBlockTime.getElectionYear(): Int =
		year - if(month < ELECTION_END_MONTH || (month == ELECTION_END_MONTH && day < ELECTION_END_DAY)) 1 else 0


	fun init() {
		TickEvents.everySecond { onSecondPassed() }
		InventoryEvents.OPEN.register(this::onInventoryOpen)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
	}

	private fun onSecondPassed() {
		if(!SkyBlockAPI.inSkyBlock) return

		if(shouldUpdate) NobaAddons.runAsync { getCurrentMayor() }
		nextMayorTimestamp = SkyBlockTime(SkyBlockTime.now().getElectionYear() + 1, ELECTION_END_MONTH, ELECTION_END_DAY).toInstant()

		if(!Mayor.JERRY.isElected()) return
		if(jerryMayor.first.mayor == Mayor.UNKNOWN) return
		if(jerryMayor.second.isFuture()) return

		jerryMayor = Mayor.UNKNOWN.withNone() to Instant.DISTANT_PAST
		ChatUtils.addMessage(tr("nobaaddons.mayorApi.jerryMayorExpired", "The Perkpocalypse Mayor has expired! Click here to get the new mayor.").runCommand("/calendar"))
	}

	private fun onInventoryOpen(event: InventoryEvents.Open) {
		if(!SkyBlockAPI.inSkyBlock) return
		if(event.inventory.title != "Calendar and Events") return

		val item = event.inventory.items.values.firstOrNull {
			MAYOR_NAME_REGEX.getGroupFromFullMatch(it.name.string.cleanFormatting(), "name") == "Jerry"
		} ?: return

		val lore = item.lore.stringLines

		val perk = lore.nextAfter("Perkpocalypse Perks:", 2) ?: return
		val perkpocalypseMayor = Mayor.getByPerk(MayorPerk.getByName(perk) ?: return)?.withAll() ?: return

		val lastMayorTimestamp = nextMayorTimestamp - SKYBLOCK_YEAR_MILLIS.milliseconds

		val expirationTime = (1..21).map { lastMayorTimestamp + (6.hours * it) }
			.firstOrNull { it.isFuture() }
			?.coerceAtMost(nextMayorTimestamp) ?: return

		jerryMayor = perkpocalypseMayor to expirationTime
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!SkyBlockAPI.inSkyBlock) return

		if(event.cleaned == ELECTION_END_MESSAGE) {
			lastMayor = currentMayor
			currentMayor = Mayor.UNKNOWN.withNone()
			currentMinister = Mayor.UNKNOWN.withNone()
		}
	}

	private suspend fun getCurrentMayor() {
		lastUpdate = Instant.now

		val election = HTTPUtils.fetchJson<Election>(ELECTION_API_URL).await()
		val mayor = election.mayor
		if(lastMayor?.displayName == mayor.name) return

		currentMayor = Mayor.getByName(mayor.name)?.with(mayor.perks) ?: Mayor.UNKNOWN.withNone()
		currentMinister = mayor.minister?.let { Mayor.getByName(it.name)?.with(listOf(it.perk)) } ?: Mayor.UNKNOWN.withNone()
	}

	data class ActiveMayor(val mayor: Mayor, val perks: List<MayorPerk>) {
		val displayName: String get() = mayor.displayName
	}
}