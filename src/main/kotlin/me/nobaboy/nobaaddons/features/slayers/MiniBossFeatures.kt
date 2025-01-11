package me.nobaboy.nobaaddons.features.slayers

import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.EntityOverlay.highlight
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.entity.LivingEntity
import kotlin.time.Duration.Companion.seconds

object MiniBossFeatures {
	private val config get() = NobaConfig.INSTANCE.slayers
	private val enabled: Boolean get() = SlayerAPI.currentQuest?.spawned == false

	fun init() {
		SlayerEvents.MINI_BOSS_SPAWN.register(this::onFindMiniBoss)
	}

	private fun onFindMiniBoss(event: SlayerEvents.MiniBossSpawn) {
		if(!enabled) return

		val entity = event.entity
		if(config.miniBossAlert.enabled) handleMiniBossAlert(entity)
		if(config.highlightMiniBosses.enabled) entity.highlight(config.highlightMiniBosses.highlightColor)
	}

	private fun handleMiniBossAlert(entity: LivingEntity) {
		if(entity.age > 20) return

		val distance = entity.getNobaVec().distanceToPlayer()
		if(distance > 16) return

		RenderUtils.drawTitle(tr("nobaaddons.slayers.miniBossAlert.spawned", "MiniBoss Spawned!"), config.miniBossAlert.alertColor, duration = 1.5.seconds, id = "slayer.alert")
		SoundUtils.dingLowSound.play()
	}
}