package me.nobaboy.nobaaddons.screens.keybinds

import kotlinx.io.IOException
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.events.mythological.BurrowWaypoints
import me.nobaboy.nobaaddons.screens.keybinds.impl.KeyBind
import me.nobaboy.nobaaddons.screens.keybinds.impl.NobaKeyBind
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import org.lwjgl.glfw.GLFW
import kotlin.time.Duration.Companion.milliseconds

object KeyBindsManager {
	private val config = NobaConfigManager.config.general

	private val cooldownManager = CooldownManager(100.milliseconds)

	internal val commandKeyBinds = mutableListOf<KeyBind>()
	private val gameKeyBinds = listOf<NobaKeyBind>(
		NobaKeyBind(tr("nobaaddons.keyBind.mythologicalRitual.nearestWarp", "Mythological Nearest Warp")) { BurrowWaypoints.useNearestWarp() }
	)

	fun init() {
		gameKeyBinds.forEach(KeyBindingHelper::registerKeyBinding)

		try {
			KeyBindsConfig.load()
			commandKeyBinds.addAll(KeyBindsConfig.keyBinds)
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to load keybinds", ex)
		}
	}

	fun saveKeyBinds() {
		try {
			KeyBindsConfig.keyBinds.clear()
			KeyBindsConfig.keyBinds.addAll(commandKeyBinds)
			KeyBindsConfig.save()
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to save keybinds", ex)
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