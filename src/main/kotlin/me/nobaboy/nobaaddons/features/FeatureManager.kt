package me.nobaboy.nobaaddons.features

import com.google.gson.JsonObject
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.FileUtils.readGson
import me.nobaboy.nobaaddons.utils.FileUtils.writeGson
import java.io.IOException

private val FEATURE_CONFIG = NobaAddons.CONFIG_DIR.resolve("features.json")

object FeatureManager {
	val features by ::FEATURES
	private val enabledFeatures: List<Feature> get() = features.filter(::isEnabled)

	val categories: Map<FeatureCategory, List<Feature>>
		get() = FeatureCategory.entries.associate { it to features.filter { f -> f.category == it } }

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
	private fun loadConfig(): Map<String, JsonObject> {
		return try {
			buildMap {
				val json = FEATURE_CONFIG.toFile().readGson(JsonObject::class.java)
				for((feature, config) in json.entrySet()) {
					put(feature, config as? JsonObject ?: continue)
				}
			}
		} catch(ex: Exception) {
			ErrorManager.logError("Failed to read feature configuration from disk", ex)
			return emptyMap()
		}
	}

	fun saveConfig() {
		try {
			FEATURE_CONFIG.toFile().writeGson(JsonObject().apply {
				for(feature in features) {
					add(feature.id, feature.config.save())
				}
			})
		} catch(ex: IOException) {
			ErrorManager.logError("Failed to save feature configuration", ex)
		}
	}

	fun init() {
		val config = loadConfig()
		for(feature in FEATURES) {
			val config = config[feature.id] ?: continue
			feature.config.load(config)
		}
	}

	internal fun reevaulate(data: Map<String, KillSwitchData>?) {
		// TODO
	}

	fun isEnabled(feature: Feature) = feature.config.enabled == true && !feature.killswitch
}