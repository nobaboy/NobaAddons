package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.utils.tr

object GeneralCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.general", "General")) {
		boolean(
			tr("nobaaddons.config.general.allowKeybindsOutsideSkyBlock", "Allow Keybinds Outside SkyBlock"),
			tr("nobaaddons.config.general.allowKeybindsOutsideSkyBlock.tooltip", "Enables the use of keybinds while not in SkyBlock"),
			default = defaults.general.allowKeybindsOutsideSkyBlock,
			property = config.general::allowKeybindsOutsideSkyBlock
		)

		boolean(
			tr("nobaaddons.config.general.wikiCommandAutoOpen", "Auto Open /swiki Results"),
			tr("nobaaddons.config.general.wikiCommandAutoOpen.tooltip", "Automatically opens your browser with the page from /swiki"),
			default = defaults.general.wikiCommandAutoOpen,
			property = config.general::wikiCommandAutoOpen
		)
	}
}