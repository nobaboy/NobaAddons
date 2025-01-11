package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.ducks.FishingBobberTimerDuck
import me.nobaboy.nobaaddons.events.EntityEvents
import me.nobaboy.nobaaddons.events.EntityNametagRenderEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.asTimestamp
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.minecraft.entity.Entity
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.text.Text
import kotlin.time.DurationUnit

object FishingBobberTweaks {
	private val renderConfig get() = NobaConfig.INSTANCE.uiAndVisuals.renderingTweaks
	private val fishingConfig get() = NobaConfig.INSTANCE.fishing

	private val GREEN = NobaColor.GREEN.rgb
	private val GOLD = NobaColor.GOLD.rgb

	fun init() {
		EntityEvents.ALLOW_RENDER.register(this::onEntityRender)
		EntityNametagRenderEvents.VISIBILITY.register(this::allowNameTag)
		EntityNametagRenderEvents.EVENT.register(this::renderTimer)
		ClientEntityEvents.ENTITY_LOAD.register { entity, _ -> onEntityLoad(entity) }
	}

	private fun onEntityRender(event: EntityEvents.AllowRender) {
		val entity = event.entity as? FishingBobberEntity ?: return
		if(!renderConfig.hideOtherPeopleFishing) return
		if(entity.isOurs) return

		event.cancel()
	}

	private fun allowNameTag(event: EntityNametagRenderEvents.Visibility) {
		val entity = event.entity as? FishingBobberEntity ?: return
		if(!fishingConfig.bobberTimer.enabled) return
		if(!SkyBlockAPI.inSkyBlock) return
		if(!SkyBlockIsland.CRIMSON_ISLE.inIsland() && fishingConfig.bobberTimer.crimsonIsleOnly) return
		if(!entity.isOurs) return
		if((entity as FishingBobberTimerDuck).`nobaaddons$spawnedAt`() == null) return

		event.shouldRender = true
	}

	private fun renderTimer(event: EntityNametagRenderEvents.Nametag) {
		val entity = event.entity as? FishingBobberEntity ?: return
		if(!fishingConfig.bobberTimer.enabled) return
		if(!entity.isOurs) return

		val time = (entity as FishingBobberTimerDuck).`nobaaddons$spawnedAt`() ?: return
		val seconds = time.asTimestamp().elapsedSince().toDouble(DurationUnit.SECONDS)

		val slugTime = PetAPI.currentPet?.takeIf { it.id == "SLUG" }?.let {
			20.0 - it.level * 0.1
		} ?: 20.0

		val color: Int = if(seconds >= slugTime) GOLD else GREEN
		event.renderEntityName = false
		event.tags.add(Text.literal(seconds.roundTo(1).toString()).withColor(color))
	}

	private fun onEntityLoad(entity: Entity) {
		if(entity !is FishingBobberEntity) return
		(entity as FishingBobberTimerDuck).`nobaaddons$markSpawnTime`()
	}

	private val FishingBobberEntity.isOurs: Boolean
		get() = this.owner == MCUtils.player
}