package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.utils.NobaVec
import net.fabricmc.fabric.api.event.EventFactory
import net.minecraft.util.Identifier

object PlaySoundEvent {
	@JvmField
	val ALLOW_SOUND = EventFactory.createArrayBacked(AllowSound::class.java) { listeners ->
		AllowSound { identifier, location, pitch, volume ->
			listeners.all { it.onSound(identifier, location, pitch, volume) }
		}
	}

	@JvmField
	val SOUND = EventFactory.createArrayBacked(Sound::class.java) { listeners ->
		Sound { identifier, location, pitch, volume ->
			listeners.forEach { it.onSound(identifier, location, pitch, volume) }
		}
	}

	fun interface AllowSound {
		fun onSound(sound: Identifier, location: NobaVec, pitch: Float, volume: Float): Boolean
	}

	fun interface Sound {
		fun onSound(sound: Identifier, location: NobaVec, pitch: Float, volume: Float)
	}
}