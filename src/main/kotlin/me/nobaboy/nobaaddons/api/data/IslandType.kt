package me.nobaboy.nobaaddons.api.data

enum class IslandType(val apiName: String?) {
	PRIVATE_ISLAND("dynamic"),
	GARDEN("garden"),
	HUB("hub"),

	FARMING_ISLANDS("farming_1"),

	THE_PARK("foraging_1"),

	SPIDERS_DEN("combat_1"),
	THE_END("combat_3"),
	CRIMSON_ISLE("crimson_isle"),
	KUUDRAS_HOLLOW("instanced"),

	GOLD_MINES("mining_1"),
	DEEP_CAVERNS("mining_2"),
	DWARVEN_MINES("mining_3"),
	MINESHAFT("mineshaft"),
	CRYSTAL_HOLLOWS("crystal_hollows"),

	DUNGEON_HUB("dungeon_hub"),
	DUNGEONS("dungeon"),

	JERRYS_WORKSHOP("winter"),
	DARK_AUCTION("dark_action"),
	RIFT("rift"),
	UNKNOWN(null);

	companion object {
		val ISLANDS = IslandType.entries.filter { it.apiName != null }.associateBy { it.apiName!! }
		fun getIslandType(mode: String): IslandType = ISLANDS.getOrDefault(mode, UNKNOWN)
	}
}