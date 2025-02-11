package me.nobaboy.nobaaddons.core.profile

import dev.celestialfault.celestialconfig.AbstractConfig
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.utils.saveOnExit
import java.util.UUID

private val PROFILES_DIR = NobaAddons.CONFIG_DIR.resolve("profiles")

sealed class AbstractPerProfileConfig(val profile: UUID?, file: String) : AbstractConfig(
	PROFILES_DIR.resolve(profile?.toString() ?: "unknown").resolve(file),
	createIfMissing = profile != null,
) {
	init {
		saveOnExit(onlyIfDirty = profile == null)
	}
}