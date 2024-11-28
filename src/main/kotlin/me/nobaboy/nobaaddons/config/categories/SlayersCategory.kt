package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.StringControllerBuilder
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import net.minecraft.text.Text
import java.awt.Color

object SlayersCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.slayers"))

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.slayers.miniBossAlert"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.enabled"))
					.binding(defaults.slayers.miniBossAlert.enabled, config.slayers.miniBossAlert::enabled) { config.slayers.miniBossAlert.enabled = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<String>()
					.name(Text.translatable("nobaaddons.config.slayers.miniBossAlert.alertText"))
					.binding(defaults.slayers.miniBossAlert.alertText, config.slayers.miniBossAlert::alertText) { config.slayers.miniBossAlert.alertText = it }
					.controller(StringControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Color>()
					.name(Text.translatable("nobaaddons.config.slayers.miniBossAlert.alertColor"))
					.binding(defaults.slayers.miniBossAlert.alertColor, config.slayers.miniBossAlert::alertColor) { config.slayers.miniBossAlert.alertColor = it }
					.controller(ColorControllerBuilder::create)
					.build())

				.collapsed(true)
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.slayers.highlightMiniBosses"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.enabled"))
					.binding(defaults.slayers.highlightMiniBosses.enabled, config.slayers.highlightMiniBosses::enabled) { config.slayers.highlightMiniBosses.enabled = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Color>()
					.name(Text.translatable("nobaaddons.config.slayers.highlightMiniBosses.highlightColor"))
					.binding(defaults.slayers.highlightMiniBosses.highlightColor, config.slayers.highlightMiniBosses::highlightColor) { config.slayers.highlightMiniBosses.highlightColor = it }
					.controller(ColorControllerBuilder::create)
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}