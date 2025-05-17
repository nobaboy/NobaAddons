package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object

class GeneralConfig {
	@Object val shortCommands = ShortCommands()

	var wikiCommandAutoOpen = false
	var allowKeybindsOutsideSkyBlock = false
	var updateNotifier = true
	var compactModMessagePrefix = false

	class ShortCommands {
		var registerInstanceCommands = false
		var registerCalculateCommands = false
	}
}