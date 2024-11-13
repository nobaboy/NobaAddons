package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.core.Mayor
import me.nobaboy.nobaaddons.core.MayorPerk
import me.nobaboy.nobaaddons.data.InventoryData
import me.nobaboy.nobaaddons.data.jsonobjects.MayorJson
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.events.skyblock.SecondPassedEvent
import me.nobaboy.nobaaddons.utils.APIUtils
import me.nobaboy.nobaaddons.utils.APIUtils.fetchJson
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.SkyBlockTime
import me.nobaboy.nobaaddons.utils.SkyBlockTime.Companion.SKYBLOCK_YEAR_MILLIS
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.asTimestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.regex.Pattern
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

object MayorAPI {
	private const val ELECTION_API_URL = "${APIUtils.HYPIXEL_API_ROOT}/resources/skyblock/election"

	// SkyBlock elections end on the 27th of Late Spring
	private const val ELECTION_END_MONTH = 3
	private const val ELECTION_END_DAY = 27

	private const val electionOverMessage = "The election room is now closed. Clerk Serpahine is doing a final count of the votes..."
	private val mayorHeadPattern = Pattern.compile("Mayor (?<name>[A-z]+)")

	val foxyExtraEventPattern = Pattern.compile("Schedules an extra §.(?<event>[A-z ]+) §.event during the year\\.")

	var currentMayor: Mayor = Mayor.UNKNOWN
		private set
	var currentMinister: Mayor = Mayor.UNKNOWN
		private set

	var jerryMayor: Pair<Mayor, Timestamp> = Mayor.UNKNOWN to Timestamp.distantPast()
		private set

	private var lastMayor: Mayor? = null

	private var lastApiUpdate = Timestamp.distantPast()

	var nextMayorTimestamp = Timestamp.distantPast()
		private set

	fun init() {
		SecondPassedEvent.EVENT.register { handleSecondPassed() }
		InventoryEvents.READY.register(this::handleInventoryReady)
		ClientReceiveMessageEvents.GAME.register { message, _ -> handleChatEvent(message.string.cleanFormatting()) }
	}

	private fun handleSecondPassed() {
		if(!SkyBlockAPI.inSkyblock) return

		getCurrentMayor()
		getNextMayorTimestamp()

		if(!Mayor.JERRY.isElected()) return
		if(jerryMayor.first == Mayor.UNKNOWN || jerryMayor.second.isFuture()) return

		jerryMayor = Mayor.UNKNOWN to Timestamp.distantPast()

		val text = buildText {
			append("The Perokpocalypse Mayor has expired! Click ")
			append(Text.literal("HERE").formatted(Formatting.DARK_AQUA, Formatting.BOLD))
			append(" to update the new mayor.")
		}

		text.style = text.style.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, "calendar"))
		ChatUtils.addMessage(text)
	}

	private fun handleChatEvent(message: String) {
		if(!SkyBlockAPI.inSkyblock) return
		
		if(electionOverMessage == message) {
			lastMayor = currentMayor
			currentMayor = Mayor.UNKNOWN
			currentMinister = Mayor.UNKNOWN
		}
	}

	private fun handleInventoryReady(inventory: InventoryData) {
		if(!SkyBlockAPI.inSkyblock) return
		if(inventory.title != "Calendar and Events") return

		val item = inventory.items.values.firstOrNull {
			mayorHeadPattern.matchMatcher(it.name.string.cleanFormatting()) {
				group("name") == "Jerry"
			} == true
		} ?: return

		val lore = item.lore.lines.map { it.string.cleanFormatting() }

		val perk = lore.nextAfter("Perkpocalypse Perks:", 2) ?: return
		val extraMayor = Mayor.getMayor(MayorPerk.getPerk(perk) ?: return)?.activateAllPerks() ?: return

		val lastMayorTimestamp = nextMayorTimestamp - SKYBLOCK_YEAR_MILLIS.milliseconds

		// The maximum amount of extra mayors we get while Jerry is elected is 21
		val expirationTime = (1..21)
			.map { lastMayorTimestamp + (6.hours * it) }
			.firstOrNull { it.isFuture() }
			?.coerceAtMost(nextMayorTimestamp) ?: return

		jerryMayor = extraMayor to expirationTime
	}

	fun Mayor.isElected(): Boolean = currentMayor == this

	private fun getCurrentMayor() {
		if(lastApiUpdate.elapsedSince() < 20.minutes) return
		lastApiUpdate = Timestamp.now()

		fetchJson<MayorJson>(ELECTION_API_URL).thenAccept { mayorJson ->
			val mayor = mayorJson.mayor

			val currentMayorName = mayor.name
			if(lastMayor?.name != currentMayorName) {
				MayorPerk.disableAll()
				currentMayor = Mayor.getMayor(currentMayorName, mayor.perks)
				currentMinister = mayor.minister?.let { Mayor.getMayor(it.name, listOf(it.perk)) } ?: Mayor.UNKNOWN
			}
		}
	}

	private fun getNextMayorTimestamp() {
		val now = SkyBlockTime.now()
		nextMayorTimestamp = SkyBlockTime(now.getElectionYear() + 1, ELECTION_END_MONTH, day = ELECTION_END_DAY).asTimestamp()
	}

	fun SkyBlockTime.getElectionYear(): Int {
		var mayorYear = year

		if(month < ELECTION_END_MONTH || (day < ELECTION_END_DAY && month == ELECTION_END_MONTH)) mayorYear--
		return mayorYear
	}

	// TODO: Move this to a utility class once one is made
	private fun List<String>.nextAfter(after: String, skip: Int = 1): String? {
		val index = this.indexOf(after)
		if(index == -1) return null
 		return this.getOrNull(index + skip)
	}
}