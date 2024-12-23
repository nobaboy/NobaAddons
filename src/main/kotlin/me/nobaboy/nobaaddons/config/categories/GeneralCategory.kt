package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import net.minecraft.text.Text

object GeneralCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.general"))

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