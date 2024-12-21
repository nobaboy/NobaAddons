package me.nobaboy.nobaaddons.features.slayers

import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.chat.ChatUtils

object AnnounceBossKillTime {
	private val config = NobaConfigManager.config.slayers.announceBossKillTime
	private val enabled: Boolean get() = SlayerAPI.currentQuest != null && config.enabled

	fun init() {
		SlayerEvents.BOSS_KILL.register(this::onBossKill)
	}

	private fun onBossKill(event: SlayerEvents.BossKill) {
		if(!enabled) return
		val seconds = getBossKillTime(event) ?: return

		ChatUtils.addMessage("Slayer Boss took $seconds seconds to kill!")
	}

	private fun getBossKillTime(event: SlayerEvents.BossKill): Double? {
		if(event.entity == null) return null
		val killTime = event.entity.age / 20.0

		when(config.timeSource) {
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