package me.nobaboy.nobaaddons.core.profile

import dev.celestialfault.histoire.Histoire
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.util.saveOnExit
import java.util.UUID

private val PROFILES_DIR = NobaAddons.CONFIG_DIR.resolve("profiles")

sealed class AbstractPerProfileConfig(val profile: UUID?, file: String) : Histoire(
	PROFILES_DIR.resolve(profile?.toString() ?: "unknown").resolve(file).toFile()
) {
	init {
		saveOnExit()
	}

	override fun load() {
		if(profile == null) return
		super.load()
	}

	override fun save() {
		if(profile == null) return
		super.save()
	}
}