package me.nobaboy.nobaaddons.features

import com.google.gson.JsonObject
import kotlinx.serialization.serializer
import me.nobaboy.nobaaddons.repo.Repo

object FeatureManager {
	val FEATURES = mutableListOf<Feature>()

	val CONFIG = mutableMapOf<String, JsonObject>()
	val KILLSWITCHES by Repo.create("killswitch.json", serializer<Map<String, FeatureKillSwitch>>()).onReload {
		reevaulate()
	}

	fun getKillswitch(feature: String) = KILLSWITCHES?.get(feature)

	private fun reevaulate() {
		//
	}
}