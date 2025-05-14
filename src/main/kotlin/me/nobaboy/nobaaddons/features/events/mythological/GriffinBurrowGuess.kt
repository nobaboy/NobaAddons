package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.skyblock.events.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.EventDispatcher.Companion.registerIf
import me.nobaboy.nobaaddons.events.impl.interact.BlockInteractionEvent
import me.nobaboy.nobaaddons.events.impl.interact.GenericInteractEvent
import me.nobaboy.nobaaddons.events.impl.interact.ItemUseEvent
import me.nobaboy.nobaaddons.events.impl.render.ParticleEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyBlockId
import me.nobaboy.nobaaddons.utils.math.ParticlePathFitter
import net.minecraft.particle.ParticleTypes
import kotlin.time.Duration.Companion.seconds

object GriffinBurrowGuess {
	private val config get() = NobaConfig.events.mythological
	private val enabled: Boolean get() = config.burrowGuess && DianaAPI.isActive

	private val particlePath = ParticlePathFitter(3)
	private var lastAbilityUse = Timestamp.distantPast()

	init {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		ParticleEvents.PARTICLE.register(this::onParticle)
		ItemUseEvent.EVENT.register(this::onItemUse)
		BlockInteractionEvent.EVENT.registerIf<BlockInteractionEvent.Interact>(this::onItemUse)
	}

	private fun onParticle(event: ParticleEvents.Particle) {
		if(!enabled) return
		if(event.type != ParticleTypes.DRIPPING_LAVA) return
		if(event.speed != -0.5f || event.count != 2) return
		if(lastAbilityUse.elapsedSince() > 3.seconds) return

		val location = event.location
		val lastPoint = particlePath.lastPoint
		if(lastPoint != null && lastPoint.distanceSq(location) > 9) return

		particlePath.addPoint(location)
		val guessLocation = particlePath.solve() ?: return

		MythologicalEvents.BURROW_GUESS.dispatch(MythologicalEvents.BurrowGuess(guessLocation.lower(0.5).roundToBlock()))
	}

	private fun onItemUse(event: GenericInteractEvent) {
		if(!enabled) return
		if(lastAbilityUse.elapsedSince() <= 3.seconds) return
		if(event.itemInHand.skyBlockId != DianaAPI.SPADE) return

		particlePath.reset()
		lastAbilityUse = Timestamp.now()
	}

	private fun reset() {
		particlePath.reset()
	}
}