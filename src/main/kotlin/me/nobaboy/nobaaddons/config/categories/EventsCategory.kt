package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import net.minecraft.text.Text

object EventsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.events"))

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.events.mythologicalRitual"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythologicalRitual.burrowGuess"))
					.binding(defaults.events.mythological.burrowGuess, config.events.mythological::burrowGuess) { config.events.mythological.burrowGuess = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythologicalRitual.findNearbyBurrows"))
					.binding(defaults.events.mythological.findNearbyBurrows, config.events.mythological::findNearbyBurrows) { config.events.mythological.findNearbyBurrows = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythologicalRitual.announceRareDrops"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.events.mythologicalRitual.announceRareDrops.tooltip")))
					.binding(defaults.events.mythological.announceRareDrops, config.events.mythological::announceRareDrops) { config.events.mythological.announceRareDrops = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.events.mythologicalRitual.label.warpLocations"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythologicalRitual.ignoreCrypt"))
					.binding(defaults.events.mythological.ignoreCrypt, config.events.mythological::ignoreCrypt) { config.events.mythological.ignoreCrypt = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythologicalRitual.ignoreWizard"))
					.binding(defaults.events.mythological.ignoreWizard, config.events.mythological::ignoreWizard) { config.events.mythological.ignoreWizard = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythologicalRitual.ignoreStonks"))
					.binding(defaults.events.mythological.ignoreStonks, config.events.mythological::ignoreStonks) { config.events.mythological.ignoreStonks = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}