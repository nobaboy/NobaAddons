package me.nobaboy.nobaaddons.features.slayers

import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.tr
import kotlin.time.Duration.Companion.seconds

object SlayerBossFeatures {
	private val config get() = NobaConfig.INSTANCE.slayers
	private val enabled: Boolean get() = SlayerAPI.currentQuest != null

	fun init() {
		SlayerEvents.BOSS_SPAWN.register(this::onBossSpawn)
		SlayerEvents.BOSS_KILL.register(this::onBossKill)
	}

	private fun onBossSpawn(event: SlayerEvents.BossSpawn) {
		if(!config.bossAlert.enabled) return
		if(!enabled) return

		RenderUtils.drawTitle(tr("nobaaddons.slayers.bossAlert.spawned", "Boss Spawned!"), config.bossAlert.alertColor, duration = 1.5.seconds)
		SoundUtils.dingLowSound.play()
	}

	private fun onBossKill(event: SlayerEvents.BossKill) {
		if(!config.announceBossKillTime.enabled) return
		if(!enabled) return

		val seconds = getBossKillTime(event) ?: return
		ChatUtils.addMessage(tr("nobaaddons.slayers.announceBossKillTime.kill", "Slayer Boss took $seconds seconds to kill!"))
	}

	private fun getBossKillTime(event: SlayerEvents.BossKill): Double? {
		if(event.entity == null) return null
		val killTime = event.entity.age / 20.0

		when(config.announceBossKillTime.timeSource) {
			BossTimeSource.REAL_TIME -> return killTime
			BossTimeSource.BOSS_TIME_REMAINING -> {
				val timer = event.timerEntity?.name?.string
				if(timer.isNullOrEmpty()) return killTime

				val timerParts = timer.split(":").mapNotNull { it.toIntOrNull() }
				if(timerParts.size != 2) return killTime

				val maxTime = 3 * 60.0
				val remainingTime = timerParts[0] * 60.0 + timerParts[1]

				return maxTime - remainingTime
			}
		}

		return null
	}
}