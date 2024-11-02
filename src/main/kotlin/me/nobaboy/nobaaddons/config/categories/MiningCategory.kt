package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import net.minecraft.text.Text

object MiningCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.mining"))

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.mining.glaciteMineshaft"))
				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.mining.glaciteMineshaft.label.corpses"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.mining.glaciteMineshaft.corpseLocator"))
					.binding(defaults.mining.glaciteMineshaft.corpseLocator, config.mining.glaciteMineshaft::corpseLocator) { config.mining.glaciteMineshaft.corpseLocator = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.mining.glaciteMineshaft.autoShareCorpseCoords"))
					.binding(defaults.mining.glaciteMineshaft.autoShareCorpseCoords, config.mining.glaciteMineshaft::autoShareCorpseCoords) { config.mining.glaciteMineshaft.autoShareCorpseCoords = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.label.miscellaneous"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.mining.glaciteMineshaft.entranceWaypoint"))
					.binding(defaults.mining.glaciteMineshaft.entranceWaypoint, config.mining.glaciteMineshaft::entranceWaypoint) { config.mining.glaciteMineshaft.entranceWaypoint = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.mining.glaciteMineshaft.ladderWaypoint"))
					.binding(defaults.mining.glaciteMineshaft.ladderWaypoint, config.mining.glaciteMineshaft::ladderWaypoint) { config.mining.glaciteMineshaft.ladderWaypoint = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}