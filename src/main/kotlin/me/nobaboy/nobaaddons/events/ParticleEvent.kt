package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.data.jsonobjects.ParticleData
import net.fabricmc.fabric.api.event.EventFactory

fun interface ParticleEvent {
	fun onParticle(particle: ParticleData)

	companion object {
		@JvmField
		val EVENT = EventFactory.createArrayBacked(ParticleEvent::class.java) { listeners ->
			ParticleEvent { particle ->
				listeners.forEach { it.onParticle(particle) }
			}
		}
	}
}