package me.nobaboy.nobaaddons.core.profile

import dev.celestialfault.histoire.migrations.Migrations
import dev.celestialfault.histoire.migrations.getMap
import kotlinx.datetime.Instant
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.longOrNull
import me.nobaboy.nobaaddons.config.util.DiscardValue
import me.nobaboy.nobaaddons.config.util.modify
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.data.PetData
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.features.rift.RiftTimerData
import java.util.UUID

private val migrations = Migrations("version") {
	add {
		val timers = it.getMap("riftTimers")
		val unixToIso: (Any) -> Any = {
			(it as? JsonPrimitive)?.longOrNull?.let(Instant::fromEpochMilliseconds)?.toString()?.let(::JsonPrimitive) ?: DiscardValue
		}
		timers.modify("nextFreeInfusion", unixToIso)
		timers.modify("nextSplitSteal", unixToIso)
	}
}

class ProfileData private constructor(profile: UUID?) : AbstractPerProfileConfig(profile, "data.json") {
	var pet: PetData? = null
	var riftTimers: RiftTimerData = RiftTimerData()
	var trophyFish: MutableMap<String, MutableMap<TrophyFishRarity, Int>> = mutableMapOf()

	private var version: Int = migrations.currentVersion

	companion object : AbstractPerProfileDataLoader<ProfileData>() {
		override fun create(id: UUID?): ProfileData = ProfileData(id)

		override fun postLoad(id: UUID, data: ProfileData) {
			SkyBlockEvents.PROFILE_DATA_LOADED.dispatch(SkyBlockEvents.ProfileDataLoad(id, data))
		}

		fun saveAll() {
			profiles.values.forEach { it.save() }
		}
	}
}