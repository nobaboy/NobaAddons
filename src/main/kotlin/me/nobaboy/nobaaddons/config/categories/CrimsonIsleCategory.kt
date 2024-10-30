package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import net.minecraft.text.Text
import java.awt.Color

object CrimsonIsleCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.crimsonIsle"))

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.crimsonIsle.highlightThunderSparks"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.enabled"))
					.binding(defaults.crimsonIsle.highlightThunderSparks.enabled, config.crimsonIsle.highlightThunderSparks::enabled) { config.crimsonIsle.highlightThunderSparks.enabled = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Color>()
					.name(Text.translatable("nobaaddons.config.crimsonIsle.highlightThunderSparks.highlightColor"))
					.binding(defaults.crimsonIsle.highlightThunderSparks.highlightColor, config.crimsonIsle.highlightThunderSparks::highlightColor) { config.crimsonIsle.highlightThunderSparks.highlightColor = it }
					.controller(ColorControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.crimsonIsle.highlightThunderSparks.showText"))
					.binding(defaults.crimsonIsle.highlightThunderSparks.showText, config.crimsonIsle.highlightThunderSparks::showText) { config.crimsonIsle.highlightThunderSparks.showText = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}