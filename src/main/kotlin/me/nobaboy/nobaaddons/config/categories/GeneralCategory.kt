package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.ui.NobaMainScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

object GeneralCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.general"))

			.option(ButtonOption.createBuilder()
				.name(Text.translatable("nobaaddons.config.general.main"))
				.text(Text.translatable("nobaaddons.config.general.open"))
				.action { screen, option -> MinecraftClient.getInstance().setScreen(NobaMainScreen()) }
				.build())

			.option(Option.createBuilder<Boolean>()
				.name(Text.translatable("nobaaddons.config.general.allowKeybindsOutsideSkyBlock"))
				.description(OptionDescription.of(Text.translatable("nobaaddons.config.general.allowKeybindsOutsideSkyBlock.tooltip")))
				.binding(defaults.general.allowKeybindsOutsideSkyBlock, config.general::allowKeybindsOutsideSkyBlock) { config.general.allowKeybindsOutsideSkyBlock = it }
				.controller(NobaConfigUtils::createBooleanController)
				.build())

			.option(Option.createBuilder<Boolean>()
				.name(Text.translatable("nobaaddons.config.general.wikiCommandAutoOpen"))
				.description(OptionDescription.of(Text.translatable("nobaaddons.config.general.wikiCommandAutoOpen.tooltip")))
				.binding(defaults.general.wikiCommandAutoOpen, config.general::wikiCommandAutoOpen) { config.general.wikiCommandAutoOpen = it }
				.controller(NobaConfigUtils::createBooleanController)
				.build())

			.build()
	}
}