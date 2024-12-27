package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry

class GeneralConfig {
	@SerialEntry
	var wikiCommandAutoOpen: Boolean = false

	@SerialEntry
	var allowKeybindsOutsideSkyBlock: Boolean = false

	@SerialEntry
	var updateNotifier: Boolean = true
}