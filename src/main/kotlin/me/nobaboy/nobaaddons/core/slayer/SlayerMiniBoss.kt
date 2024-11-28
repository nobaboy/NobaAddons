package me.nobaboy.nobaaddons.core.slayer

enum class SlayerMiniBoss(vararg names: String) {
	REVENANT(
		"Revenant Sycophant",
		"Revenant Champion",
		"Deformed Revenant",
		"Atoned Champion",
		"Atoned Revenant"
	),
	TARANTULA(
		"Tarantula Vermin",
		"Tarantula Beast",
		"Mutant Tarantula"
	),
	SVEN(
		"Pack Enforcer",
		"Sven Follower",
		"Sven Alpha"
	),
	VOIDLING(
		"Voidling Devotee",
		"Voidling Radical",
		"Voidcrazed Maniac"
	),
	INFERNAL(
		"Flare Demon",
		"Kindleheart Demon",
		"Burningsoul Demon"
	);

	val names = names.toSet()

	companion object {
		fun getByName(name: String): SlayerMiniBoss? = entries.firstOrNull { name in it.names }
	}
}