package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.CancelableEvent
import me.nobaboy.nobaaddons.events.internal.CancelableEventDispatcher
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.particle.ParticleType

object ParticleEvents {
	/**
	 * Event invoked to determine whether a given [ParticleType] should be allowed to render.
	 */
	@JvmField val ALLOW_PARTICLE = CancelableEventDispatcher<AllowParticle>()

	/**
	 * Event invoked after a [ParticleType] is rendered.
	 */
	@JvmField val PARTICLE = EventDispatcher<Particle>()

	data class AllowParticle(
		val type: ParticleType<*>,
		val location: NobaVec,
		val count: Int,
		val speed: Float,
		val offset: NobaVec,
		val isLongDistance: Boolean
	) : CancelableEvent()

	data class Particle(
		val type: ParticleType<*>,
		val location: NobaVec,
		val count: Int,
		val speed: Float,
		val offset: NobaVec,
		val isLongDistance: Boolean
	)
}