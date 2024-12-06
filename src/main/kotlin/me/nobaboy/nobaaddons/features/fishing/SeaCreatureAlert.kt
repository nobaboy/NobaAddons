package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.SeaCreature
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

object SeaCreatureAlert {
	private val config get() = NobaConfigManager.config.fishing.seaCreatureAlert

	fun init() {
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onChatMessage(message: String) {
		if(!isEnabled()) return

		val seaCreature = SeaCreature.creatures[message] ?: return
		if(!seaCreature.rarity.isAtLeast(config.minimumRarity)) return

		val text = if(config.nameInsteadOfRarity) {
			"${seaCreature.displayName}!"
		} else {
			"${seaCreature.rarity} Catch!"
		}

		RenderUtils.drawTitle(text, seaCreature.rarity.color)
		config.notificationSound.play()
	}

	private fun isEnabled() = SkyBlockAPI.inSkyBlock && config.enabled
}