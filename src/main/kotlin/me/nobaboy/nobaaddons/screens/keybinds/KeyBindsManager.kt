package me.nobaboy.nobaaddons.screens.keybinds

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.events.mythological.BurrowWaypoints
import me.nobaboy.nobaaddons.screens.keybinds.impl.KeyBind
import me.nobaboy.nobaaddons.screens.keybinds.impl.NobaKeyBind
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import org.lwjgl.glfw.GLFW

object KeyBindsManager {
	private val config = NobaConfigManager.config.general

	val commandKeyBinds = mutableListOf<KeyBind>()
	private val gameKeyBinds = mutableListOf<NobaKeyBind>(
		NobaKeyBind("nobaaddons.keyBind.mythologicalRitual.nearestWarp") { BurrowWaypoints.useNearestWarp() }
	)

	fun init() {
		gameKeyBinds.forEach(KeyBindingHelper::registerKeyBinding)

		runCatching {
			KeyBindsConfig.load()
			commandKeyBinds.addAll(KeyBindsConfig.keyBinds)
		}.onFailure {
			NobaAddons.LOGGER.error("Failed to load key-binds.json", it)
		}
	}

	fun saveKeyBinds() {
		try {
			KeyBindsConfig.keyBinds.clear()
			KeyBindsConfig.keyBinds.addAll(commandKeyBinds)
			KeyBindsConfig.save()
		} catch(ex: IOException) {
			NobaAddons.LOGGER.error("Failed to save key-binds.json", ex)
		}
	}

	@JvmStatic
	fun onPress(keyCode: Int) {
		if(keyCode == GLFW.GLFW_KEY_UNKNOWN) return
		if(!SkyBlockAPI.inSkyBlock && !config.allowKeybindsOutsideSkyBlock) return

		val keyBind = commandKeyBinds.firstOrNull { it.keyCode == keyCode } ?: return
		keyBind.maybePress()
	}
}