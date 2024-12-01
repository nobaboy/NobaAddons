package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import net.minecraft.text.Text

object QOLCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.qol"))

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.qol.soundFilters"))
				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.label.itemAbilities"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.qol.soundFilters.muteWitherSkullAbilities"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.qol.soundFilters.muteWitherSkullAbilities.tooltip")))
					.binding(defaults.qol.soundFilters.muteWitherSkullAbilities, config.qol.soundFilters::muteWitherSkullAbilities) { config.qol.soundFilters.muteWitherSkullAbilities = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.qol.label.dwarvenMines"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.qol.soundFilters.muteGoneWithTheWind"))
					.binding(defaults.qol.soundFilters.muteGoneWithTheWind, config.qol.soundFilters::muteGoneWithTheWind) { config.qol.soundFilters.muteGoneWithTheWind = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.label.rift"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.qol.soundFilters.muteKillerSpring"))
					.binding(defaults.qol.soundFilters.muteKillerSpring, config.qol.soundFilters::muteKillerSpring) { config.qol.soundFilters.muteKillerSpring = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.qol.garden"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.qol.garden.reduceMouseSensitivity"))
					.binding(defaults.qol.garden.reduceMouseSensitivity, config.qol.garden::reduceMouseSensitivity) { config.qol.garden.reduceMouseSensitivity = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Int>()
					.name(Text.translatable("nobaaddons.config.qol.garden.reductionMultiplier"))
					.binding(defaults.qol.garden.reductionMultiplier, config.qol.garden::reductionMultiplier) { config.qol.garden.reductionMultiplier = it }
					.controller { IntegerSliderControllerBuilder.create(it).step(1).range(2, 10) }
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.qol.garden.isDaedalusFarmingTool"))
					.binding(defaults.qol.garden.isDaedalusFarmingTool, config.qol.garden::isDaedalusFarmingTool) { config.qol.garden.isDaedalusFarmingTool = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}