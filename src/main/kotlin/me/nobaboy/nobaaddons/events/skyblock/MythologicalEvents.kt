package me.nobaboy.nobaaddons.events.skyblock

import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import me.nobaboy.nobaaddons.features.events.mythological.BurrowType
import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.entity.Entity

object MythologicalEvents {
	@JvmField
	val BURROW_GUESS = EventDispatcher<BurrowGuess>()

	@JvmField
	val BURROW_FIND = EventDispatcher<BurrowFind>()

	@JvmField
	val BURROW_DIG = EventDispatcher<BurrowDig>()

	@JvmField
	val INQUISITOR_SPAWN = EventDispatcher<InquisitorSpawn>()

	data class BurrowGuess(val location: NobaVec)
	data class BurrowFind(val location: NobaVec, val type: BurrowType)
	data class BurrowDig(val location: NobaVec)
	data class InquisitorSpawn(val entity: Entity)
}