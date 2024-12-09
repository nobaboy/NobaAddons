package me.nobaboy.nobaaddons.features.keybinds

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.events.mythological.BurrowWaypoints
import me.nobaboy.nobaaddons.features.keybinds.data.KeybindConfig
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.keybinds.NobaKeyBind
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import org.lwjgl.glfw.GLFW
import kotlin.time.Duration.Companion.milliseconds

object KeyBindListener {
	private val config get() = NobaConfigManager.config.general

	private val cooldownManager = CooldownManager(500.milliseconds)
	private val keybinds = mutableListOf<NobaKeyBind>(
		NobaKeyBind("nobaaddons.keybind.mythologicalNearestWarp") { BurrowWaypoints.useNearestWarp() }
	)

	fun init() {
		keybinds.forEach(KeyBindingHelper::registerKeyBinding)
		KeybindConfig.load()
	}

	@JvmStatic
	fun onPress(key: Int) {
		if(key == GLFW.GLFW_KEY_UNKNOWN) return
		if(!SkyBlockAPI.inSkyBlock && !config.allowKeybindsOutsideSkyBlock) return
		if(cooldownManager.isOnCooldown()) return

		val keybind = KeybindConfig.keybinds.firstOrNull { it.keycode == key } ?: return
		MCUtils.networkHandler!!.sendChatCommand(keybind.command)
		cooldownManager.startCooldown()
	}
}