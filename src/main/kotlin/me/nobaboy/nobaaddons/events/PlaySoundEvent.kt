package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.events.internal.CancelableEvent
import me.nobaboy.nobaaddons.events.internal.CancelableEventDispatcher
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.util.Identifier

object PlaySoundEvent {
	data class AllowSound(val id: Identifier, val location: NobaVec, val pitch: Float, val volume: Float) : CancelableEvent()
	data class Sound(val id: Identifier, val location: NobaVec, val pitch: Float, val volume: Float)

	@JvmField
	val ALLOW_SOUND = CancelableEventDispatcher<AllowSound>()

	@JvmField
	val SOUND = EventDispatcher<Sound>()
}