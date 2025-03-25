package me.nobaboy.nobaaddons.core

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.util.saveOnExit
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.uuid

object PersistentCache : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("cache.json")) {
	init {
		saveOnExit()
	}

	var lastProfile by Property.ofNullable("lastProfile", Serializer.uuid)
	var repoCommit by Property.ofNullable<String>("repoCommit")
	var devMode by Property.of("devMode", false)
}