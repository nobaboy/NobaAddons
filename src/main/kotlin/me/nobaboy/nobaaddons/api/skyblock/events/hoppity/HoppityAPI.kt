package me.nobaboy.nobaaddons.api.skyblock.events.hoppity

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.isSeason
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.SkyBlockSeason
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyBlockId

object HoppityAPI {
	const val LOCATOR = "EGGLOCATOR"

	val isActive: Boolean get() = isSpring && inRelevantIsland && hasLocatorInHotbar

	val isSpring: Boolean get() = SkyBlockSeason.SPRING.isSeason()
	val inRelevantIsland: Boolean get() = unsupportedIslands.none { it.inIsland() }

	val hasLocatorInHand: Boolean get() = MCUtils.player?.mainHandStack?.skyBlockId == LOCATOR
	val hasLocatorInHotbar: Boolean get() = InventoryUtils.getHotbarItems().any { it.skyBlockId == LOCATOR }

	private val unsupportedIslands = listOf(
		SkyBlockIsland.PRIVATE_ISLAND,
		SkyBlockIsland.GARDEN,
		SkyBlockIsland.KUUDRAS_HOLLOW,
		SkyBlockIsland.MINESHAFT,
		SkyBlockIsland.DUNGEONS,
		SkyBlockIsland.JERRYS_WORKSHOP,
		SkyBlockIsland.DARK_AUCTION,
		SkyBlockIsland.RIFT,
		SkyBlockIsland.UNKNOWN
	)
}