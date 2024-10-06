package me.nobaboy.nobaaddons.utils.keybinds

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper

object KeyBindListener {
	private val keybinds: MutableList<NobaKeyBind> = mutableListOf(
		CommandKeyBind("nobaaddons.key.pets", "pets"),
		CommandKeyBind("nobaaddons.key.wardrobe", "wardrobe"),
		CommandKeyBind("nobaaddons.key.equipment", "equipment"),
		CommandKeyBind("nobaaddons.key.enderchest", "enderchest"),
		CommandKeyBind("nobaaddons.key.storage", "storage"),
		NobaKeyBind("nobaaddons.key.refillPearls") { null }
	)

	fun init() {
		keybinds.forEach {
			KeyBindingHelper.registerKeyBinding(it)
		}

		ClientTickEvents.END_CLIENT_TICK.register { client ->
			keybinds.forEach { if(it.wasPressed()) it.maybePress() }
		}
	}
}