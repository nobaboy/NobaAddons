package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.api.data.Party
import me.nobaboy.nobaaddons.data.json.MojangProfile
import me.nobaboy.nobaaddons.events.CooldownTickEvent
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.HTTPUtils
import me.nobaboy.nobaaddons.utils.HypixelUtils
import me.nobaboy.nobaaddons.utils.ModAPIUtils.request
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import net.minecraft.client.MinecraftClient
import net.minecraft.text.HoverEvent
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import java.util.UUID
import java.util.concurrent.CompletableFuture
import kotlin.text.Regex
import kotlin.time.Duration.Companion.seconds

object PartyAPI {
	private val uuidCache = Util.memoize<UUID, CompletableFuture<MojangProfile>> {
		val url = "https://sessionserver.mojang.com/session/minecraft/profile/$it"
		HTTPUtils.fetchJson<MojangProfile>(url)
	}

	private val invalidatePartyStateMessages: Array<Regex> = arrayOf(
		// Join
		Regex("^You have joined (?:\\[[A-Z+]+] )?(?<leader>[A-z0-9_]+)'s party!"),
		Regex("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) joined the party\\."),
		Regex("^Party Finder > (?<name>[A-z0-9_]+) joined the (?:dungeon )?group! \\([A-z0-9 ]+\\)"),

		// Leave
		Regex("^(?:You left the party\\.|The party was disbanded because all invites expired and the party was empty\\.|You are not currently in a party\\.)"),
		Regex("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) has left the party\\."),
		Regex("^You have been kicked from the party by (?:\\[[A-Z+]+] )?(?<former>[A-z0-9_]+)"),
		Regex("^(?:\\[[A-Z+]+] )?(?<former>[A-z0-9_]+) has disbanded the party!"),
		Regex("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) has been removed from the party\\."),
		Regex("^Kicked (?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) because they were offline\\."),
		Regex("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) was removed from your party because they disconnected\\."),

		// Transfer
		Regex("^The party was transferred to (?:\\[[A-Z+]+] )?(?<newLeader>[A-z0-9_]+) because (?:\\[[A-Z+]+] )?(?<formerLeader>[A-z0-9_]+) left"),
		Regex("^The party was transferred to (?:\\[[A-Z+]+] )?(?<newLeader>[A-z0-9_]+) by (?:\\[[A-Z+]+] )?(?<formerLeader>[A-z0-9_]+)"),
	)

	private var refreshPartyList = false
	var party: Party? = null
		private set

	fun init() {
		ClientReceiveMessageEvents.GAME.register { message, _ ->
			val cleaned = Formatting.strip(message.string)!!
			if(invalidatePartyStateMessages.any { it.matches(cleaned) }) {
				refreshPartyList = true
			}
		}
		ClientPlayConnectionEvents.JOIN.register { _, _, _ -> refreshPartyList = true }
		ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> party = null }
		CooldownTickEvent.EVENT.register(TickEvent)
	}

	fun getPartyInfo() {
		HypixelModAPI.getInstance().request<ClientboundPartyInfoPacket> {
			if(!it.isInParty) {
				party = null
				return@request
			}

			CompletableFuture.runAsync {
				val leader = it.leader.orElseThrow()
				val members = it.memberMap.values.map {
					val profile = uuidCache.apply(it.uuid).join()
					Party.Member(uuid = it.uuid, profile = profile, role = it.role)
				}
				party = Party(leaderUUID = leader, members = members)
			}
		}
	}

	fun listMembers() {
		val party = this.party
		if(party == null || party.members.isEmpty()) {
			ChatUtils.addMessage("Party seems to be empty...")
			return
		}

		val partySize = party.members.size
		ChatUtils.addMessage("Party Members ($partySize):")
		party.members.forEach { member ->
			ChatUtils.addMessage(buildText {
				append(" - ".toText().formatted(Formatting.AQUA))
				append(member.name.toText().styled {
					val uuid = member.uuid.toString().toText().formatted(Formatting.GRAY)
					it.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, uuid)).withColor(Formatting.GRAY)
				})
				if(member.isLeader) {
					append(" (Leader)".toText().formatted(Formatting.DARK_GRAY))
				} else if(member.isMod) {
					append(" (Mod)".toText().formatted(Formatting.DARK_GRAY))
				}
			}, prefix = false)
		}
	}

	private object TickEvent : CooldownTickEvent {
		override val cooldownManager = CooldownManager(2.seconds)

		override fun onTick(client: MinecraftClient) {
			if(refreshPartyList && HypixelUtils.onHypixel) {
				getPartyInfo()
				refreshPartyList = false
				cooldownManager.startCooldown()
			}
		}
	}
}