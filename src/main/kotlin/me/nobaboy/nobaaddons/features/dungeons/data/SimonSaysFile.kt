package me.nobaboy.nobaaddons.features.dungeons.data

import dev.celestialfault.celestialconfig.AbstractConfig
import dev.celestialfault.celestialconfig.Property
import me.nobaboy.nobaaddons.NobaAddons

object SimonSaysFile : AbstractConfig(NobaAddons.modConfigDir.resolve("simon-says-timer.json")) {
	var personalBest by Property.double("personal_best")
	val times by Property.list<Double>("times")
}