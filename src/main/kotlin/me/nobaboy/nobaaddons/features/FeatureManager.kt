package me.nobaboy.nobaaddons.features

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.YetAnotherConfigLib
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.option.AbstractConfigOptionLoader
import me.nobaboy.nobaaddons.utils.CommonText

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

		save { save() }
	}.build()
}