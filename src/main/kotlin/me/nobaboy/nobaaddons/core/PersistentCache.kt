package me.nobaboy.nobaaddons.core

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigUtils.saveOnExit
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.uuid

object PersistentCache : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("cache.json")) {
	init {
		saveOnExit()
	}

	var lastProfile by Property.Companion.ofNullable("lastProfile", serializer = Serializer.Companion.uuid)
	var repoCommit by Property.Companion.ofNullable<String>("repoCommit")
	var devMode by Property.Companion.of("devMode", false)
}