package me.nobaboy.nobaaddons.core.slayer

import net.minecraft.entity.Entity
import net.minecraft.entity.mob.BlazeEntity
import net.minecraft.entity.mob.EndermanEntity
import net.minecraft.entity.mob.SpiderEntity
import net.minecraft.entity.mob.ZombieEntity
import net.minecraft.entity.passive.WolfEntity
import net.minecraft.entity.player.PlayerEntity
import kotlin.reflect.KClass

enum class SlayerBoss(
	val displayName: String,
	val entity: KClass<out Entity>,
	val miniBossType: SlayerMiniBoss?,
	vararg slayerZones: String
) {
	REVENANT(
		"Revenant Horror",
		ZombieEntity::class,
		SlayerMiniBoss.REVENANT,
		"Graveyard",
		"Coal Mine"
	),
	TARANTULA(
		"Tarantula Broodfather",
		SpiderEntity::class,
		SlayerMiniBoss.TARANTULA,
		"Spider Mound",
		"Arachne's Burrow",
		"Arachne's Sanctuary",
		"Burning Desert"
	),
	SVEN(
		"Sven Packmaster",
		WolfEntity::class,
		SlayerMiniBoss.SVEN,
		"Ruins",
		"Howling Cave"
	),
	VOIDGLOOM(
		"Voidgloom Seraph",
		EndermanEntity::class,
		SlayerMiniBoss.VOIDGLOOM,
		"The End",
		"Dragon's Nest",
		"Void Sepulture",
		"Zealot Bruiser Hideout"
	),
	INFERNO(
		"Inferno Demonlord",
		BlazeEntity::class,
		SlayerMiniBoss.INFERNO,
		"Stronghold",
		"The Wasteland",
		"Smoldering Tomb"
	),
	RIFTSTALKER(
		"Bloodfiend",
		PlayerEntity::class,
		null,
		"Stillgore Château",
		"Oubliette"
	);

	val zones = slayerZones.toSet()

	companion object {
		fun getByName(name: String) = entries.firstOrNull { name.contains(it.displayName) }
		fun getByEntity(entity: Entity) = entries.firstOrNull { entity::class.java == it.entity }
		fun getByZone(zone: String) = entries.firstOrNull { zone in it.zones }
	}
}