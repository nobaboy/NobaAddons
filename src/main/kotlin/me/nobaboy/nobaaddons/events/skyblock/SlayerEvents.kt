package me.nobaboy.nobaaddons.events.skyblock

import me.nobaboy.nobaaddons.events.internal.Event
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import net.minecraft.entity.Entity

object SlayerEvents {
	val MINI_BOSS_SPAWN = EventDispatcher<MiniBossSpawn>()

	val BOSS_KILL = EventDispatcher<BossKill>()

	data class MiniBossSpawn(val entity: Entity) : Event()
	data class BossKill(val entity: Entity?, val timerEntity: Entity?) : Event()
}