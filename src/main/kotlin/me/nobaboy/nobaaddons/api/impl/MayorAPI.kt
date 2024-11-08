package me.nobaboy.nobaaddons.api.impl

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.core.Mayor
import me.nobaboy.nobaaddons.events.skyblock.SecondPassedEvent
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.Timestamp
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import java.util.regex.Pattern
import kotlin.time.Duration.Companion.minutes

object MayorAPI {
	private val electionOverPattern = Pattern.compile(
		"The election room is now closed\\. Clerk Serpahine is doing a final count of the votes\\.\\.\\."
	)

	private val mayorNamePattern = Pattern.compile("Mayor (?<name>[A-z]+)")

	var currentMayor: Mayor = Mayor.UNKNOWN
		private set
	var currentMinister: Mayor = Mayor.UNKNOWN
		private set

	private var lastMayor: Mayor? = null

	private var lastUpdate = Timestamp.distantPast()

	fun init() {
		SecondPassedEvent.EVENT.register { handleSecondPassed() }
//		InventoryEvents.READY.register(this::handleInventoryReady)
		ClientReceiveMessageEvents.GAME.register { message, _ -> handleChatEvent(message.string.cleanFormatting())}
	}

	private fun handleSecondPassed() {
		if(!SkyBlockAPI.inSkyblock) return

	}

	private fun handleChatEvent(message: String) {

	}

//	private fun handleInventoryReady(inventory: InventoryData) {
//		if(!SkyBlockAPI.inSkyblock) return
//		if(inventory.name != "Calendar and Events") return
//
//	}

	private fun getCurrentMayor(forced: Boolean = false) {
		if(!forced) {
			if(currentMayor == Mayor.UNKNOWN && lastUpdate.elapsedSince() < 1.minutes) return
			if(lastUpdate.elapsedSince() < 20.minutes) return
		}

		lastUpdate = Timestamp.currentTime()
	}
}