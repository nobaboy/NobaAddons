package me.nobaboy.nobaaddons.features.events.hoppity

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.events.hoppity.HoppityAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.events.HoppityData
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.interact.ItemUseEvent
import me.nobaboy.nobaaddons.events.impl.render.ParticleEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyBlockId
import me.nobaboy.nobaaddons.utils.math.ParticlePathFitter
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.particle.ParticleTypes
import kotlin.time.Duration.Companion.seconds

object HoppityEggGuess {
	private val config get() = NobaConfig.events.hoppity
	private val enabled: Boolean get() = config.eggGuess && HoppityAPI.isActive

	private val particlePath = ParticlePathFitter(3)
	private var guessLocation: NobaVec? = null

	private var lastAbilityUse = Timestamp.distantPast()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		ParticleEvents.PARTICLE.register(this::onParticle)
		ItemUseEvent.EVENT.register(this::onItemUse)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onParticle(event: ParticleEvents.Particle) {
		if(!enabled) return
		if(event.type != ParticleTypes.HAPPY_VILLAGER) return
		if(event.speed != 0f || event.count != 1) return
		if(lastAbilityUse.elapsedSince() > 5.seconds) return

		val location = event.location
		val lastPoint = particlePath.lastPoint
		if(lastPoint != null && lastPoint.distanceSq(location) > 9) return

		particlePath.addPoint(location)
		guessLocation = guessEggLocation()
	}

	private fun onItemUse(event: ItemUseEvent) {
		if(!enabled) return
		if(lastAbilityUse.elapsedSince() <= 5.seconds) return
		if(event.itemInHand.skyBlockId != HoppityAPI.LOCATOR) return

		particlePath.reset()
		lastAbilityUse = Timestamp.now()
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!enabled) return
		if(event.cleaned.startsWith("HOPPITY'S HUNT You found a Chocolate")) guessLocation = null
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!enabled) return

		guessLocation?.let {
			val distance = it.distanceToPlayer()
			val formattedDistance = distance.toInt().addSeparators()

			RenderUtils.renderWaypoint(context, it, NobaColor.AQUA, throughBlocks = true)
			RenderUtils.renderText(
				context,
				it.center().raise(),
				tr("nobaaddons.events.hoppity.eggGuessWaypoint", "Egg Guess"),
				color = NobaColor.AQUA,
				yOffset = -10f,
				throughBlocks = true
			)
			RenderUtils.renderText(
				context,
				it.center().raise(),
				"${formattedDistance}m",
				color = NobaColor.GRAY,
				hideThreshold = 5.0,
				throughBlocks = true
			)
		}
	}

	private fun guessEggLocation(): NobaVec? {
		val eggLocations = HoppityData.getEggsByIsland(SkyBlockAPI.currentIsland) ?: return null
		val guessLocation = particlePath.solve() ?: return null

		return eggLocations.minByOrNull { it.distanceSq(guessLocation) }
	}

	private fun reset() {
		particlePath.reset()
		guessLocation = null
	}
}