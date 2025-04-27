package me.nobaboy.nobaaddons.features.crimsonisle

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.StringUtils

object AnnounceVanquisher {
	private val config get() = NobaConfig.crimsonIsle.announceVanquisher
	private val enabled: Boolean get() = config.enabled && SkyBlockAPI.inSkyBlock

	private val VANQUISHER_SPAWN_MESSAGE by "A Vanquisher is spawning nearby!".fromRepo("crimson_isle.vanquisher_spawn")

	fun init() {
		ChatMessageEvents.CHAT.register(this::onChatMessage)
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!enabled) return

		if(event.cleaned == VANQUISHER_SPAWN_MESSAGE) {
			val location = LocationUtils.playerCoords()
			val randomString = StringUtils.randomAlphanumeric()

			val message = "$location | Vanquisher at [ ${SkyBlockAPI.prefixedZone} ] @$randomString"
			config.announceChannel.send(message)
		}
	}
}