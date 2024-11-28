package me.nobaboy.nobaaddons.events.skyblock

import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import me.nobaboy.nobaaddons.utils.Timestamp
import net.minecraft.entity.Entity

object SlayerEvents {
	val MINIBOSS_SPAWN = EventDispatcher<MiniBossSpawn>()

	val BOSS_SPAWN = EventDispatcher<BossSpawn>()

	val BOSS_KILL = EventDispatcher<BossKill>()

	data class BossKill(val timestamp: Timestamp)
	data class MiniBossSpawn(val entity: Entity)
	data class BossSpawn(val entity: Entity, val boss: SlayerBoss, val timestamp: Timestamp)
}