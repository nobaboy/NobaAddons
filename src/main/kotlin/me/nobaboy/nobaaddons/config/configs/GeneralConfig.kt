package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property

class GeneralConfig : ObjectProperty<GeneralConfig>("general") {
	var wikiCommandAutoOpen by Property.of<Boolean>("wikiCommandAutoOpen", false)
	var allowKeybindsOutsideSkyBlock by Property.of<Boolean>("allowKeybindsOutsideSkyBlock", false)
	var updateNotifier by Property.of<Boolean>("updateNotifier", false)
}