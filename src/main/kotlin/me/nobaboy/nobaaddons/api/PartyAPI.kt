package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.api.party.IParty
import me.nobaboy.nobaaddons.api.party.PartySnapshot
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.forEachMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatchers
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.Utils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import java.util.regex.Pattern

// NOTE: The mod API isn't being used here as it returns UUIDs, while we want usernames
object PartyAPI : IParty {
	private val rankPrefix = Regex("\\[[A-Z+]+] ")

	// Client Player Patterns
	private val clientPlayerJoinPattern = Pattern.compile("^You have joined (?:\\[[A-Z+]+] )?(?<leader>[A-z0-9_]+)'s party!")
	private val clientPlayerKickedPattern = Pattern.compile("^You have been kicked from the party by (?:\\[[A-Z+]+] )?(?<former>[A-z0-9_]+)")
	private val clientPlayerLeftPatterns by lazy { listOf(clientPlayerKickedPattern, partyDisbandPattern) }
	private val clientPlayerLeaveMessages: Array<String> = arrayOf(
		"You left the party.",
		"The party was disbanded because all invites expired and the party was empty.",
		"You are not currently in a party.",
	)

	// Other Player Patterns
	private val otherPlayerJoinPattern = Pattern.compile("^(?:\\[A-Z+]+] )?(?<name>[A-z0-9_]+) joined the party\\.")
	private val otherPlayersInPartyPattern = Pattern.compile("^You'll be partying with: (?<names>.*)")
	private val otherPlayerLeftPattern = Pattern.compile("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) left the party\\.")
	private val otherPlayerKickedPattern = Pattern.compile("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) has been removed from the party\\.")
	private val otherPlayerKickedOfflinePattern = Pattern.compile("^Kicked (?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) because they were offline\\.")
	private val otherPlayerDisconnectPattern = Pattern.compile("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) was removed from your party because they disconnected\\.")
	private val otherPlayerLeftPatterns = listOf(otherPlayerLeftPattern, otherPlayerKickedPattern, otherPlayerKickedOfflinePattern, otherPlayerDisconnectPattern)

	// Party Finder
	private val partyFinderJoinPattern = Pattern.compile("^Party Finder > (?<name>[A-z0-9_]+) joined the (?:dungeon )?group! \\([A-z0-9 ]+\\)")

	// Party General
	private val partyMembersListPattern = Pattern.compile("^Party (?<type>Leader|Moderators|Members): (?<names>.*)")
	private val partyDisbandPattern = Pattern.compile("^(?:\\[[A-Z+]+] )?(?<former>[A-z0-9_]+) has disbanded the party!")
	private val transferByPlayerPattern = Pattern.compile(
		"^The party was transferred to (?:\\[[A-Z+]+] )?(?<newLeader>[A-z0-9_]+) by (?:\\[[A-Z+]+] )?(?<formerLeader>[A-z0-9_]+)"
	)
	private val transferOnLeavePattern = Pattern.compile(
		"^The party was transferred to (?:\\[[A-Z+]+] )?(?<newLeader>[A-z0-9_]+) because (?:\\[[A-Z+]+] )?(?<formerLeader>[A-z0-9_]+) left"
	)

	// Party Misc
	private val partyChatPattern = Pattern.compile("^Party > (?:\\[[A-Z+]+ )?(?<name>[A-z0-9_]+): .*")
	private val partyListPattern = Pattern.compile(
		"^-{53}|^Party Members \\([0-9]+\\)|^Party (?<type>Leader|Moderators|Members): (?<names>.*)|^You are not currently in a party\\."
	)

	private var storedPartyList = mutableListOf<String>()
	private var gotList = false

	override var inParty: Boolean = false
	override var partyLeader: String? = null
	override var partyMembers = mutableListOf<String>()

