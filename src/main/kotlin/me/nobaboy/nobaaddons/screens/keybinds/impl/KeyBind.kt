package me.nobaboy.nobaaddons.screens.keybinds.impl

import com.google.gson.annotations.Expose
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.MCUtils
import org.lwjgl.glfw.GLFW
import kotlin.time.Duration.Companion.milliseconds

data class KeyBind(
	@Expose var command: String = "",
	@Expose var key: Int = GLFW.GLFW_KEY_UNKNOWN
) {
	private val cooldownManager = CooldownManager(500.milliseconds)

	fun maybePress() {
		if(cooldownManager.isOnCooldown()) return
		cooldownManager.startCooldown()

		MCUtils.networkHandler!!.sendChatCommand(command.removePrefix("/"))
		cooldownManager.startCooldown()
	}
}