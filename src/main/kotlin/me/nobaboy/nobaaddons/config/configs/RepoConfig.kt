package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry

class RepoConfig {
	@SerialEntry
	var uri: String = "https://github.com/nobaboy/NobaAddons-REPO.git"

	@SerialEntry
	var branch: String = "main"
}