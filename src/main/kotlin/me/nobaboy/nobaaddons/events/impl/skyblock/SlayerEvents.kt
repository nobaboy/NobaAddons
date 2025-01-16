package me.nobaboy.nobaaddons.events.impl.skyblock

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity

object SlayerEvents {
	val BOSS_SPAWN = EventDispatcher<BossSpawn>()
	val BOSS_KILL = EventDispatcher<BossKill>()

	val MINI_BOSS_SPAWN = EventDispatcher<MiniBossSpawn>()

	class BossSpawn() : Event()
	data class BossKill(val entity: LivingEntity?, val timerEntity: ArmorStandEntity?) : Event()
	data class MiniBossSpawn(val entity: LivingEntity) : Event()
}