package me.nobaboy.nobaaddons.events.skyblock

import me.nobaboy.nobaaddons.events.internal.Event
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity

object SlayerEvents {
	val BOSS_SPAWN = EventDispatcher<BossSpawn>()
	val BOSS_KILL = EventDispatcher<BossKill>()

	val FIND_BOSS = EventDispatcher<Find>()
	val FIND_MINI_BOSS = EventDispatcher<Find>()

	class BossSpawn() : Event()
	data class BossKill(val entity: LivingEntity?, val timerEntity: ArmorStandEntity?) : Event()
	data class Find(val entity: LivingEntity) : Event()
}