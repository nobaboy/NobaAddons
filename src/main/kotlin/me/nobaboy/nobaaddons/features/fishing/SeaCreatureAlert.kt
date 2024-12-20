package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

object SeaCreatureAlert {
	private val config get() = NobaConfigManager.config.fishing.seaCreatureAlert

	fun init() {
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onChatMessage(message: String) {
		if(!isEnabled()) return

		val seaCreature = SeaCreature.getBySpawnMessage(message) ?: return
		if(!seaCreature.isRare) return

		val text = if(config.nameInsteadOfRarity) {
			"${seaCreature.displayName}!"
		} else {
			"${seaCreature.rarity} Catch!"
		}

		if(config.announceInPartyChat) {
			HypixelCommands.partyChat("[NobaAddons] Caught a ${seaCreature.displayName}!")
		}

		RenderUtils.drawTitle(text, seaCreature.rarity.color)
		config.notificationSound.play()
	}

	private val SeaCreature.isRare: Boolean
		get() {
			if(rarity.isAtLeast(config.minimumRarity)) return true
			if(this == SeaCreature.CARROT_KING && config.carrotKingIsRare) return true
			return false
		}

	private fun isEnabled() = SkyBlockAPI.inSkyBlock && config.enabled
}