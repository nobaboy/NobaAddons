package me.nobaboy.nobaaddons.events.impl.skyblock

import me.nobaboy.nobaaddons.core.events.MythologicalDrops
import me.nobaboy.nobaaddons.core.events.MythologicalMobs
import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.features.events.mythological.BurrowType
import me.nobaboy.nobaaddons.utils.NobaVec

object MythologicalEvents {
	val BURROW_FIND = EventDispatcher<BurrowFind>()

	val BURROW_DIG = EventDispatcher<BurrowDig>()

	val MOB_DIG = EventDispatcher<MobDig>()

	val TREASURE_DIG = EventDispatcher<TreasureDig>()

	data class BurrowFind(val location: NobaVec, val type: BurrowType) : Event
	data class BurrowDig(val location: NobaVec) : Event
	data class MobDig(val mob: MythologicalMobs) : Event
	data class TreasureDig(val drop: MythologicalDrops, val amount: Int = 1) : Event
}