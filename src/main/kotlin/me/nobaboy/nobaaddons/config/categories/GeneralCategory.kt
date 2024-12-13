package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.screens.NobaMainScreen
import me.nobaboy.nobaaddons.utils.MCUtils
import net.minecraft.text.Text

object GeneralCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.general"))

			.option(ButtonOption.createBuilder()
				.name(Text.translatable("nobaaddons.screen.main"))
				.text(Text.translatable("nobaaddons.screen.button.open"))
				.action { screen, option -> MCUtils.client.setScreen(NobaMainScreen()) }
				.build())

			.boolean(
				Text.translatable("nobaaddons.config.general.allowKeybindsOutsideSkyBlock"),
				Text.translatable("nobaaddons.config.general.allowKeybindsOutsideSkyBlock.tooltip"),
				default = defaults.general.allowKeybindsOutsideSkyBlock,
				property = config.general::allowKeybindsOutsideSkyBlock
			)

			.boolean(
				Text.translatable("nobaaddons.config.general.wikiCommandAutoOpen"),
				Text.translatable("nobaaddons.config.general.wikiCommandAutoOpen.tooltip"),
				default = defaults.general.wikiCommandAutoOpen,
				property = config.general::wikiCommandAutoOpen
			)

			.build()
	}
}