package me.nobaboy.nobaaddons.features

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.YetAnotherConfigLib
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.utils.safeLoad
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.FileUtils.readJson
import me.nobaboy.nobaaddons.utils.FileUtils.writeJson
import kotlin.io.path.exists

object FeatureManager {
	private val FEATURE_CONFIG = NobaAddons.CONFIG_DIR.resolve("features.json")
	private val JSON = Json {
		prettyPrint = true
	}

	val features: List<Feature> by ::FEATURES
	val categories: Map<FeatureCategory, List<Feature>> by lazy {
		FeatureCategory.entries.associateWith {
			features.filter { f -> f.category == it }.sortedBy { it.id }
		}
	}

	fun load() {
		if(!FEATURE_CONFIG.exists()) {
			save()
			return
		}
		val obj = safeLoad({ FEATURE_CONFIG }) { FEATURE_CONFIG.toFile().readJson<JsonObject>(JSON) } ?: return
		features.forEach {
			try {
				it.load(JSON, obj[it.id] as? JsonObject ?: return@forEach)
			} catch(ex: Exception) {
				ErrorManager.logError("Failed to load a feature config", ex, "Feature" to it::class)
			}
		}
	}

	fun save() {
		try {
			FEATURE_CONFIG.toFile().writeJson(buildJsonObject {
				features.forEach { put(it.id, it.dump(JSON)) }
			}, JSON)
		} catch(ex: Exception) {
			ErrorManager.logError("Failed to save feature config", ex)
		}
	}

	fun init() {
		load()
		features.forEach { it.init() }
	}

	fun config(): YetAnotherConfigLib = YetAnotherConfigLib.createBuilder().apply {
		title(CommonText.NOBAADDONS)

		categories(categories.map {
			val features = it.value.mapNotNull { it.config() }
			if(features.isEmpty()) return@map null
			ConfigCategory.createBuilder().apply {
				name(it.key.displayName)
				groups(features)
			}.build()
		}.filterNotNull())

		save { save() }
	}.build()
}