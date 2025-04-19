package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.events.impl.skyblock.FishingEvents
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.StringUtils

object AnnounceSeaCreatures {
	private val config get() = NobaConfig.fishing.announceSeaCreatures
	private val enabled: Boolean get() = config.enabled && SkyBlockAPI.inSkyBlock

	private val SeaCreature.isRare: Boolean get() =
		rarity >= config.minimumRarity || (id == "CARROT_KING" && config.carrotKing) || (id == "NUTCRACKER" && config.nutcracker)

	fun init() {
		FishingEvents.SEA_CREATURE_CATCH.register(this::onSeaCreatureCatch)
	}

	private fun onSeaCreatureCatch(event: FishingEvents.SeaCreatureCatch) {
		if(!enabled) return

		val seaCreature = event.seaCreature
		if(!seaCreature.isRare) return

		val location = LocationUtils.playerCoords()
		val randomString = StringUtils.randomAlphanumeric()

		val prefix = if(event.doubleHook) "DOUBLE HOOK! " else ""
		val message = "$location | $prefix${seaCreature.displayName} at [ ${SkyBlockAPI.prefixedZone} ] @$randomString"

		config.announceChannel.send(message)
	}
}