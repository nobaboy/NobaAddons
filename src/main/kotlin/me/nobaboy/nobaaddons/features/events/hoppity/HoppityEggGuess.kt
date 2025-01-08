package me.nobaboy.nobaaddons.features.events.hoppity

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.events.hoppity.HoppityAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.hoppity.HoppityEgg
import me.nobaboy.nobaaddons.events.ParticleEvents
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItemId
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.ActionResult
import kotlin.time.Duration.Companion.seconds

object HoppityEggGuess {
	private val config get() = NobaConfig.INSTANCE.events.hoppity
	private val enabled: Boolean get() = config.eggGuess && HoppityAPI.isActive

	private var lastAbilityUse = Timestamp.distantPast()
	private val particleLocations = mutableListOf<NobaVec>()
	private var guessLocation: NobaVec? = null

	fun init() {
		SecondPassedEvent.EVENT.register { onSecondPassed() }
		ParticleEvents.PARTICLE.register(this::onParticle)
		UseItemCallback.EVENT.register { player, _, _ -> onUseItem(player) }
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onSecondPassed() {
		if(!enabled) return
		if(lastAbilityUse.elapsedSince() > 2.seconds) particleLocations.clear()
	}

	private fun onParticle(event: ParticleEvents.Particle) {
		if(!enabled) return
		if(event.type != ParticleTypes.HAPPY_VILLAGER) return
		if(lastAbilityUse.elapsedSince() > 2.seconds) return

		val location = event.location
		particleLocations.add(location)
		if(particleLocations.size < 2) return

		val first = particleLocations.first()
		val last = particleLocations.last()

		val direction = (last - first).normalize()
		val end = first + direction * 512

		val eggLocations = HoppityEgg.getByIsland(SkyBlockAPI.currentIsland) ?: return
		guessLocation = eggLocations.minByOrNull {
			it.distanceToLine(first, end)
		}
	}

	private fun onUseItem(player: PlayerEntity): ActionResult {
		if(!enabled) return ActionResult.PASS

		val itemId = player.mainHandStack.getSkyBlockItemId() ?: return ActionResult.PASS
		if(itemId != HoppityAPI.LOCATOR) return ActionResult.PASS

		lastAbilityUse = Timestamp.now()
		return ActionResult.PASS
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!enabled) return

		guessLocation?.let {
			val distance = it.distanceToPlayer()

			RenderUtils.renderWaypoint(context, it, NobaColor.AQUA, throughBlocks = distance > 5)
			RenderUtils.renderText(it.center().raise(), "Egg Guess", NobaColor.AQUA, yOffset = -10.0f, throughBlocks = true)

			if(distance > 5) {
				val formattedDistance = distance.toInt().addSeparators()
				RenderUtils.renderText(it.center().raise(), "${formattedDistance}m", NobaColor.GRAY, hideThreshold = 5.0, throughBlocks = true)
			}
		}
	}
}