package me.nobaboy.nobaaddons.features

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.YetAnotherConfigLib
import kotlinx.serialization.json.JsonObject
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.LegacyConfigMigration
import me.nobaboy.nobaaddons.config.LegacyConfigMigration.Companion.applyAll
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.option.AbstractConfigOptionLoader
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.FileUtils.readGson
import kotlin.io.path.exists

private val FEATURE_CONFIG = NobaAddons.CONFIG_DIR.resolve("features.json")

// TODO migrating configs to this new system is going to be a pain in the ass
// one possible way to go about this would be something like
// listOf(
//     "old.config.key" to "feature.config.key",
//     ...
// )

object FeatureManager : AbstractConfigOptionLoader<Feature>(FEATURE_CONFIG.toFile()) {
	val features: Array<Feature> by ::FEATURES
	override val configs: Array<Feature> by ::FEATURES

	val categories: Map<FeatureCategory, List<Feature>> by lazy {
		FeatureCategory.entries.associateWith {
			features.filter { f -> f.category == it }.sortedBy { it.id }
		}
	}

	fun init() {
		load()
		features.forEach { it.init() }
	}

	override fun createNewConfig() {
		if(NobaConfig.CONFIG_PATH.exists()) {
			loadFromJson(
				LegacyConfigMigration.FEATURES.applyAll(
					NobaConfig.CONFIG_PATH.toFile().readGson(com.google.gson.JsonObject::class.java),
					JsonObject(mapOf())
				)
			)
		}
		save()
	}

	fun buildConfig(): YetAnotherConfigLib = YetAnotherConfigLib.createBuilder().apply {
		title(CommonText.NOBAADDONS)

		categories(categories.mapNotNull {
			// TODO remove this as features are migrated over
			if(it.value.isEmpty() && it.key.global == null) {
				return@mapNotNull null
			}

			val category = ConfigCategory.createBuilder().apply {
				name(it.key.displayName)
				it.key.global?.buildConfig(this)
				it.value.forEach { it.buildConfig(this) }
			}

			category.build()
		})

		save {
			features.forEach {
				try {
					it.saveEvent()
				} catch(ex: Throwable) {
					ErrorManager.logError("Feature failed to run save handler", ex, "In class" to it::class)
				}
			}
			FeatureCategory.entries.forEach {
				try {
					it.global?.saveEvent()
				} catch(ex: Throwable) {
					ErrorManager.logError("Core config failed to run save handler", ex, "In class" to it::class)
				}
			}
			save()
		}
	}.build()
}