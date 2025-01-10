package me.nobaboy.nobaaddons.data

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigUtils.safeLoad
import me.nobaboy.nobaaddons.config.NobaConfigUtils.saveEvery
import me.nobaboy.nobaaddons.config.NobaConfigUtils.saveOnExit
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.enumMap
import java.util.EnumMap

object PersistentCache : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("cache.json")) {
	var pet by Property.ofNullable("pet", Serializer.expose<PetData>())
	val trophyFish by Property.of(
		key = "trophyFish",
		default = mutableMapOf(),
		serializer = Serializer.map<EnumMap<TrophyFishRarity, Int>>(Serializer.enumMap<TrophyFishRarity, Int>())
	)
	var repoCommit by Property.ofNullable<String>("repoCommit")
	var devMode by Property.of("devMode", false)

	fun init() {
		safeLoad()
		saveOnExit()
		saveEvery(ticks = 15 * 20)
	}
}