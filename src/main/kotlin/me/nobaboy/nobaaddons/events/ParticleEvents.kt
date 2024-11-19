package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.CancelableEvent
import me.nobaboy.nobaaddons.events.internal.CancelableEventDispatcher
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.particle.ParticleType

object ParticleEvents {
	@JvmField
	val PARTICLE = EventDispatcher<Particle>()

	@JvmField
	val ALLOW_PARTICLE = CancelableEventDispatcher<AllowParticle>()

	data class Particle(
		val type: ParticleType<*>,
		val location: NobaVec,
		val count: Int,
		val speed: Float,
		val offset: NobaVec,
		val isLongDistance: Boolean
	)

	data class AllowParticle(
		val type: ParticleType<*>,
		val location: NobaVec,
		val count: Int,
		val speed: Float,
		val offset: NobaVec,
		val isLongDistance: Boolean
	) : CancelableEvent()
}