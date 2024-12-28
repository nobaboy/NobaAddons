package me.nobaboy.nobaaddons.utils.sound

import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.Scheduler
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvent
import net.minecraft.sound.SoundEvents
import kotlin.math.pow

// TODO: Either drop this or expand on this so complex sequences are allowed (basically multiple sound events)
object SoundUtils {
	private val experienceOrbPickup = SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP
	private val noteBlockPling = SoundEvents.BLOCK_NOTE_BLOCK_PLING.value()
	private val noteBlockFlute = SoundEvents.BLOCK_NOTE_BLOCK_HARP.value()

	val plingSound = SimpleSound(noteBlockPling, pitch = 2.0f)
	val dingSound = SimpleSound(experienceOrbPickup, pitch = 0.5f)

	val zeldaSecretSound = SoundSequence.uniformVolume(
		soundEvent = noteBlockFlute,
		semitones = listOf(1, 0, -3, -9, -10, -2, 2, 6),
		volume = 1.0f,
		delay = 3
	)

	val rareDropSound = SoundSequence.uniformVolume(
		soundEvent = noteBlockPling,
		semitones = listOf(-9, -2, 1, 3),
		volume = 0.8f,
		delay = 4
	)

	fun playSound(soundEvent: SoundEvent, volume: Float = 1f, pitch: Float = 1f, category: SoundCategory = SoundCategory.MASTER) {
		val player = MCUtils.player ?: return
		MCUtils.world?.playSound(player, player.x, player.y, player.z, soundEvent, category, volume, pitch)
	}

	class SoundSequence(
		val soundEvent: SoundEvent,
		val steps: List<SoundStep>,
		val delay: Int
	) : PlayableSound {
		override fun play(category: SoundCategory) {
			steps.forEachIndexed { index, step ->
				val delay = index * delay
				Scheduler.schedule(delay) { playSound(soundEvent, step.volume, step.pitch, category) }
			}
		}

		companion object {
			fun uniformVolume(
				soundEvent: SoundEvent,
				semitones: List<Int>,
				volume: Float,
				delay: Int
			) : SoundSequence {
				val steps = semitones.map { SoundStep(it, volume) }
				return SoundSequence(soundEvent, steps, delay)
			}
		}
	}

	class SimpleSound(
		val soundEvent: SoundEvent,
		val pitch: Float = 1.0f,
		val volume: Float = 1.0f
	) : PlayableSound {
		override fun play(category: SoundCategory) {
			playSound(soundEvent, volume, pitch, category)
		}
	}

	data class SoundStep(val semitone: Int, val volume: Float) {
		val pitch: Float
			get() = 2.0.pow(semitone / 12.0).toFloat()
	}
}