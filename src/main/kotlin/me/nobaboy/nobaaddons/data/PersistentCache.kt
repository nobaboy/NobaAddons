package me.nobaboy.nobaaddons.data

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigUtils.safeLoad
import me.nobaboy.nobaaddons.config.NobaConfigUtils.saveOnExit
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.enumMap
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
import java.util.EnumMap

object PersistentCache : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("cache.json")) {
	var pet by Property.ofNullable("pet", Serializer.expose<PetData>())
	val trophyFish by Property.of(
		key = "trophyFish",
		default = mutableMapOf(),
		serializer = Serializer.map<EnumMap<TrophyFishRarity, Int>>(Serializer.enumMap<TrophyFishRarity, Int>())
	)
	var repoCommit by Property.ofNullable<String>("repoCommit")

	fun init() {
		safeLoad()
		saveOnExit()
		Scheduler.scheduleAsync(15 * 20, repeat = true) {
			if(dirty) {
				NobaAddons.LOGGER.info("Saving cached values")
				save()
			}
		}
	}
}