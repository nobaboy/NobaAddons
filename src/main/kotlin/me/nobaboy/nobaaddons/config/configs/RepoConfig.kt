package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import net.fabricmc.loader.api.FabricLoader

class RepoConfig {
	@SerialEntry
	var username: String = "nobaboy"

	@SerialEntry
	var repository: String = "NobaAddons-REPO"

	@SerialEntry
	var branch: String = "main"

	@SerialEntry
	var autoUpdate: Boolean = !FabricLoader.getInstance().isDevelopmentEnvironment
}