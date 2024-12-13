package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import net.minecraft.text.Text

object MiningCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.mining"))

			// region Glacite Mineshaft
			.buildGroup(Text.translatable("nobaaddons.config.mining.glaciteMineshaft")) {
				// region Corpses
				label(Text.translatable("nobaaddons.config.mining.glaciteMineshaft.label.corpses"))

				boolean(
					Text.translatable("nobaaddons.config.mining.glaciteMineshaft.corpseLocator"),
					default = defaults.mining.glaciteMineshaft.corpseLocator,
					property = config.mining.glaciteMineshaft::corpseLocator
				)
				boolean(
					Text.translatable("nobaaddons.config.mining.glaciteMineshaft.autoShareCorpseCoords"),
					default = defaults.mining.glaciteMineshaft.autoShareCorpseCoords,
					property = config.mining.glaciteMineshaft::autoShareCorpseCoords
				)
				// endregion

				// region Miscellaneous
				label(Text.translatable("nobaaddons.config.label.miscellaneous"))

				boolean(
					Text.translatable("nobaaddons.config.mining.glaciteMineshaft.entranceWaypoint"),
					default = defaults.mining.glaciteMineshaft.entranceWaypoint,
					property = config.mining.glaciteMineshaft::entranceWaypoint
				)
				boolean(
					Text.translatable("nobaaddons.config.mining.glaciteMineshaft.ladderWaypoint"),
					default = defaults.mining.glaciteMineshaft.ladderWaypoint,
					property = config.mining.glaciteMineshaft::ladderWaypoint
				)
				// endregion
			}
			// endregion

			.build()
	}
}