package me.nobaboy.nobaaddons.screens.keybinds

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.events.mythological.BurrowWaypoints
import me.nobaboy.nobaaddons.screens.keybinds.impl.KeyBind
import me.nobaboy.nobaaddons.screens.keybinds.impl.NobaKeyBind
import me.nobaboy.nobaaddons.utils.CooldownManager
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import org.lwjgl.glfw.GLFW
import kotlin.time.Duration.Companion.milliseconds

object KeyBindsManager {
	private val config = NobaConfigManager.config.general

	private val cooldownManager = CooldownManager(100.milliseconds)

	internal val commandKeyBinds = mutableListOf<KeyBind>()
	private val gameKeyBinds = listOf<NobaKeyBind>(
		NobaKeyBind("nobaaddons.keyBind.mythologicalRitual.nearestWarp") { BurrowWaypoints.useNearestWarp() }
	)

	fun init() {
		gameKeyBinds.forEach(KeyBindingHelper::registerKeyBinding)

		try {
			KeyBindsConfig.load()
			commandKeyBinds.addAll(KeyBindsConfig.keyBinds)
		} catch(ex: IOException) {
			NobaAddons.LOGGER.error("Failed to load key-binds.json", ex)
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
		if(cooldownManager.isOnCooldown()) return

		val keyBind = commandKeyBinds.firstOrNull { it.key == keyCode } ?: return
		keyBind.maybePress()
		cooldownManager.startCooldown()
	}
}