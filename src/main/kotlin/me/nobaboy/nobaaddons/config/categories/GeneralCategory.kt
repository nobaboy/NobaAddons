package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.config.util.worldSwitchRequired
import me.nobaboy.nobaaddons.utils.mc.TextUtils.aqua
import me.nobaboy.nobaaddons.utils.mc.TextUtils.toText
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

		commands()
	}

	private fun ConfigCategory.Builder.commands() {
		group(tr("nobaaddons.config.general.commands", "Commands")) {
			add({ general.shortCommands::registerCalculateCommands }) {
				name = tr("nobaaddons.config.general.commands.registerCalculateCommands", "Calculate Commands")
				val nobaCalc = "/nobaaddons calculate".toText().aqua()
				descriptionText = tr("nobaaddons.config.general.commands.registerCalculateCommands.tooltip", "Registers shorter aliases for the $nobaCalc commands like /calcpet, /calcskill, etc.")
				booleanController()
				worldSwitchRequired()
			}
			add({ general.shortCommands::registerInstanceCommands }) {
				name = tr("nobaaddons.config.general.commands.registerInstanceCommands", "Instance Commands")
				descriptionText = tr("nobaaddons.config.general.commands.registerInstanceCommands.tooltip", "Registers commands to join instanced islands like Catacombs (e.g. /f7), Master Mode Catacombs (e.g. /m6), and Kuudra (e.g. /t5)")
				booleanController()
				worldSwitchRequired()
			}
		}
	}
}