	fun init() {
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, _ -> handleChatEvent(message.string.cleanFormatting()) }
		ClientPlayConnectionEvents.JOIN.register { _, _, _ -> if(!gotList) requestPartyList() }
		ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> clear() }
	}

	fun requestPartyList() {
		gotList = true
		Scheduler.schedule(5 * 20) {
			if(!Utils.onHypixel) return@schedule
			HypixelCommands.partyList()
		}
		Scheduler.schedule(7 * 20) {
			processPartyList()
		}
	}

	fun clear() {
		partyLeft()
		storedPartyList.clear()
		gotList = false
	}

	private fun processPartyList() {
		storedPartyList.forEachMatch(partyMembersListPattern) {
			inParty = true

			val type = group("type")
			val names = group("names")

			addPlayersToList(type == "Leader", names)
		}
		storedPartyList.clear()
	}

	override val isLeader get() = partyLeader == MCUtils.playerName

	fun listMembers() {
		val partySize = partyMembers.size
		if(partySize == 0) {
			ChatUtils.addMessage("Party seems to be empty...")
		} else {
			ChatUtils.addMessage("Party Members ($partySize):")
			for(member in partyMembers) {
				val isMemberLeader = if(partyLeader.equals(member)) "§9(Leader)" else ""
				ChatUtils.addMessage(" §b- §7$member $isMemberLeader", false)
			}
		}
	}

	private fun handleChatEvent(message: String): Boolean {
		if(!gotList) {
			partyListPattern.matchMatcher(message) {
				storedPartyList.add(message)
				return false
			}
		}

		partyChatPattern.matchMatcher(message) {
			val name = group("name")
			addPlayer(name)
		}

		// Member Join
		clientPlayerJoinPattern.matchMatcher(message) {
			val name = group("leader")
			partyLeader = name
			addPlayer(name)
		}
		otherPlayerJoinPattern.matchMatcher(message) {
			val name = group("name")
			if(partyMembers.size == 1) {
				partyLeader = MCUtils.playerName
			}
			addPlayer(name)
		}
		partyFinderJoinPattern.matchMatcher(message) {
			val name = group("name")
			addPlayer(name)
		}

		// Member Leave
		otherPlayerLeftPatterns.matchMatchers(message) {
			val name = group("name")
			removePlayer(name)
		}
		transferOnLeavePattern.matchMatcher(message) {
			val formerLeader = group("formerLeader")
			partyLeader = group("newLeader")
			partyMembers.remove(formerLeader)
			inParty = true
		}
		transferByPlayerPattern.matchMatcher(message) {
			partyLeader = group("newLeader")
			inParty = true
		}

		// Party Disband
		clientPlayerLeftPatterns.matchMatchers(message) {
			partyLeft()
		}
		if(message in clientPlayerLeaveMessages) {
			partyLeft()
		}

		// Party Members
		otherPlayersInPartyPattern.matchMatcher(message) {
			group("names").replace(rankPrefix, "").split(", ").forEach(this@PartyAPI::addPlayer)
		}

		// Party List
		partyMembersListPattern.matchMatcher(message) {
			inParty = true

			val type = group("type")
			val names = group("names")

			addPlayersToList(type == "Leader", names)
		}

		return true
	}

	private fun addPlayersToList(isPartyLeader: Boolean, names: String) {
		inParty = true

		for(name in names.split(" ● ")) {
			name.replace(" ●", "")
				.replace(rankPrefix, "")
				.split(" ")
				.filter { !it.isBlank() }
				.forEach {
					addPlayer(it)
					if(isPartyLeader) partyLeader = it
				}
		}
	}

	private fun addPlayer(playerName: String) {
		if(partyMembers.contains(playerName)) return
		partyMembers.add(playerName)
		inParty = true
	}

	private fun removePlayer(playerName: String) {
		partyMembers.remove(playerName)
	}

	private fun partyLeft() {
		partyMembers.clear()
		partyLeader = null
		inParty = false
	}

	fun snapshot(): PartySnapshot = PartySnapshot(inParty, partyLeader, partyMembers.toMutableList(), isLeader)
}