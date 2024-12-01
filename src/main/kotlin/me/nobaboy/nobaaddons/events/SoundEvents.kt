package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.CancelableEvent
import me.nobaboy.nobaaddons.events.internal.CancelableEventDispatcher
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.sound.SoundCategory
import net.minecraft.util.Identifier

object SoundEvents {
	@JvmField
	val ALLOW_SOUND = CancelableEventDispatcher<AllowSound>()

	@JvmField
	val SOUND = EventDispatcher<Sound>()

	data class AllowSound(val id: Identifier, val location: NobaVec, val pitch: Float, val volume: Float) : CancelableEvent()
	data class Sound(val id: Identifier, val category: SoundCategory, val location: NobaVec, val pitch: Float, val volume: Float)
}