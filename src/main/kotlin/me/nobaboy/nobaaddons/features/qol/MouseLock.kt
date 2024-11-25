package me.nobaboy.nobaaddons.features.qol

import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.chat.ChatUtils

object MouseLock {
	@JvmField
	var mouseLocked: Boolean = false

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { mouseLocked = false }
	}

	fun lockMouse() {
		mouseLocked = !mouseLocked

		val text = if(mouseLocked) "Mouse locked" else "Mouse unlocked"
		ChatUtils.addMessage(text)
	}
}