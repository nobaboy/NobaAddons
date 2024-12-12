package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.ducks.FishingBobberTimerDuck
import me.nobaboy.nobaaddons.events.EntityRenderEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.asTimestamp
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.minecraft.client.world.ClientWorld
import net.minecraft.entity.Entity
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.util.math.ColorHelper
import net.minecraft.util.math.MathHelper
import kotlin.time.DurationUnit

object FishingBobberTweaks {
	private val renderConfig get() = NobaConfigManager.config.uiAndVisuals.renderingTweaks
	private val fishingConfig get() = NobaConfigManager.config.fishing

	private val GREEN = NobaColor.GREEN.rgb
	private val GOLD = NobaColor.GOLD.rgb

	fun init() {
		EntityRenderEvents.ALLOW_RENDER.register(this::hideOthers)
		EntityRenderEvents.POST_RENDER.register(this::renderTimer)
		ClientEntityEvents.ENTITY_LOAD.register(this::onSpawn)
	}

	private fun onSpawn(entity: Entity, @Suppress("unused") world: ClientWorld) {
		if(entity !is FishingBobberEntity) return
		(entity as FishingBobberTimerDuck).`nobaaddons$markSpawnTime`()
	}

	private fun hideOthers(event: EntityRenderEvents.AllowRender) {
		val entity = event.entity as? FishingBobberEntity ?: return
		if(renderConfig.hideOtherPeopleFishing && !entity.isOurs) {
			event.cancel()
			return
		}
	}

	private fun renderTimer(event: EntityRenderEvents.Render) {
		val entity = event.entity as? FishingBobberEntity ?: return
		if(!fishingConfig.showBobberTimer || !entity.isOurs) return

		val time = ((entity as FishingBobberTimerDuck).`nobaaddons$spawnedAt`() ?: return).asTimestamp()

		val seconds = time.elapsedSince().toDouble(DurationUnit.SECONDS)
		val slugTime = if(PetAPI.currentPet?.let { it.id == "SLUG" } == true) 10 else 20

		val color: Int
		if(fishingConfig.lerpBobberTimer) {
			val delta = MathHelper.clamp(seconds / slugTime.toDouble(), 0.0, 1.0).toFloat()
			color = ColorHelper/*? if <1.21.2 {*//*.Argb*//*?}*/.lerp(delta, GREEN, GOLD)
		} else {
			color = if(seconds >= slugTime) GOLD else GREEN
		}

		RenderUtils.renderText(
			null,
			entity.pos.toNobaVec().add(y = 0.5),
			text = seconds.roundTo(1).toString(),
			color = color,
			hideThreshold = 0.0, // noba wtf is this?
			shadow = /*? if >=1.21.2 {*/true/*?} else {*//*false*//*?}*/,
			throughBlocks = true
		)
	}

	private val FishingBobberEntity.isOurs: Boolean get() = this.owner == MCUtils.player
}