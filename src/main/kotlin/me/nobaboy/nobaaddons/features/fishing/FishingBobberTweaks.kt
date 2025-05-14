package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.render.EntityNametagRenderEvents
import me.nobaboy.nobaaddons.events.impl.render.RenderStateUpdateEvent
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.render.EntityDataKey
import net.minecraft.client.render.entity.state.FishingBobberEntityState
import net.minecraft.entity.projectile.FishingBobberEntity
import net.minecraft.text.Text
import kotlin.time.DurationUnit

object FishingBobberTweaks {
	private val SPAWNED_AT = EntityDataKey.nullable<Timestamp?>()
	private val OUR_BOBBER = EntityDataKey<Boolean> { false }

	private val config get() = NobaConfig.fishing

	private val GREEN = NobaColor.GREEN.rgb
	private val GOLD = NobaColor.GOLD.rgb

	init {
		RenderStateUpdateEvent.EVENT.register(this::updateRenderState)
		EntityEvents.SPAWN.register(this::onEntitySpawn)
		EntityEvents.ALLOW_RENDER.register(this::onEntityRender)
		EntityNametagRenderEvents.VISIBILITY.register(this::allowNameTag)
		EntityNametagRenderEvents.EVENT.register(this::renderTimer)
	}

	private fun updateRenderState(event: RenderStateUpdateEvent) {
		if(event.entity is FishingBobberEntity) {
			event.copyToRender(SPAWNED_AT)
			event.set(OUR_BOBBER, event.entity.isOurs)
		}
	}

	private fun onEntitySpawn(event: EntityEvents.Spawn) {
		if(event.entity is FishingBobberEntity) {
			SPAWNED_AT.put(event.entity, Timestamp.now())
		}
	}

	private fun onEntityRender(event: EntityEvents.AllowRender) {
		if(!config.hideOtherPeopleFishing) return
		if(!OUR_BOBBER.get(event.state)) return

		event.cancel()
	}

	private fun allowNameTag(event: EntityNametagRenderEvents.Visibility) {
		val entity = event.entity as? FishingBobberEntity ?: return
		if(!config.bobberTimer.enabled) return
		if(!SkyBlockAPI.inSkyBlock) return
		if(!SkyBlockIsland.CRIMSON_ISLE.inIsland() && config.bobberTimer.crimsonIsleOnly) return
		if(!entity.isOurs || SPAWNED_AT.get(entity) == null) return

		event.shouldRender = true
	}

	private fun renderTimer(event: EntityNametagRenderEvents.Nametag) {
		val state = event.state as? FishingBobberEntityState ?: return
		if(!config.bobberTimer.enabled) return
		if(!OUR_BOBBER.get(state)) return

		val time = SPAWNED_AT.get(state) ?: return
		val seconds = time.elapsedSince().toDouble(DurationUnit.SECONDS)

		val slugTime = PetAPI.currentPet?.takeIf { it.id == "SLUG" }?.let {
			20.0 - it.level * 0.1
		} ?: 20.0

		val color: Int = if(seconds >= slugTime) GOLD else GREEN

		event.tags.add(Text.literal(seconds.roundTo(1).toString()).withColor(color))
	}

	private val FishingBobberEntity.isOurs: Boolean
		get() = this.owner == MCUtils.player
}