package me.nobaboy.nobaaddons.features.keybinds.impl

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.MCUtils
import org.lwjgl.glfw.GLFW
import kotlin.time.Duration.Companion.milliseconds

@Serializable
data class KeyBind(
	var command: String = "",
	var key: Int = GLFW.GLFW_KEY_UNKNOWN,
) {
	@Transient
	private val cooldownManager = CooldownManager(500.milliseconds)

	fun maybePress() {
		if(cooldownManager.isOnCooldown()) return
		cooldownManager.startCooldown()

		MCUtils.networkHandler!!.sendChatCommand(command.removePrefix("/"))
		cooldownManager.startCooldown()
	}
}