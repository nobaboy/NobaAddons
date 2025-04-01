package me.nobaboy.nobaaddons.config.configs

import net.fabricmc.loader.api.FabricLoader

class RepoConfig {
	var solvedCaptcha = FabricLoader.getInstance().isDevelopmentEnvironment
	var username = "nobaboy"
	var repository = "NobaAddons-REPO"
	var branch = "main"
	var autoUpdate = !FabricLoader.getInstance().isDevelopmentEnvironment
}