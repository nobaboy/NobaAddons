package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.render.RenderUtils

object SeaCreatureAlert {
	private val config get() = NobaConfig.INSTANCE.fishing.seaCreatureAlert
	private val enabled: Boolean get() = config.enabled && SkyBlockAPI.inSkyBlock

	fun init() {
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return

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

		RenderUtils.drawTitle(text, (seaCreature.rarity.color ?: NobaColor.GOLD))
		config.notificationSound.play()
	}

	private val SeaCreature.isRare: Boolean
		get() = rarity >= config.minimumRarity || (id == "CARROT_KING" && config.carrotKingIsRare)
}