package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.SeaCreature
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

// TODO: Implement Title Hud to use with this
object SeaCreatureAlert {
	private val config get() = NobaConfigManager.config.uiAndVisuals.seaCreatureAlert

	fun init() {
		ClientReceiveMessageEvents.GAME.register { message, _ -> handleChatEvent(message.string.cleanFormatting()) }
	}

	private fun handleChatEvent(message: String) {
		if(!isEnabled()) return

		val seaCreature = SeaCreature.creatures[message] ?: return
		if(!seaCreature.rarity.isAtLeast(config.minimumRarity)) return

		val titleText = if(config.nameInsteadOfRarity) "${seaCreature.displayName}!"
			else "${seaCreature.rarity.displayName} Catch!"
	}

	private fun isEnabled() = SkyBlockAPI.inSkyblock && config.enabled
}