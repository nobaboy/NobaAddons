package me.nobaboy.nobaaddons.data

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.Scheduler

object PersistentCache : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("cache.json")) {
	var pet by Property.ofNullable("pet", Serializer.expose<PetData>())

	fun init() {
		load()
		Scheduler.schedule(20, repeat = true) {
			if(dirty) {
				NobaAddons.LOGGER.info("Saving cached values")
				save()
			}
		}
	}
}