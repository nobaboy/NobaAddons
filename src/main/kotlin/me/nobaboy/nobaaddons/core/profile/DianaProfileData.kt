package me.nobaboy.nobaaddons.core.profile

import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.core.events.MythologicalDrops
import me.nobaboy.nobaaddons.core.events.MythologicalMobs
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.enumMap
import java.util.EnumMap
import java.util.UUID

class DianaProfileData private constructor(profile: UUID?) : AbstractPerProfileConfig(profile, "diana.json") {
	var burrowsDug: Long by Property.of("burrowsDug", Serializer.long, 0L)
	var chainsFinished: Long by Property.of("chainsFinished", Serializer.long, 0L)
	var mobsSinceInquisitor: Long by Property.of("mobsSinceInquisitor", Serializer.long, 0L)
	val drops: MutableMap<MythologicalDrops, Long> by Property.of("drops", Serializer.enumMap<MythologicalDrops, Long>(), EnumMap(MythologicalDrops::class.java))
	val mobs: MutableMap<MythologicalMobs, Long> by Property.of("mobs", Serializer.enumMap<MythologicalMobs, Long>(), EnumMap(MythologicalMobs::class.java))

	companion object : AbstractPerProfileDataLoader<DianaProfileData>() {
		override fun create(id: UUID?): DianaProfileData = DianaProfileData(id)

		fun saveAll() {
			profiles.values.forEach { it.save() }
		}
	}
}