package me.nobaboy.nobaaddons.features.dungeons.data

import dev.celestialfault.histoire.Histoire
import dev.celestialfault.histoire.migrations.Migrations
import me.nobaboy.nobaaddons.NobaAddons

private val migrations = Migrations("configVersion") {
	add { it["personalBest"] = it.remove("personal_best") ?: return@add }
}

object SimonSaysTimes : Histoire(NobaAddons.CONFIG_DIR.resolve("simon-says-timer.json").toFile(), migrations = migrations) {
	var personalBest: Double? = null
	var times: MutableList<Double> = mutableListOf()

	// this cannot be private in any capacity (not even a `private set`), as reflection totally shits the bed
	// and behaves incredibly inconsistently when encountering a private var in an object class
	@Suppress("unused")
	var configVersion: Int = migrations.currentVersion
}