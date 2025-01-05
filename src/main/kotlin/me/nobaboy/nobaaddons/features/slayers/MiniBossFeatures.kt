package me.nobaboy.nobaaddons.features.slayers

import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.tr
import kotlin.time.Duration.Companion.seconds

// TODO: Add miniboss highlighting
object MiniBossFeatures {
	private val config get() = NobaConfigManager.config.slayers
	private val enabled: Boolean get() = config.miniBossAlert.enabled && SlayerAPI.currentQuest?.spawned == false

	fun init() {
		SlayerEvents.MINI_BOSS_SPAWN.register(this::onMiniBossSpawn)
	}

	private fun onMiniBossSpawn(event: SlayerEvents.MiniBossSpawn) {
		if(!enabled) return

		val distance = event.entity.getNobaVec().distanceToPlayer()
		if(distance > 16) return

		RenderUtils.drawTitle(tr("nobaaddons.slayers.miniBossAlert.spawned", "MiniBoss Spawned!"), config.miniBossAlert.alertColor.toNobaColor(), duration = 1.5.seconds)
		SoundUtils.dingLowSound.play()
	}
}