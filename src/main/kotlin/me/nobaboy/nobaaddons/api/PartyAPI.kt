package me.nobaboy.nobaaddons.api

import com.mojang.authlib.yggdrasil.ProfileResult
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.data.PartyData
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.HypixelUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.ModAPIUtils.listen
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.ClientboundPartyInfoPacket
import net.hypixel.modapi.packet.impl.serverbound.ServerboundPartyInfoPacket
import net.minecraft.util.Formatting
import net.minecraft.util.Util
import java.util.UUID
import kotlin.time.Duration.Companion.seconds

object PartyAPI {
	private val semaphore = Semaphore(3)
	private val uuidCache = Util.memoize<UUID, Deferred<ProfileResult?>> {
		NobaAddons.coroutineScope.async {
			semaphore.withPermit { MCUtils.client.sessionService.fetchProfile(it, true) }
		}
	}

	private val INVALIDATE_PARTY_REGEXES by Repo.list(
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
		TickEvents.cooldown { _, cooldown -> onTick(cooldown) }
		ClientPlayConnectionEvents.JOIN.register { _, _, _ -> refreshPartyList = true }
		ClientPlayConnectionEvents.DISCONNECT.register { _, _ -> party = null }
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		HypixelModAPI.getInstance().listen(this::onPartyData)
	}

	private fun onTick(cooldownManager: CooldownManager) {
		if(refreshPartyList && HypixelUtils.onHypixel) {
			getPartyInfo()
			refreshPartyList = false
			cooldownManager.startCooldown(1.5.seconds)
		}
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!HypixelUtils.onHypixel) return

		if(INVALIDATE_PARTY_REGEXES.any { it.matches(event.cleaned) }) {
			refreshPartyList = true
		}
	}

	private fun onPartyData(party: ClientboundPartyInfoPacket) {
		if(!party.isInParty) {
			this.party = null
			return
		}

		this.party = PartyData(
			leaderUUID = party.leader.orElseThrow(),
			members = party.memberMap.values.map {
				PartyData.Member(uuid = it.uuid, profile = uuidCache.apply(it.uuid), role = it.role)
			},
		)
	}

	fun getPartyInfo() {
		HypixelModAPI.getInstance().sendPacket(ServerboundPartyInfoPacket())
	}

	// This method is only called from debug commands, and as such is fine being untranslated.
	@OptIn(UntranslatedMessage::class)
	fun listMembers() {
		val party = this.party
		if(party == null || party.members.isEmpty()) {
			ChatUtils.addMessage("Party seems to be empty...")
			return
		}

		val partySize = party.members.size
		ChatUtils.addMessage("Party Members ($partySize):")
		party.members.forEach { member ->
			val text = buildText {
				append(" - ".toText().formatted(Formatting.AQUA))
				append(member.name.toText().styled {
					val uuid = member.uuid.toString().toText().formatted(Formatting.GRAY)
					it.hoverText(uuid).withColor(Formatting.GRAY).withBold(member.isMe)
				})
				if(member.isLeader) {
					append(" (Leader)".toText().formatted(Formatting.BLUE))
				} else if(member.isMod) {
					append(" (Mod)".toText().formatted(Formatting.BLUE))
				}
			}
			ChatUtils.addMessage(text, prefix = false)
		}
	}
}