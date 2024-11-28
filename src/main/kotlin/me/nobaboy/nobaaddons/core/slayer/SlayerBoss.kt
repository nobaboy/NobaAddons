package me.nobaboy.nobaaddons.core.slayer

import net.minecraft.entity.mob.BlazeEntity
import net.minecraft.entity.mob.EndermanEntity
import net.minecraft.entity.mob.SpiderEntity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity

enum class SlayerBoss(
	val displayName: String,
	val entity: Class<*>,
//	val miniBossType: SlayerMiniBoss? = null,
	vararg slayerZones: String
) {
	REVENANT(
		"Revenant Horror",
		ZombieEntity::class.java,
//		SlayerMiniBoss.REVENANT,
		"Graveyard",
		"Coal Mine"
	),
	TARANTULA(
		"Tarantula Broodfather",
		SpiderEntity::class.java,
//		SlayerMiniBoss.TARANTULA,
		"Spider Mound",
		"Arachne's Burrow",
		"Arachne's Sanctuary",
		"Burning Desert"
	),
	SVEN(
		"Sven Packmaster",
		WolfEntity::class.java,
//		SlayerMiniBoss.SVEN,
		"Ruins",
		"Howling Cave"
	),
	VOIDGLOOM(
		"Voidgloom Seraph",
		EndermanEntity::class.java,
//		SlayerMiniBoss.VOIDGLOOM,
		"The End",
		"Dragon's Nest",
		"Void Sepulture",
		"Zealot Bruiser Hideout"
	),
	INFERNO(
		"Inferno Demonlord",
		BlazeEntity::class.java,
//		SlayerMiniBoss.INFERNO,
		"Stronghold",
		"The Wasteland",
		"Smoldering Tomb"
	),
	RIFTSTALKER(
		"Bloodfiend",
		PlayerEntity::class.java,
//		null,
		"Stillgore Château",
		"Oubliette"
	);

	val zones = slayerZones.toSet()

	companion object {
		fun getByName(name: String) = SlayerBoss.entries.firstOrNull { it.displayName.contains(name) }
		fun getByZone(zone: String) = SlayerBoss.entries.firstOrNull { zone in it.zones }
	}
}