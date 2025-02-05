package me.nobaboy.nobaaddons.config.core

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import me.nobaboy.nobaaddons.config.option.AbstractConfigOptionHolder
import me.nobaboy.nobaaddons.config.utils.safeLoad
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.FileUtils.readJson
import me.nobaboy.nobaaddons.utils.FileUtils.writeJson
import java.io.File

private val JSON = Json {
	prettyPrint = true
}

abstract class AbstractConfigOptionLoader<T : AbstractConfigOptionHolder>(private val file: File) {
	protected abstract val configs: Array<T>

	fun load() {
		if(!file.exists()) {
			save()
			return
		}
		val obj = safeLoad({ file.toPath() }) { file.readJson<JsonObject>(JSON) } ?: return
		configs.forEach {
			val featureConf = obj[it.id] as? JsonObject ?: return@forEach
			try {
				it.load(JSON, featureConf)
			} catch(ex: Exception) {
				ErrorManager.logError(
					"Failed to load a config", ex,
					"In class" to it::class,
					"Config" to featureConf,
				)
			}
		}
	}

	fun save() {
		try {
			val obj = buildJsonObject {
				configs.forEach { put(it.id, it.dump(JSON)) }
			}
			file.writeJson(obj, JSON)
		} catch(ex: Exception) {
			ErrorManager.logError("Failed to save a config", ex)
		}
	}
}