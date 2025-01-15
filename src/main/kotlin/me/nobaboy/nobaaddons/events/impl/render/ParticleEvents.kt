package me.nobaboy.nobaaddons.events.impl.render

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.particle.ParticleType

object ParticleEvents {
	/**
	 * Event invoked to determine whether a given [ParticleType] should be allowed to render.
	 */
	@JvmField val ALLOW_PARTICLE = EventDispatcher.cancelable<AllowParticle>()

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
		val forceSpawn: Boolean
	): Event(isCancelable = true)

	data class Particle(
		val type: ParticleType<*>,
		val location: NobaVec,
		val count: Int,
		val speed: Float,
		val offset: NobaVec,
		val forceSpawn: Boolean
	) : Event()
}