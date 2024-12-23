package me.nobaboy.nobaaddons.api

import kotlinx.coroutines.Deferred
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.data.PartyData
import me.nobaboy.nobaaddons.data.json.MojangProfile
import me.nobaboy.nobaaddons.events.CooldownTickEvent
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.HTTPUtils
import me.nobaboy.nobaaddons.utils.HypixelUtils
import me.nobaboy.nobaaddons.utils.ModAPIUtils.request
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import net.minecraft.text.HoverEvent
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import java.util.UUID
import kotlin.text.Regex
import kotlin.time.Duration.Companion.seconds

object PartyAPI {
	private val uuidCache = Util.memoize<UUID, Deferred<MojangProfile>> {
		val url = "https://sessionserver.mojang.com/session/minecraft/profile/$it"
		HTTPUtils.fetchJson<MojangProfile>(url)
	}

	private val invalidatePartyStateMessages: List<Regex> by Repo.list(
		// Join
		Regex("^You have joined (?:\\[[A-Z+]+] )?(?<leader>[A-z0-9_]+)'s party!").fromRepo("party.join"),
		Regex("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) joined the party\\.").fromRepo("party.other_join"),
		Regex("^Party Finder > (?<name>[A-z0-9_]+) joined the (?:dungeon )?group! \\([A-z0-9 ]+\\)").fromRepo("party.party_finder_join"),

		// Leave
		Regex("^(?:You left the party\\.|The party was disbanded because all invites expired and the party was empty\\.|You are not currently in a party\\.)").fromRepo("party.leave"),
		Regex("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) has left the party\\.").fromRepo("party.other_leave"),
		Regex("^You have been kicked from the party by (?:\\[[A-Z+]+] )?[A-z0-9_]+").fromRepo("party.kicked"),
		Regex("^(?:\\[[A-Z+]+] )?(?<former>[A-z0-9_]+) has disbanded the party!").fromRepo("party.disbanded"),
		Regex("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) has been removed from the party\\.").fromRepo("party.other_kicked"),
		Regex("^Kicked (?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) because they were offline\\.").fromRepo("party.offline_kicked"),
		Regex("^(?:\\[[A-Z+]+] )?(?<name>[A-z0-9_]+) was removed from your party because they disconnected\\.").fromRepo("party.offline_removed"),

		// Transfer
		Regex("^The party was transferred to (?:\\[[A-Z+]+] )?(?<newLeader>[A-z0-9_]+) because (?:\\[[A-Z+]+] )?(?<formerLeader>[A-z0-9_]+) left").fromRepo("party.transfer_leave"),
		Regex("^The party was transferred to (?:\\[[A-Z+]+] )?(?<newLeader>[A-z0-9_]+) by (?:\\[[A-Z+]+] )?(?<formerLeader>[A-z0-9_]+)").fromRepo("party.transfer"),
		Regex("^(?:\\[[A-Z+]+] )?[A-z0-9_]+ has (?:promoted|demoted) (?:\\[[A-Z+]+] )?[A-z0-9_]+ to Party (?:Member|Moderator|Leader)").fromRepo("party.promote_demote"),
	)

	private var refreshPartyList = false
	var party: PartyData? = null
		private set

	fun init() {
		CooldownTickEvent.EVENT.register(this::onTick)
		ClientPlayConnectionEvents.JOIN.register { _, _, _ -> refreshPartyList = true }
		ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> party = null }
		ClientReceiveMessageEvents.GAME.register { message, _ ->
			val cleaned = message.string.cleanFormatting()
			if(invalidatePartyStateMessages.any { it.matches(cleaned) }) refreshPartyList = true
		}
	}

	private fun onTick(event: CooldownTickEvent) {
		if(refreshPartyList && HypixelUtils.onHypixel) {
			getPartyInfo()
			refreshPartyList = false
			event.cooldownManager.startCooldown(1.5.seconds)
		}
	}

	fun getPartyInfo() {
		HypixelModAPI.getInstance().request<ClientboundPartyInfoPacket> {
			if(!it.isInParty) {
				party = null
				return@request
			}

			NobaAddons.runAsync {
				val leader = it.leader.orElseThrow()
				// TODO this should be reworked to not wait until *every* profile is fetched
				val members = it.memberMap.values.map {
					val profile = uuidCache.apply(it.uuid).await()
					PartyData.Member(uuid = it.uuid, profile = profile, role = it.role)
				}
				party = PartyData(leaderUUID = leader, members = members)
			}
		}
	}

	// This method is only called from debug commands, and as such is fine being untranslated.
	fun listMembers() {
		val party = this.party
		if(party == null || party.members.isEmpty()) {
			ChatUtils.addMessage("Party seems to be empty...")
			return
		}

		val partySize = party.members.size
		ChatUtils.addMessage("Party Members ($partySize):")
		party.members.forEach { member ->
			ChatUtils.addMessage(prefix = false) {
				append(" - ".toText().formatted(Formatting.AQUA))
				append(member.name.toText().styled {
					val uuid = member.uuid.toString().toText().formatted(Formatting.GRAY)
					it.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, uuid)).withColor(Formatting.GRAY).withBold(member.isMe)
				})
				if(member.isLeader) {
					append(" (Leader)".toText().formatted(Formatting.BLUE))
				} else if(member.isMod) {
					append(" (Mod)".toText().formatted(Formatting.BLUE))
				}
			}
		}
	}
}