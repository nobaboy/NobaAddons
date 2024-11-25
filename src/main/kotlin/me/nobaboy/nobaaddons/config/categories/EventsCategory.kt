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
				.name(Text.translatable("nobaaddons.config.events.mythological"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.burrowGuess"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.events.mythological.burrowGuess.tooltip")))
					.binding(defaults.events.mythological.burrowGuess, config.events.mythological::burrowGuess) { config.events.mythological.burrowGuess = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.findNearbyBurrows"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.events.mythological.findNearbyBurrows.tooltip")))
					.binding(defaults.events.mythological.findNearbyBurrows, config.events.mythological::findNearbyBurrows) { config.events.mythological.findNearbyBurrows = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.removeGuessOnBurrowFind"))
					.binding(defaults.events.mythological.removeGuessOnBurrowFind, config.events.mythological::removeGuessOnBurrowFind) { config.events.mythological.removeGuessOnBurrowFind = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.findNearestWarp"))
					.binding(defaults.events.mythological.findNearestWarp, config.events.mythological::findNearestWarp) { config.events.mythological.findNearestWarp = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.events.mythological.label.inquisitorSharing"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.alertInquisitor"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.events.mythological.alertInquisitor.tooltip")))
					.binding(defaults.events.mythological.alertInquisitor, config.events.mythological::alertInquisitor) { config.events.mythological.alertInquisitor = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.alertOnlyInParty"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.events.mythological.alertOnlyInParty.tooltip")))
					.binding(defaults.events.mythological.alertOnlyInParty, config.events.mythological::alertOnlyInParty) { config.events.mythological.alertOnlyInParty = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.zeldaSecretSoundOnInquisitor"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.events.mythological.zeldaSecretSoundOnInquisitor.tooltip")))
					.binding(defaults.events.mythological.zeldaSecretSoundOnInquisitor, config.events.mythological::zeldaSecretSoundOnInquisitor) { config.events.mythological.zeldaSecretSoundOnInquisitor = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.showInquisitorDespawnTime"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.events.mythological.showInquisitorDespawnTime.tooltip")))
					.binding(defaults.events.mythological.showInquisitorDespawnTime, config.events.mythological::showInquisitorDespawnTime) { config.events.mythological.showInquisitorDespawnTime = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.inquisitorFocusMode"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.events.mythological.inquisitorFocusMode.tooltip")))
					.binding(defaults.events.mythological.inquisitorFocusMode, config.events.mythological::inquisitorFocusMode) { config.events.mythological.inquisitorFocusMode = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.label.miscellaneous"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.announceRareDrops"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.events.mythological.announceRareDrops.tooltip")))
					.binding(defaults.events.mythological.announceRareDrops, config.events.mythological::announceRareDrops) { config.events.mythological.announceRareDrops = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.events.mythological.label.warpLocations"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.ignoreCrypt"))
					.binding(defaults.events.mythological.ignoreCrypt, config.events.mythological::ignoreCrypt) { config.events.mythological.ignoreCrypt = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.ignoreWizard"))
					.binding(defaults.events.mythological.ignoreWizard, config.events.mythological::ignoreWizard) { config.events.mythological.ignoreWizard = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.events.mythological.ignoreStonks"))
					.binding(defaults.events.mythological.ignoreStonks, config.events.mythological::ignoreStonks) { config.events.mythological.ignoreStonks = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}