package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.events.AbstractEvent
import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier

object SoundEvents {
	@Deprecated("Use the companion object instead")
	@JvmField val ALLOW_SOUND = AllowSound.Companion

	@Deprecated("Use the companion object instead")
	@JvmField val SOUND = Sound.Companion

	/**
	 * Event invoked to determine whether a given sound should be allowed to play
	 */
	data class AllowSound(
		val id: Identifier,
		val location: NobaVec,
		val pitch: Float,
		val volume: Float
	) : AbstractEvent(isCancelable = true) {
		companion object : EventDispatcher<AllowSound>()
	}

	/**
	 * Event invoked after a sound is played (or canceled)
	 */
	data class Sound @JvmOverloads constructor(
		val id: Identifier,
		val category: SoundCategory,
		val location: NobaVec,
		val pitch: Float,
		val volume: Float,
		override val canceled: Boolean = false,
	) : Event {
		fun asCanceled() = if(canceled) this else copy(canceled = true)

		companion object : EventDispatcher<Sound>()
	}
}