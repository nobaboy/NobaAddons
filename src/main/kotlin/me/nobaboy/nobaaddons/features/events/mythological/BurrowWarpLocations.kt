package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr

object BurrowWarpLocations {
	private val config get() = NobaConfig.events.mythological
	private val lockedWarps = mutableSetOf<WarpPoint>()

	fun getNearestWarp(location: NobaVec): WarpPoint? {
		val warpPoint = WarpPoint.entries.filter { it !in lockedWarps && !it.ignored() }
			.minByOrNull { it.distance(location) } ?: return null

		val playerToLocation = location.distanceToPlayer()
		val warpPointToLocation = location.distance(warpPoint.location)
		if(playerToLocation - warpPointToLocation < 40) return null

		return warpPoint
	}

	fun lock(warpPoint: WarpPoint) {
		lockedWarps.add(warpPoint)
	}

	fun unlockAll() {
		ChatUtils.addMessage(tr("nobaaddons.command.mythological.unlockedAllWaypoints", "Unlocked all burrow warp locations."))
		lockedWarps.clear()
	}

	enum class WarpPoint(
		val displayName: String,
		val warpName: String,
		val location: NobaVec,
		private val extraBlocks: Int = 0,
		val ignored: () -> Boolean = { false }
	) {
		HUB("Hub", "hub", NobaVec(-2.5, 70.0, -69.5)),
		CASTLE("Castle", "castle", NobaVec(-250.0, 130.0, 45.0), 10),
		CRYPT("Crypt", "crypt", NobaVec(-161.5, 61.0, -99.5), 20, { config.ignoreCrypt }),
		DARK_AUCTION("Dark Auction", "da", NobaVec(91.5, 75.0, 173.5), 5),
		MUSEUM("Museum", "museum", NobaVec(-75.5, 76.0, 80.5)),
		WIZARD("Wizard", "wizard", NobaVec(42.5, 122.0, 69.0), 10, { config.ignoreWizard }),
		STONKS("Stonks", "stonks", NobaVec(-52.5, 71.0, -52.5), 5, { config.ignoreStonks }),
		;

		fun distance(other: NobaVec): Double = other.distance(location) + extraBlocks
	}
}