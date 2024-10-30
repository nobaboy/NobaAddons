package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.utils.render.HighlightMode
import net.minecraft.text.Text
import java.awt.Color

object DungeonsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.dungeons"))

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.dungeons.highlightStarredMobs"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.enabled"))
					.binding(defaults.dungeons.highlightStarredMobs.enabled, config.dungeons.highlightStarredMobs::enabled) { config.dungeons.highlightStarredMobs.enabled = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Color>()
					.name(Text.translatable("nobaaddons.config.dungeons.highlightStarredMobs.highlightColor"))
					.binding(defaults.dungeons.highlightStarredMobs.highlightColor, config.dungeons.highlightStarredMobs::highlightColor) { config.dungeons.highlightStarredMobs.highlightColor = it }
					.controller(ColorControllerBuilder::create)
					.build())

				.option(Option.createBuilder<HighlightMode>()
					.name(Text.translatable("nobaaddons.config.dungeons.highlightStarredMobs.highlightMode"))
					.binding(defaults.dungeons.highlightStarredMobs.highlightMode, config.dungeons.highlightStarredMobs::highlightMode) { config.dungeons.highlightStarredMobs.highlightMode = it }
					.controller(NobaConfigUtils::createCyclingController)
					.build())

				.collapsed(true)
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.dungeons.simonSaysTimer"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.enabled"))
					.binding(defaults.dungeons.simonSaysTimer.enabled, config.dungeons.simonSaysTimer::enabled) { config.dungeons.simonSaysTimer.enabled = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.dungeons.simonSaysTimer.timeInPartyChat"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.dungeons.simonSaysTimer.timeInPartyChat.tooltip")))
					.binding(defaults.dungeons.simonSaysTimer.timeInPartyChat, config.dungeons.simonSaysTimer::timeInPartyChat) { config.dungeons.simonSaysTimer.timeInPartyChat = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}