package me.nobaboy.nobaaddons.core.slayer

import net.minecraft.entity.EntityType

enum class SlayerBoss(
	val displayName: String,
	val entityType: EntityType<*>,
	val miniBossNames: List<String>?,
	val zones: List<String>,
) {
	REVENANT(
		"Revenant Horror",
		EntityType.ZOMBIE,
		listOf("Revenant Sycophant", "Revenant Champion", "Deformed Revenant", "Atoned Champion", "Atoned Revenant"),
		listOf("Graveyard", "Coal Mine")
	),
	TARANTULA(
		"Tarantula Broodfather",
		EntityType.SPIDER,
		listOf("Tarantula Vermin", "Tarantula Beast", "Mutant Tarantula"),
		listOf("Spider Mound", "Arachne's Burrow", "Arachne's Sanctuary", "Burning Desert")
	),
	SVEN(
		"Sven Packmaster",
		EntityType.WOLF,
		listOf("Pack Enforcer", "Sven Follower", "Sven Alpha"),
		listOf("Ruins", "Howling Cave")
	),
	VOIDGLOOM(
		"Voidgloom Seraph",
		EntityType.ENDERMAN,
		listOf("Voidling Devotee", "Voidling Radical", "Voidcrazed Maniac"),
		listOf("The End", "Dragon's Nest", "Void Sepulture", "Zealot Bruiser Hideout")
	),
	INFERNO(
		"Inferno Demonlord",
		EntityType.BLAZE,
		listOf("Flare Demon", "Kindleheart Demon", "Burningsoul Demon"),
		listOf("Stronghold", "The Wasteland", "Smoldering Tomb")
	),
	RIFTSTALKER(
		"Bloodfiend",
		EntityType.PLAYER,
		null,
		listOf("Stillgore Château", "Oubliette")
	),
	;

	companion object {
		fun getByName(name: String): SlayerBoss? = entries.firstOrNull { name.contains(it.displayName, ignoreCase = true) }
		fun getByZone(zone: String): SlayerBoss? = entries.firstOrNull { zone in it.zones }
	}
}