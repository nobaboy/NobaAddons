package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.config.util.builders.OptionBuilder.Companion.descriptionText
import me.nobaboy.nobaaddons.utils.tr

object GeneralCategory {
	fun create() = category(tr("nobaaddons.config.general", "General")) {
		add({ general::allowKeybindsOutsideSkyBlock }) {
			name = tr("nobaaddons.config.general.allowKeybindsOutsideSkyBlock", "Allow Keybinds Outside SkyBlock")
			descriptionText = tr("nobaaddons.config.general.allowKeybindsOutsideSkyBlock.tooltip", "Enables the use of keybinds while not in SkyBlock")
			booleanController()
		}

		add({ general::wikiCommandAutoOpen }) {
			name = tr("nobaaddons.config.general.wikiCommandAutoOpen", "Auto Open /swiki Results")
			descriptionText = tr("nobaaddons.config.general.wikiCommandAutoOpen.tooltip", "Automatically opens your browser with the page from /swiki")
			booleanController()
		}

		add({ general::updateNotifier }) {
			name = tr("nobaaddons.config.general.updateNotifier", "Update Notifier")
			descriptionText = tr("nobaaddons.config.general.updateNotifier.tooltip", "Sends a message in chat when a new update is available")
			booleanController()
		}

		add({ general::compactModMessagePrefix }) {
			name = tr("nobaaddons.config.general.compactModMessagePrefix", "Compact Mod Message Prefix")
			descriptionText = tr("nobaaddons.config.general.compactModMessagePrefix.tooltip", "Chat messages added by the mod will use a shorter prefix when enabled")
			booleanController()
		}
	}
}