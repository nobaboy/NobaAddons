package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands

object AnnounceSeaCreatures {
	private val config get() = NobaConfig.INSTANCE.fishing.announceSeaCreatures
	private val enabled: Boolean get() = config.enabled

	fun init() {
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return

		val seaCreature = SeaCreature.getBySpawnMessage(message) ?: return
		val shouldAnnounce = when(config.seaCreatureType) {
			SeaCreatureType.RARITY -> config.minimumRarity >= seaCreature.rarity
			SeaCreatureType.HOTSPOT -> "hotspot" in seaCreature.tags
			SeaCreatureType.RARE -> "rare" in seaCreature.tags
			SeaCreatureType.HOTSPOT_RARE -> "hotspot" in seaCreature.tags || "rare" in seaCreature.tags
		}
		if(!shouldAnnounce) return

		val location = LocationUtils.playerCoords()
		val randomString = StringUtils.randomAlphanumeric()
		val message = "$location | ${seaCreature.displayName} at [ ${SkyBlockAPI.prefixedZone} ] @$randomString"

		if(config.onlyInPartyChat) {
			if(PartyAPI.party != null) HypixelCommands.partyChat(message)
		} else {
			ChatUtils.sendChatAsPlayer(message)
		}
	}
}