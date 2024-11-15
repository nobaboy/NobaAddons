package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.data.SoundData
import net.fabricmc.fabric.api.event.EventFactory

object PlaySoundEvent {
	@JvmField
	val ALLOW_SOUND = EventFactory.createArrayBacked(AllowSound::class.java) { listeners ->
		AllowSound { sound ->
			listeners.all { it.onSound(sound) }
		}
	}

	@JvmField
	val SOUND = EventFactory.createArrayBacked(Sound::class.java) { listeners ->
		Sound { sound ->
			listeners.forEach { it.onSound(sound) }
		}
	}

	fun interface AllowSound {
		fun onSound(sound: SoundData): Boolean
	}

	fun interface Sound {
		fun onSound(sound: SoundData)
	}
}