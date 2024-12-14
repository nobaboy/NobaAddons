package me.nobaboy.nobaaddons.features.slayer

import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.events.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.EntityUtils
import net.minecraft.entity.Entity

// Could temporarily do with highlighting the same way as Thunder Sparks, idk what you'll do
object HighlightMiniBosses {
	private val config get() = NobaConfigManager.config.slayers.highlightMiniBosses

	private val miniBosses = mutableListOf<Entity>()

	fun init() {
		SecondPassedEvent.EVENT.register { onSecondPassed() }
		SlayerEvents.MINIBOSS_SPAWN.register(this::onMiniBossSpawn)
	}

	private fun onSecondPassed() {
		miniBosses.removeIf { !it.isAlive || EntityUtils.getEntityById(it.id) !== it }
	}

	private fun onMiniBossSpawn(event: SlayerEvents.MiniBossSpawn) {
		if(!isEnabled()) return

		val entity = event.entity
		if(entity in miniBosses) return

		miniBosses.add(entity)
	}

	private fun isEnabled() = SlayerAPI.questActive && config.enabled
}