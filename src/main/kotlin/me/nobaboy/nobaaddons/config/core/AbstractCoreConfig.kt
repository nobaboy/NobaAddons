package me.nobaboy.nobaaddons.config.core

import dev.isxander.yacl3.api.ConfigCategory
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.option.AbstractConfigOptionHolder
import me.nobaboy.nobaaddons.config.utils.safeLoad
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.FileUtils.readJson
import me.nobaboy.nobaaddons.utils.FileUtils.writeJson
import kotlin.io.path.exists

sealed class AbstractCoreConfig(protected val id: String) : AbstractConfigOptionHolder() {
	override fun buildConfig(category: ConfigCategory.Builder) {
		options.values.mapNotNull { it.yaclOption }.forEach(category::option)
	}

	companion object {
		private val FILE = NobaAddons.CONFIG_DIR.resolve("core.json")
		private val CONFIGS = arrayOf<AbstractCoreConfig>(
			CoreAPIConfig,
		)

		private val JSON = Json {
			prettyPrint = true
		}

		fun load() {
			if(!FILE.exists()) {
				save()
				return
			}
			val obj = safeLoad({ FILE }) { FILE.toFile().readJson<JsonObject>(JSON) } ?: return
			CONFIGS.forEach {
				try {
					it.load(JSON, obj[it.id] as? JsonObject ?: return@forEach)
				} catch(ex: Exception) {
					ErrorManager.logError("Failed to load a core config", ex, "Feature" to it::class)
				}
			}
		}

		fun save() {
			try {
				FILE.toFile().writeJson(buildJsonObject {
					CONFIGS.forEach { put(it.id, it.dump(JSON)) }
				}, JSON)
			} catch(ex: Exception) {
				ErrorManager.logError("Failed to save core config", ex)
			}
		}
	}
}