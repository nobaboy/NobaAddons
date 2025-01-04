package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import net.fabricmc.loader.api.FabricLoader

class RepoConfig : ObjectProperty<RepoConfig>("repo") {
	var username by Property.of<String>("username", "nobaboy")
	var repository by Property.of<String>("repository", "NobaAddons-REPO")
	var branch by Property.of<String>("branch", "main")
	var autoUpdate by Property.of<Boolean>("autoUpdate", !FabricLoader.getInstance().isDevelopmentEnvironment)
}