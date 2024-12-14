package me.nobaboy.nobaaddons.features.slayer

import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.render.RenderUtils

object MiniBossAlert {
	private val config get() = NobaConfigManager.config.slayers.miniBossAlert

	fun init() {
		SlayerEvents.MINIBOSS_SPAWN.register(this::onMiniBossSpawn)
	}

	private fun onMiniBossSpawn(event: SlayerEvents.MiniBossSpawn) {
		if(!isEnabled()) return

		RenderUtils.drawTitle(config.alertText, config.alertColor)
	}

	private fun isEnabled() = SlayerAPI.questActive && config.enabled
}