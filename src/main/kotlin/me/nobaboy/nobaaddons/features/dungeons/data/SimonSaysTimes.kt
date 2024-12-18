package me.nobaboy.nobaaddons.features.dungeons.data

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.NobaAddons

object SimonSaysTimes : AbstractConfig(NobaAddons.CONFIG_DIR.resolve("simon-says-timer.json")) {
	var personalBest by Property.ofNullable<Double>("personal_best")
	val times by Property.of("times", Serializer.list<Double>(), mutableListOf())
}