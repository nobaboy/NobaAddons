package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.ducks.FishingBobberTimerDuck
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.render.EntityNametagRenderEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.asTimestamp
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.text.Text
import kotlin.time.DurationUnit

object FishingBobberTweaks {
	private val config get() = NobaConfig.fishing

	private val GREEN = NobaColor.GREEN.rgb
	private val GOLD = NobaColor.GOLD.rgb

	fun init() {
		EntityEvents.SPAWN.register(this::onEntitySpawn)
		EntityEvents.ALLOW_RENDER.register(this::onEntityRender)
		EntityNametagRenderEvents.VISIBILITY.register(this::allowNameTag)
		EntityNametagRenderEvents.EVENT.register(this::renderTimer)
	}

	private fun onEntitySpawn(event: EntityEvents.Spawn) {
		val entity = event.entity as? FishingBobberEntity ?: return
		(entity as FishingBobberTimerDuck).`nobaaddons$markSpawnTime`()
	}

	private fun onEntityRender(event: EntityEvents.AllowRender) {
		val entity = event.entity as? FishingBobberEntity ?: return
		if(!config.hideOtherPeopleFishing) return
		if(entity.isOurs) return

		event.cancel()
	}

	private fun allowNameTag(event: EntityNametagRenderEvents.Visibility) {
		val entity = event.entity as? FishingBobberEntity ?: return
		if(!config.bobberTimer.enabled) return
		if(!SkyBlockAPI.inSkyBlock) return
		if(!SkyBlockIsland.CRIMSON_ISLE.inIsland() && config.bobberTimer.crimsonIsleOnly) return
		if(!entity.isOurs) return
		if((entity as FishingBobberTimerDuck).`nobaaddons$spawnedAt`() == null) return

		event.shouldRender = true
	}

	private fun renderTimer(event: EntityNametagRenderEvents.Nametag) {
		val entity = event.entity as? FishingBobberEntity ?: return
		if(!config.bobberTimer.enabled) return
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

	private val FishingBobberEntity.isOurs: Boolean
		get() = this.owner == MCUtils.player
}