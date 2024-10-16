package me.nobaboy.nobaaddons.api

import com.mojang.brigadier.Command
import me.nobaboy.nobaaddons.api.party.IParty
import me.nobaboy.nobaaddons.api.party.PartySnapshot
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
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
	// User Patterns
	private val userPartyJoinPattern = Pattern.compile("^You have joined (?:\\[[A-Z+]+] )?(?<leader>[A-z0-9_]+)'s party!")
	private val userKickedPattern = Pattern.compile("^You have been kicked from the party by (?:\\[[A-Z+]+] )?(?<former>[A-z0-9_]+)")

	// Others Patterns
	private val otherPartyJoinPattern = Pattern.compile("^(?:\\[A-Z+]+] )?(?<name>[A-z0-9_]+) joined the party\\.")
	private val othersInPartyPattern = Pattern.compile("^You'll be partying with: (?<names>.*)")
	private val otherLeftPartyPattern = Pattern.compile("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) left the party\\.")
	private val otherKickedPattern = Pattern.compile("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) has been removed from the party\\.")
	private val otherKickedOfflinePattern = Pattern.compile("^Kicked (?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) because they were offline\\.")
	private val otherDisconnectPattern = Pattern.compile("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) was removed from your party because they disconnected\\.")

	// Party Finder
	private val kuudraQueuePattern = Pattern.compile("^Party Finder > (?<name>[A-z0-9_]+) joined the group! \\([A-z0-9 ]+\\)")
	private val dungeonQueuePattern = Pattern.compile("^Party Finder > (?<name>[A-z0-9_]+) joined the dungeon group! \\([A-z0-9 ]+\\)")

	// Party General
	private val partyMembersListPattern = Pattern.compile("^Party (?<type>Leader|Moderators|Members): (?<names>.*)")
	private val partyDisbandPattern = Pattern.compile("^(?:\\[[A-Z+]+] )?(?<former>[A-z0-9_]+) has disbanded the party!")
	private val transferByPlayerPattern = Pattern.compile(
		"^The party was transferred to (?:\\[[A-Z+]+] )?(?<newLeader>[A-z0-9_]+) by (?:\\[[A-Z+]+] )?(?<formerLeader>[A-z0-9_]+)"
	)
	private val transferOnLeavePattern = Pattern.compile(
		"^The party was transferred to (?:\\[[A-Z+]+] )?(?<newLeader>[A-z0-9_]+) because (?:\\[[A-Z+]+] )?(?<formerLeader>[A-z0-9_]+) left"
	)
	private val partyInvitePattern = Pattern.compile(
		"^(?:\\[[A-Z+]+] )?(?<leader>[A-z0-9_]+) invited (?:\\[[A-Z+]+] )?[A-z0-9_]+ to the party! They have 60 seconds to accept."
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
		ClientReceiveMessageEvents.GAME.register { message, _ ->
			handleChatEvent(message.string.cleanFormatting())
		}

		ClientPlayConnectionEvents.JOIN.register { _, _, _ -> if(!gotList) onConnect() }
		ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> onDisconnect() }
	}

	fun onConnect() {
		getPartyList()
		gotList = true
	}

	fun onDisconnect() {
		storedPartyList.clear()
		gotList = false
	}

	override val isLeader get() = partyLeader == MCUtils.playerName

	fun listMembers(): Int {
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
		return Command.SINGLE_SUCCESS
	}

	fun handleChatEvent(message: String) {
		if(!gotList) {
			partyListPattern.matchMatcher(message) {
				storedPartyList.add(message)
			}

			storedPartyList.forEach { line ->
				partyMembersListPattern.matchMatcher(line) {
					inParty = true

					val type = group("type")
					val isPartyLeader = type == "Leader"
					val names = group("names")

					addPlayersToList(isPartyLeader, names)
				}
			}
			return
		}

		partyChatPattern.matchMatcher(message) {
			val name = group("name")
			addPlayer(name)
		}

		// Misc
		partyInvitePattern.matchMatcher(message) {
			val name = group("leader")
			partyLeader = name
			addPlayer(name)
		}

		// Member Join
		userPartyJoinPattern.matchMatcher(message) {
			val name = group("leader")
			partyLeader = name
			addPlayer(name)
		}
		otherPartyJoinPattern.matchMatcher(message) {
			val name = group("name")
			if(partyMembers.size == 1) {
				partyLeader = MCUtils.playerName
			}
			addPlayer(name)
		}
		kuudraQueuePattern.matchMatcher(message) {
			val name = group("name")
			addPlayer(name)
		}
		dungeonQueuePattern.matchMatcher(message) {
			val name = group("name")
			addPlayer(name)
		}

		// Member Leave
		otherLeftPartyPattern.matchMatcher(message) {
			val name = group("name")
			removePlayer(name)
		}
		otherKickedPattern.matchMatcher(message) {
			val name = group("name")
			removePlayer(name)
		}
		otherKickedOfflinePattern.matchMatcher(message) {
			val name = group("name")
			removePlayer(name)
		}
		otherDisconnectPattern.matchMatcher(message) {
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
		partyDisbandPattern.matchMatcher(message) {
			partyLeft()
		}
		userKickedPattern.matchMatcher(message) {
			partyLeft()
		}
		if(message == "You left the party." ||
			message == "The party was disbanded because all invites expired and the party was empty." ||
			message == "You are not currently in a party."
		) {
			partyLeft()
		}

		// Party Members
		othersInPartyPattern.matchMatcher(message) {
			for(name in group("names").replace(Regex("\\[[A-Z+]+] "), "").split(", ")) {
				addPlayer(name)
			}
		}

		// Party List
		partyMembersListPattern.matchMatcher(message) {
			inParty = true

			val type = group("type")
			val isPartyLeader = type == "Leader"
			val names = group("names")

			addPlayersToList(isPartyLeader, names)
		}
	}

	private fun getPartyList() {
		Scheduler.schedule(5 * 20) {
			if(!Utils.onHypixel) return@schedule
			HypixelCommands.partyList()
			gotList = true
		}
	}

	private fun addPlayersToList(isPartyLeader: Boolean, names: String) {
		inParty = true

		for(name in names.split(" ● ")) {
			val playerName = name.replace(" ●", "")
				.replace(Regex("\\[[A-Z+]+] "), "")
				.split(" ")

			playerName.forEach {
				if(it == "") return

				addPlayer(it)
				if(isPartyLeader) {
					partyLeader = it
				}
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