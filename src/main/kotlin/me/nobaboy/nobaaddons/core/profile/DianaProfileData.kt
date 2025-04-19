package me.nobaboy.nobaaddons.core.profile

import me.nobaboy.nobaaddons.core.events.MythologicalDrops
import me.nobaboy.nobaaddons.core.events.MythologicalMobs
import java.util.UUID

class DianaProfileData private constructor(profile: UUID?) : AbstractPerProfileConfig(profile, "diana.json") {
	var burrowsDug: Long = 0L
	var chainsFinished: Long = 0L
	var mobsSinceInquisitor: Long = 0L
	val drops: MutableMap<MythologicalDrops, Long> = mutableMapOf()
	val mobs: MutableMap<MythologicalMobs, Long> = mutableMapOf()

	companion object : AbstractPerProfileDataLoader<DianaProfileData>() {
		override fun create(id: UUID?): DianaProfileData = DianaProfileData(id)
	}
}