package me.nobaboy.nobaaddons.utils

import net.minecraft.sound.SoundEvent
import net.minecraft.util.Identifier
import kotlin.math.pow

object SoundUtils {
	private val noteBlockPling = SoundEvent.of(Identifier.ofVanilla("block.note_block.pling"))

	fun playRareDropSound() {
		Scheduler.schedule(0) { playPling(2.0.pow(-9.0 / 12).toFloat(), 0.8f) }
		Scheduler.schedule(4) { playPling(2.0.pow(-2.0 / 12).toFloat(), 0.8f) }
		Scheduler.schedule(8) { playPling(2.0.pow(1.0 / 12).toFloat(), 0.8f) }
		Scheduler.schedule(12) { playPling(2.0.pow(3.0 / 12).toFloat(), 0.8f) }
	}

	private fun playPling(pitch: Float = 1.0f, volume: Float = 1.0f) {
		MCUtils.player?.playSound(noteBlockPling, volume, pitch)
	}
}