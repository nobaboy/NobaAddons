package me.nobaboy.nobaaddons.features.slayers.inferno

import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.mc.EntityUtils
import me.nobaboy.nobaaddons.utils.render.EntityOverlay.highlight
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.mob.WitherSkeletonEntity
import net.minecraft.entity.mob.ZombifiedPiglinEntity

// TODO: Allow entity highlight to work on armor layer
object HighlightHellionShield {
	private val config get() = NobaConfig.slayers.inferno
	private val enabled: Boolean
		get() = config.highlightHellionShield && SlayerAPI.currentQuest?.let { it.boss == SlayerBoss.INFERNO && it.spawned } == true

	private val armorStands = mutableSetOf<LivingEntity>()

	init {
		TickEvents.TICK.register { onTick() }
		EntityEvents.SPAWN.register(this::onEntitySpawn)
	}

	private fun onTick() {
		if(!enabled) return

		val currentQuest = SlayerAPI.currentQuest ?: return

		currentQuest.timerArmorStand?.let {
			val color = HellionShield.getByName(it.name.string)?.color ?: return@let
			currentQuest.entity?.highlight(color)
		}

		armorStands.removeIf { !it.isAlive }
		armorStands.forEach {
			val color = HellionShield.getByName(it.name.string)?.color ?: return@forEach
			it.highlight(color)
		}
	}

	private fun onEntitySpawn(event: EntityEvents.Spawn) {
		if(!enabled) return

		val bossEntity = SlayerAPI.currentQuest?.entity ?: return

		val entity = event.entity as? LivingEntity ?: return
		if(entity !is ZombifiedPiglinEntity && entity !is WitherSkeletonEntity) return
		if(bossEntity.getNobaVec().distance(entity.getNobaVec()) !in 3.0..3.3) return

		val armorStand = EntityUtils.getNextEntity(entity, 2) as? ArmorStandEntity ?: return
		armorStands.add(armorStand)
	}
}