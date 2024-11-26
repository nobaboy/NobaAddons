package me.nobaboy.nobaaddons.core

import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class SeaCreature(
	val rarity: MobRarity,
	val spawnMessage: String,
	val island: SkyBlockIsland? = null,
	displayName: String? = null
) {
	// Water
	SQUID(MobRarity.COMMON, "A Squid appeared."),
	SEA_WALKER(MobRarity.COMMON, "You caught a Sea Walker."),
	SEA_GUARDIAN(MobRarity.COMMON, "You stumbled upon a Sea Guardian."),
	SEA_WITCH(MobRarity.UNCOMMON, "It looks like you've disrupted the Sea Witch's brewing session. Watch out, she's furious!"),
	SEA_ARCHER(MobRarity.UNCOMMON, "You reeled in a Sea Archer."),
	RIDER_OF_THE_DEEP(MobRarity.UNCOMMON, "The Rider of the Deep has emerged.", displayName = "Rider of the Deep"),
	CATFISH(MobRarity.RARE, "Huh? A Catfish!"),
	SEA_LEECH(MobRarity.RARE, "Gross! A Sea Leech!"),
	GUARDIAN_DEFENDER(MobRarity.EPIC, "You've discovered a Guardian Defender of the sea."),
	DEEP_SEA_PROTECTOR(MobRarity.EPIC, "You have awoken the Deep Sea Protector, prepare for a battle!"),
	WATER_HYDRA(MobRarity.LEGENDARY, "The Water Hydra has come to test your strength."),
	THE_SEA_EMPEROR(MobRarity.LEGENDARY, "The Sea Emperor arises from the depths."),

	// Water Special
	NIGHT_SQUID(MobRarity.COMMON, "Pitch darkness reveals a Night Squid."),
	CARROT_KING(MobRarity.RARE, "Is this even a fish? It's the Carrot King!"),
	AGARIMOO(MobRarity.RARE, "Your Chumcap Bucket trembles, it's an Agarimoo."),
	OASIS_RABBIT(MobRarity.UNCOMMON, "An Oasis Rabbit appears from the water.", SkyBlockIsland.FARMING_ISLANDS),
	OASIS_SHEEP(MobRarity.UNCOMMON, "An Oasis Sheep appears from the water.", SkyBlockIsland.FARMING_ISLANDS),
	WATER_WORM(MobRarity.RARE, "A Water Worm surfaces!", SkyBlockIsland.CRYSTAL_HOLLOWS),
	POISONED_WATER_WORM(MobRarity.RARE, "A Poisoned Water Worm surfaces!", SkyBlockIsland.CRYSTAL_HOLLOWS),
	ABYSSAL_MINER(MobRarity.LEGENDARY, "An Abyssal Miner breaks out of the water!", SkyBlockIsland.CRYSTAL_HOLLOWS),

	// Lava
	FLAMING_WORM(MobRarity.RARE, "A Flaming Worm surfaces from the depths!", SkyBlockIsland.CRYSTAL_HOLLOWS),
	LAVA_BLAZE(MobRarity.EPIC, "A Lava Blaze has surfaced from the depths!", SkyBlockIsland.CRYSTAL_HOLLOWS),
	LAVA_PIGMAN(MobRarity.EPIC, "A Lava Pigman arose from the depths!", SkyBlockIsland.CRYSTAL_HOLLOWS),
	MAGMA_SLUG(MobRarity.UNCOMMON, "From beneath the lava appears a Magma Slug.", SkyBlockIsland.CRIMSON_ISLE),
	MOOGMA(MobRarity.UNCOMMON, "You hear a faint Moo from the lava... A Moogma appears.", SkyBlockIsland.CRIMSON_ISLE),
	LAVA_LEECH(MobRarity.RARE, "A small but fearsome Lava Leech emerges.", SkyBlockIsland.CRIMSON_ISLE),
	PYROCLASTIC_WORM(MobRarity.RARE, "You feel the heat radiating as a Pyroclastic Worm surfaces.", SkyBlockIsland.CRIMSON_ISLE),
	LAVA_FLAME(MobRarity.RARE, "A Lava Flame flies out from beneath the lava.", SkyBlockIsland.CRIMSON_ISLE),
	LAVA_EAL(MobRarity.RARE, "A Fire Eel slithers out from the depths.", SkyBlockIsland.CRIMSON_ISLE),
	TAURUS(MobRarity.EPIC, "Taurus and his steed emerge.", SkyBlockIsland.CRIMSON_ISLE),
	PLHLEGBLAST(MobRarity.LEGENDARY, "WOAH! A Plhlegblast appeared.", SkyBlockIsland.CRIMSON_ISLE),
	THUNDER(MobRarity.MYTHIC, "You hear a massive rumble as Thunder emerges.", SkyBlockIsland.CRIMSON_ISLE),
	LORD_JAWBUS(MobRarity.MYTHIC, "You have angered a legendary creature... Lord Jawbus has arrived.", SkyBlockIsland.CRIMSON_ISLE),

	// Winter
	FROZEN_STEVE(MobRarity.COMMON, "Frozen Steve fell into the pond long ago, never to resurface...until now!", SkyBlockIsland.JERRYS_WORKSHOP),
	FROSTY(MobRarity.COMMON, "It's a snowman! He looks harmless.", SkyBlockIsland.JERRYS_WORKSHOP),
	GRINCH(MobRarity.UNCOMMON, "The Grinch stole Jerry's Gifts...get them back!", SkyBlockIsland.JERRYS_WORKSHOP),
	NUTCRACKER(MobRarity.RARE, "You found a forgotten Nutcracker laying beneath the ice.", SkyBlockIsland.JERRYS_WORKSHOP),
	YETI(MobRarity.LEGENDARY, "What is this creature!?", SkyBlockIsland.JERRYS_WORKSHOP),
	REINDRAKE(MobRarity.LEGENDARY, "A Reindrake forms from the depths.", SkyBlockIsland.JERRYS_WORKSHOP),

	// Spooky Festival
	SCARECROW(MobRarity.COMMON, "Phew! It's only a Scarecrow."),
	NIGHTMARE(MobRarity.RARE, "You hear trotting from beneath the waves, you caught a Nightmare."),
	WEREWOLF(MobRarity.EPIC, "It must be a full moon, a Werewolf appears."),
	PHANTOM_FISHER(MobRarity.LEGENDARY, "The spirit of a long lost Phantom Fisher has come to haunt you."),
	GRIM_REAPER(MobRarity.LEGENDARY, "This can't be! The manifestation of death himself!"),

	// Fishing Festival
	NURSE_SHARK(MobRarity.UNCOMMON, "A tiny fin emerges from the water, you've caught a Nurse Shark."),
	BLUE_SHARK(MobRarity.RARE, "You spot a fin as blue as the water it came from, it's a Blue Shark."),
	TIGER_SHARK(MobRarity.EPIC, "A striped beast bounds from the depths, the wild Tiger Shark!"),
	GREAT_WHITE_SHARK(MobRarity.LEGENDARY, "Hide no longer, a Great White Shark has tracked your scent and thirsts for your blood!");

	val displayName = displayName ?: name.replace("_", " ").title()

	companion object {
		val creatures = entries.associateBy { it.spawnMessage }
	}
}