package me.nobaboy.nobaaddons.features.slayers

import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import kotlin.time.Duration.Companion.seconds

object MiniBossAlert {
	private val config get() = NobaConfigManager.config.slayers.miniBossAlert
	private val enabled: Boolean get() = SlayerAPI.currentQuest?.spawned == false && config.enabled

	fun init() {
		SlayerEvents.MINI_BOSS_SPAWN.register(this::onMiniBossSpawn)
	}

	private fun onMiniBossSpawn(event: SlayerEvents.MiniBossSpawn) {
		if(!enabled) return

		val distance = event.entity.getNobaVec().distanceToPlayer()
		if(distance > 16) return

		RenderUtils.drawTitle(config.alertText, config.alertColor, 1.5.seconds)
		SoundUtils.dingSound.play()
	}
}