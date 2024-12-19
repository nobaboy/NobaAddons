package me.nobaboy.nobaaddons.data

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.core.fishing.TrophyFish
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.enumMap
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import java.util.EnumMap

object PersistentCache : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("cache.json")) {
	var pet by Property.ofNullable("pet", Serializer.expose<PetData>())
	val trophyFish by Property.of(
		key = "trophyFish",
		default = EnumMap(TrophyFish::class.java),
		serializer = Serializer.enumMap<TrophyFish, EnumMap<TrophyFishRarity, Int>>(Serializer.enumMap<TrophyFishRarity, Int>())
	)

	fun init() {
		load()
		Scheduler.schedule(20, repeat = true) {
			if(dirty) {
				NobaAddons.LOGGER.info("Saving cached values")
				save()
			}
		}
		ClientLifecycleEvents.CLIENT_STOPPING.register {
			// mutable variables (like trophyFish) can't set dirty to true, so we have to also save on shutdown
			save()
		}
	}
}