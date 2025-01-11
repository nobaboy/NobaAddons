package me.nobaboy.nobaaddons.features.slayers.voidgloom

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.EntityEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.Entity
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

object BrokenHeartRadiationTimer {
	private val config get() = NobaConfig.INSTANCE.slayers.voidgloom
	private val enabled: Boolean
		get() = config.brokenHeartRadiationTimer && SkyBlockIsland.THE_END.inIsland() &&
			SlayerAPI.currentQuest?.let { it.boss == SlayerBoss.VOIDGLOOM && it.spawned } == true

	private var brokenHeartRadiation: BrokenHeartRadiation? = null

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { brokenHeartRadiation = null }
		EntityEvents.VEHICLE_CHANGE.register(this::onEntityVehicleChange)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::onWorldRender)
	}

	private fun onEntityVehicleChange(event: EntityEvents.VehicleChange) {
		if(!enabled) return

		val bossEntity = SlayerAPI.currentQuest?.entity ?: return
		if(bossEntity.vehicle == null) return // This is fairly useless but does sometimes??? prevent false timers
		if(event.entity != bossEntity) return

		if(brokenHeartRadiation == null) brokenHeartRadiation = BrokenHeartRadiation(bossEntity)
	}

	@Suppress("unused")
	private fun onWorldRender(context: WorldRenderContext) {
		if(!enabled) return

		brokenHeartRadiation?.let {
			if(!it.isValid) {
				brokenHeartRadiation = null
				return
			}

			RenderUtils.renderText(it.bossEntity.getNobaVec().raise(1.5), it.remainingTime, NobaColor.GOLD)
		}
	}

	data class BrokenHeartRadiation(
		val bossEntity: Entity,
		val timestamp: Timestamp = Timestamp.now() + 8.seconds
	) {
		val isValid: Boolean
			get() = timestamp.timeRemaining() > 0.seconds && (bossEntity.vehicle != null || timestamp.timeRemaining() > 5.seconds)

		val remainingTime: String get() = timestamp.timeRemaining().toString(DurationUnit.SECONDS, 1)
	}
}