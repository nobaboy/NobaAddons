package me.nobaboy.nobaaddons.features.dungeons.data

import dev.celestialfault.histoire.Histoire
import dev.celestialfault.histoire.migrations.Migrations
import me.nobaboy.nobaaddons.NobaAddons

private val migrations = Migrations("version") {
	add {
		it.put("personalBest", it.remove("personal_best") ?: return@add)
	}
}

object SimonSaysTimes : Histoire(NobaAddons.CONFIG_DIR.resolve("simon-says-timer.json").toFile(), migrations = migrations) {
	var personalBest: Double? = null
	var times: MutableList<Double> = mutableListOf()

	var version: Int = migrations.currentVersion
}