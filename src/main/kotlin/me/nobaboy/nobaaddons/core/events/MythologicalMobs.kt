package me.nobaboy.nobaaddons.core.events

enum class MythologicalMobs(val displayName: String) {
	MINOS_HUNTER("Minos Hunter"),
	SIAMESE_LYNX("Siamese Lynxes"),
	MINOTAUR("Minotaur"),
	GAIA_CONSTRUCT("Gaia Construct"),
	MINOS_CHAMPION("Minos Champion"),
	MINOS_INQUISITOR("Minos Inquisitor");

	companion object {
		fun getByName(name: String): MythologicalMobs? = entries.firstOrNull { it.displayName == name }
	}
}