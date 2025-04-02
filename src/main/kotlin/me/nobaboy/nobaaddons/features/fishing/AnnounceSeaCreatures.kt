package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.skyblock.FishingEvents
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands

object AnnounceSeaCreatures {
	private val config get() = NobaConfig.fishing.announceSeaCreatures
	private val enabled: Boolean get() = config.enabled && SkyBlockAPI.inSkyBlock

	fun init() {
		FishingEvents.SEA_CREATURE_CATCH.register(this::onSeaCreatureCatch)
	}

	private fun onSeaCreatureCatch(event: FishingEvents.SeaCreatureCatch) {
		if(!enabled) return

		val seaCreature = event.seaCreature
		if(seaCreature.rarity < config.minimumRarity) return

		val location = LocationUtils.playerCoords()
		val randomString = StringUtils.randomAlphanumeric()

		val prefix = if(event.doubleHook) "DOUBLE HOOK! " else ""
		val message = "$location | $prefix${seaCreature.displayName} at [ ${SkyBlockAPI.prefixedZone} ] @$randomString"

		if(config.onlyInPartyChat) {
			if(PartyAPI.party != null) HypixelCommands.partyChat(message)
		} else {
			ChatUtils.sendChatAsPlayer(message)
		}
	}
}