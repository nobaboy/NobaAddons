package me.nobaboy.nobaaddons.core

import dev.celestialfault.histoire.Histoire
import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.util.saveOnExit
import me.nobaboy.nobaaddons.utils.serializers.UUIDKSerializer
import java.util.UUID

object PersistentCache : Histoire(NobaAddons.CONFIG_DIR.resolve("cache.json")) {
	init {
		saveOnExit()
	}

	@Serializable(UUIDKSerializer::class)
	var lastProfile: UUID? = null

	var repoCommit: String? = null
	var devMode: Boolean = false
}