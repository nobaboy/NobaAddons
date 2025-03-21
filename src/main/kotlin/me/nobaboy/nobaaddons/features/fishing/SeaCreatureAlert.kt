package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.fishing.SeaCreature
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.render.RenderUtils

// TODO: Add Double Hook subtitle
object SeaCreatureAlert {
	private val config get() = NobaConfig.INSTANCE.fishing.seaCreatureAlert
	private val enabled: Boolean get() = config.enabled && SkyBlockAPI.inSkyBlock

	fun init() {
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return

		val seaCreature = SeaCreature.getBySpawnMessage(message) ?: return
		if(seaCreature.rarity < config.minimumRarity) return

		val text = if(config.nameInsteadOfRarity) {
			"${seaCreature.displayName}!"
		} else {
			"${seaCreature.rarity} Catch!"
		}

		RenderUtils.drawTitle(text, (seaCreature.rarity.color ?: NobaColor.GOLD), id = "sea_creature_alert")
		config.notificationSound.play()
	}
}