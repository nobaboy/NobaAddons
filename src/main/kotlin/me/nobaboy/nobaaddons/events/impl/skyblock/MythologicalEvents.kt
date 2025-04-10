package me.nobaboy.nobaaddons.events.impl.skyblock

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.features.events.mythological.BurrowType
import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.client.network.OtherClientPlayerEntity

object MythologicalEvents {
	@JvmField
	val BURROW_GUESS = EventDispatcher<BurrowGuess>()

	@JvmField
	val BURROW_FIND = EventDispatcher<BurrowFind>()

	@JvmField
	val BURROW_DIG = EventDispatcher<BurrowDig>()

	@JvmField
	val INQUISITOR_SPAWN = EventDispatcher<InquisitorSpawn>()

	data class BurrowGuess(val location: NobaVec) : Event
	data class BurrowFind(val location: NobaVec, val type: BurrowType) : Event
	data class BurrowDig(val location: NobaVec) : Event
	data class InquisitorSpawn(val entity: OtherClientPlayerEntity) : Event
}