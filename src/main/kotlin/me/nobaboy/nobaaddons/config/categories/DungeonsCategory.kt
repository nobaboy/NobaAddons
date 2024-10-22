package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import net.minecraft.text.Text

object DungeonsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.dungeons"))
			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.dungeons.simonSaysTimer"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.dungeons.simonSaysTimer.enabled"))
					.binding(defaults.dungeons.simonSaysTimer.enabled, config.dungeons.simonSaysTimer::enabled) { config.dungeons.simonSaysTimer.enabled = it}
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.dungeons.simonSaysTimer.timeInPartyChat"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.dungeons.simonSaysTimer.timeInPartyChat.tooltip")))
					.binding(defaults.dungeons.simonSaysTimer.timeInPartyChat, config.dungeons.simonSaysTimer::timeInPartyChat) { config.dungeons.simonSaysTimer.timeInPartyChat = it}
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}