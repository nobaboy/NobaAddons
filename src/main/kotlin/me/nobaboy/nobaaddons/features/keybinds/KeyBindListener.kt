package me.nobaboy.nobaaddons.features.keybinds

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.features.keybinds.data.KeybindConfig
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.keybinds.NobaKeyBind
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.MinecraftClient
import org.lwjgl.glfw.GLFW
import kotlin.time.Duration.Companion.seconds

object KeyBindListener {
	private val cooldown = CooldownManager(1.seconds)
	private val keybinds = mutableListOf<NobaKeyBind>(/* TODO refill pearls keybind */)

	fun init() {
		keybinds.forEach { KeyBindingHelper.registerKeyBinding(it) }
		KeybindConfig.load()
	}

	@JvmStatic
	fun onPress(key: Int) {
		if(key == GLFW.GLFW_KEY_UNKNOWN) return
		// TODO add override to allow using outside skyblock (i do not feel like touching yacl right now)
		if(!SkyBlockAPI.inSkyblock) return
		if(cooldown.isOnCooldown()) return

		val keybind = KeybindConfig.keybinds.firstOrNull { it.keycode == key } ?: return
		MinecraftClient.getInstance().networkHandler!!.sendChatCommand(keybind.command)
		cooldown.startCooldown()
	}
}