package me.nobaboy.nobaaddons.core

enum class SkyBlockIsland(val apiName: String?) {
	// Main & private islands
	PRIVATE_ISLAND("dynamic"),
	GARDEN("garden"),
	HUB("hub"),

	// Fishing
	BACKWATER_BAYOU("fishing_1"),

	// Farming
	FARMING_ISLANDS("farming_1"),

	// Foraging
	THE_PARK("foraging_1"),

	// Combat
	SPIDERS_DEN("combat_1"),
	THE_END("combat_3"),
	CRIMSON_ISLE("crimson_isle"),
	KUUDRAS_HOLLOW("instanced"),

	// Mining
	GOLD_MINES("mining_1"),
	DEEP_CAVERNS("mining_2"),
	DWARVEN_MINES("mining_3"),
	MINESHAFT("mineshaft"),
	CRYSTAL_HOLLOWS("crystal_hollows"),

	// Dungeons
	DUNGEON_HUB("dungeon_hub"),
	DUNGEONS("dungeon"),

	// Misc
	JERRYS_WORKSHOP("winter"),
	DARK_AUCTION("dark_action"),
	RIFT("rift"),
	UNKNOWN(null),
	;

	companion object {
		val ISLANDS = entries.filter { it.apiName != null }.associateBy { it.apiName!! }

		fun getByName(name: String): SkyBlockIsland = ISLANDS.getOrDefault(name, UNKNOWN)
	}
}