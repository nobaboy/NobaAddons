package me.nobaboy.nobaaddons.api

import com.mojang.brigadier.Command
import me.nobaboy.nobaaddons.utils.ChatUtils
import me.nobaboy.nobaaddons.utils.HypixelCommands
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.StringUtils.clean
import me.nobaboy.nobaaddons.utils.Utils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import java.util.regex.Pattern

object PartyAPI {
    // User Patterns
    private val userPartyJoinPattern : Pattern =
        Pattern.compile("^You have joined (?:\\[[A-Z+]+] )?(?<leader>[A-z0-9_]+)'s party!")
    private val userKickedPattern : Pattern =
        Pattern.compile("^You have been kicked from the party by (?:\\[[A-Z+]+] )?(?<former>[A-z0-9_]+)")

    // Others Patterns
    private val otherPartyJoinPattern : Pattern = Pattern.compile("^(?:\\[A-Z+]+] )?(?<name>[A-z0-9_]+) joined the party\\.")
    private val othersInPartyPattern : Pattern = Pattern.compile("^You'll be partying with: (?<names>.*)")
    private val otherLeftPartyPattern : Pattern = Pattern.compile("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) left the party\\.")
    private val otherKickedPattern : Pattern =
        Pattern.compile("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) has been removed from the party\\.")
    private val otherKickedOfflinePattern : Pattern =
        Pattern.compile("^Kicked (?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) because they were offline\\.")
    private val otherDisconnectPattern : Pattern =
        Pattern.compile("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) was removed from your party because they disconnected\\.")

    // Party Finder
    private val kuudraQueuePattern : Pattern =
        Pattern.compile("^Party Finder > (?<name>[A-z0-9_]+) joined the group! \\([A-z0-9 ]+\\)")
    private val dungeonQueuePattern : Pattern =
        Pattern.compile("^Party Finder > (?<name>[A-z0-9_]+) joined the dungeon group! \\([A-z0-9 ]+\\)")

    // Party General
    private val partyMembersListPattern : Pattern = Pattern.compile("^Party (?<type>Leader|Moderators|Members): (?<names>.*)")
    private val transferByPlayerPattern : Pattern =
        Pattern.compile("^The party was transferred to (?:\\[[A-Z+]+] )?(?<newLeader>[A-z0-9_]+) by (?:\\[[A-Z+]+] )?(?<formerLeader>[A-z0-9_]+)")
    private val transferOnLeavePattern : Pattern =
        Pattern.compile("^The party was transferred to (?:\\[[A-Z+]+] )?(?<newLeader>[A-z0-9_]+) because (?:\\[[A-Z+]+] )?(?<formerLeader>[A-z0-9_]+) left")
    private val partyInvitePattern : Pattern =
        Pattern.compile("^(?:\\[[A-Z+]+] )?(?<leader>[A-z0-9_]+) invited (?:\\[[A-Z+]+] )?[A-z0-9_]+ to the party! They have 60 seconds to accept.")
    private val partyDisbandPattern : Pattern =
        Pattern.compile("^(?:\\[[A-Z+]+] )?(?<former>[A-z0-9_]+) has disbanded the party!")

    // Party Misc
    private val partyListPattern : Pattern =
        Pattern.compile("^-{53}|^Party Members \\([0-9]+\\)|^Party (?<type>Leader|Moderators|Members): (?<names>.*)|^You are not currently in a party\\.")
    private val partyChatPattern : Pattern = Pattern.compile("^Party > (?:\\[[A-Z+]+ )?(?<name>[A-z0-9_]+): .*")

    private var storedPartyList = mutableListOf<String>()
    private var gotList = false

    var inParty: Boolean = false
    var partyMembers = mutableListOf<String>()
    var partyLeader: String? = null

    fun init() {
        ClientReceiveMessageEvents.GAME.register { message, _ ->
            handleChatEvent(message.string.clean())
        }

        ClientPlayConnectionEvents.JOIN.register(ClientPlayConnectionEvents.Join { _, _, _ ->
            if (!gotList) {
                onConnect()
            }
        })
        ClientPlayConnectionEvents.DISCONNECT.register(ClientPlayConnectionEvents.Disconnect { _, _ ->
            onDisconnect()
        })
    }

    fun onConnect() {
        getPartyList()
        gotList = true
    }

    fun onDisconnect() {
        storedPartyList.clear()
        gotList = false
    }

    fun isLeader() = partyLeader == Utils.getPlayerName()

    fun listMembers(): Int {
        val partySize = partyMembers.size
        println(partySize)
        if (partySize == 0) {
            ChatUtils.addMessage("Party seems to empty...")
        } else {
            ChatUtils.addMessage("Party Members ($partySize):")
            for (member in partyMembers) {
                val isMemberLeader = if (partyLeader.equals(member)) "§9(Leader)" else ""
                ChatUtils.addMessage(" §b- §7$member $isMemberLeader", false)
            }
        }
        return Command.SINGLE_SUCCESS
    }

    fun handleChatEvent(message: String) {
        if (!gotList) {
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
            if (partyMembers.size == 1) {
                partyLeader = Utils.getPlayerName()
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
        if (message == "You left the party." ||
            message == "The party was disbanded because all invites expired and the party was empty." ||
            message == "You are not currently in a party."
        ) {
            partyLeft()
        }

        // Party Members
        othersInPartyPattern.matchMatcher(message) {
            for (name in group("names").replace(Regex("\\[[A-Z+]+] "), "").split(", ")) {
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
            if (!Utils.onHypixel) return@schedule
            HypixelCommands.partyList()
            gotList = true
        }
    }

    private fun addPlayersToList(isPartyLeader: Boolean, names: String) {
        inParty = true

        for (name in names.split(" ● ")) {
            val playerName = name.replace(" ●", "")
                .replace(Regex("\\[[A-Z+]+] "), "")
                .split(" ")

            playerName.forEach {
                if (it == "") return

                addPlayer(it)
                if (isPartyLeader) {
                    partyLeader = it
                }
            }
        }
    }

    private fun addPlayer(playerName: String) {
        if (partyMembers.contains(playerName)) return
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
}