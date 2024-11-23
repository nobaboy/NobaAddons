package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.SeaCreature
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

// TODO: Implement Title Hud to use with this
object SeaCreatureAlert {
	private val config get() = NobaConfigManager.config.fishing.seaCreatureAlert

	fun init() {
		ClientReceiveMessageEvents.GAME.register { message, _ -> handleChatEvent(message.string.cleanFormatting()) }
	}

	private fun handleChatEvent(message: String) {
		if(!isEnabled()) return

		val seaCreature = SeaCreature.creatures[message] ?: return
		if(!seaCreature.rarity.isAtLeast(config.minimumRarity)) return

		val text = if(config.nameInsteadOfRarity) {
			"${seaCreature.displayName}!"
		} else {
			"${seaCreature.rarity.displayName} Catch!"
		}

		RenderUtils.drawTitle(text.toText().formatted(seaCreature.rarity.color.toFormatting()))
	}

	private fun isEnabled() = SkyBlockAPI.inSkyBlock && config.enabled
}