package me.nobaboy.nobaaddons.features

import com.google.gson.JsonObject
import kotlinx.serialization.serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.features.FeatureManager.reevaulate
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.FileUtils.readGson
import java.io.IOException
import kotlin.io.path.bufferedReader

private val FEATURE_CONFIG = NobaAddons.CONFIG_DIR.resolve("features.json")

/**
 * ```json
 * {
 *   "feature": {
 *     "enabled": false,
 *     "config": {
 *       // ...
 *     }
 *   }
 * }
 * ```
 */
private var CONFIG = emptyMap<String, JsonObject>()
private val KILLSWITCHES by Repo.create("killswitch.json", serializer<Map<String, FeatureKillSwitch>>()).onReload { reevaulate() }

object FeatureManager {
	val features by ::FEATURES
	private val enabledFeatures = mutableListOf<String>()

	val categories: Map<FeatureCategory, List<Feature>>
		get() = FeatureCategory.entries.associate { it to features.filter { f -> f.category == it } }

	fun getKillSwitch(feature: String) = KILLSWITCHES?.get(feature)
	fun hasKillSwitch(feature: String): Boolean = getKillSwitch(feature)?.isApplicable == true

	private fun loadConfig() {
		try {
			val json = FEATURE_CONFIG.readGson(JsonObject::class.java)
			CONFIG = buildMap {
				json.entrySet().forEach { (feature, config) ->
					val config = config as? JsonObject ?: return@forEach
					//
				}
			}
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to read feature configuration from disk", ex)
			return
		}
	}

	fun init() {
		loadConfig()
		for(feature in FEATURES) {
			val config = CONFIG[feature.id] ?: continue
			//
		}
	}

	internal fun reevaulate() {
		TODO()
	}

	fun isEnabled(feature: Feature) = feature.enabled && !hasKillSwitch(feature.id)
}