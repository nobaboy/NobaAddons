package me.nobaboy.nobaaddons.api.skyblock.events.hoppity

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.isSeason
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.SkyBlockSeason
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItemId

object HoppityAPI {
	const val LOCATOR = "EGGLOCATOR"

	val isActive: Boolean get() = SkyBlockSeason.SPRING.isSeason() && inRelevantIsland && hasLocatorInHand()

	private fun hasLocatorInHand(): Boolean = MCUtils.player?.mainHandStack?.getSkyBlockItemId() == LOCATOR

	private val inRelevantIsland: Boolean
		get() = listOf(
			SkyBlockIsland.PRIVATE_ISLAND,
			SkyBlockIsland.GARDEN,
			SkyBlockIsland.KUUDRAS_HOLLOW,
			SkyBlockIsland.MINESHAFT,
			SkyBlockIsland.DUNGEONS,
			SkyBlockIsland.JERRYS_WORKSHOP,
			SkyBlockIsland.DARK_AUCTION,
			SkyBlockIsland.RIFT,
			SkyBlockIsland.UNKNOWN
		).none { it.inIsland() }
}