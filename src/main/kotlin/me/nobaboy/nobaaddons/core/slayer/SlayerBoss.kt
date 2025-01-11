package me.nobaboy.nobaaddons.core.slayer

import me.nobaboy.nobaaddons.core.slayer.SlayerBoss.entries
import net.minecraft.entity.EntityType

enum class SlayerBoss(
	val displayName: String,
	val entityType: EntityType<*>,
	val miniBossType: SlayerMiniBoss?,
	vararg slayerZones: String
) {
	REVENANT(
		"Revenant Horror",
		EntityType.ZOMBIE,
		SlayerMiniBoss.REVENANT,
		"Graveyard",
		"Coal Mine"
	),
	TARANTULA(
		"Tarantula Broodfather",
		EntityType.SPIDER,
		SlayerMiniBoss.TARANTULA,
		"Spider Mound",
		"Arachne's Burrow",
		"Arachne's Sanctuary",
		"Burning Desert"
	),
	SVEN(
		"Sven Packmaster",
		EntityType.WOLF,
		SlayerMiniBoss.SVEN,
		"Ruins",
		"Howling Cave"
	),
	VOIDGLOOM(
		"Voidgloom Seraph",
		EntityType.ENDERMAN,
		SlayerMiniBoss.VOIDGLOOM,
		"The End",
		"Dragon's Nest",
		"Void Sepulture",
		"Zealot Bruiser Hideout"
	),
	INFERNO(
		"Inferno Demonlord",
		EntityType.BLAZE,
		SlayerMiniBoss.INFERNO,
		"Stronghold",
		"The Wasteland",
		"Smoldering Tomb"
	),
	RIFTSTALKER(
		"Bloodfiend",
		EntityType.PLAYER,
		null,
		"Stillgore Château",
		"Oubliette"
	);

	val zones = slayerZones.toSet()

	companion object {
		fun getByName(name: String) = entries.firstOrNull { name.contains(it.displayName) }
		fun getByZone(zone: String) = entries.firstOrNull { zone in it.zones }
	}
}