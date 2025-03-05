package me.nobaboy.nobaaddons.events.impl.skyblock

import me.nobaboy.nobaaddons.events.AbstractEvent
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity

object SlayerEvents {
	val QUEST_CLEAR = EventDispatcher<QuestClear>()

	val BOSS_SPAWN = EventDispatcher<BossSpawn>()

	val BOSS_KILL = EventDispatcher<BossKill>()

	val MINI_BOSS_SPAWN = EventDispatcher<MiniBossSpawn>()

	class QuestClear : AbstractEvent()
	class BossSpawn : AbstractEvent()
	data class BossKill(val entity: LivingEntity?, val timerEntity: ArmorStandEntity?) : AbstractEvent()
	data class MiniBossSpawn(val entity: LivingEntity) : AbstractEvent()
}