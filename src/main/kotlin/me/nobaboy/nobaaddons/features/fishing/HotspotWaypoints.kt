package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.toShortString
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.entity.decoration.ArmorStandEntity
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object HotspotWaypoints {
	private val config get() = NobaConfig.fishing
	private val enabled: Boolean get() = config.hotspotWaypoints && SkyBlockAPI.inSkyBlock

	// TODO: At some point when a player stats core file is made, use that instead
	private val hotspotColors = mutableMapOf(
		"Double Hook Chance" to NobaColor.BLUE,
		"Sea Creature Chance" to NobaColor.DARK_AQUA,
		"Fishing Speed" to NobaColor.AQUA,
		"Treasure Chance" to NobaColor.GOLD,
		"Trophy Fish Chance" to NobaColor.GOLD
	)

	private val hotspots = mutableListOf<Hotspot>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		EntityEvents.POST_RENDER.register(this::onEntityRender)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onEntityRender(event: EntityEvents.Render) {
		if(!enabled) return

		val armorStand = event.entity as? ArmorStandEntity ?: return
		if(MCUtils.player?.canSee(armorStand) != true) return

		if(hotspots.any { it.armorStand == armorStand }) return
		if(armorStand.name.string != "HOTSPOT") return

		val statsArmorString = EntityUtils.getNextEntity<ArmorStandEntity>(armorStand, 1) ?: return
		val color = getHotspotColor(statsArmorString.name.string)

		val timestamp = Timestamp.now() + 4.5.minutes - (armorStand.age / 20).seconds
		hotspots.add(Hotspot(armorStand, color, timestamp))
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!enabled) return

		hotspots.removeIf { !it.isValid }
		hotspots.forEach {
			val time = it.remainingTime.ifEmpty { "Soon" }

			RenderUtils.renderBeaconBeam(context, it.location, it.color)
			RenderUtils.renderText(context, it.location.center().raise(1.5), time, throughBlocks = true)
		}
	}

	private fun reset() {
		hotspots.clear()
	}

	private fun getHotspotColor(name: String) =
		hotspotColors.entries.firstOrNull { name.contains(it.key) }?.value ?: NobaColor.DARK_GRAY

	private data class Hotspot(val armorStand: ArmorStandEntity, val color: NobaColor, val timestamp: Timestamp) {
		val location = armorStand.pos.toNobaVec().lower(2).roundToBlock()

		val isValid: Boolean get() = armorStand.isAlive
		val remainingTime: String get() = timestamp.timeRemaining().toShortString()
	}
}