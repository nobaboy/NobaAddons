package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr

object BurrowWarpLocations {
	private val config get() = NobaConfigManager.config.events.mythological

	fun getNearestWarp(location: NobaVec): NearestWarp? {
		val warpPoint = WarpPoint.entries.filter { it.unlocked && !it.ignored() }
			.minByOrNull { it.distance(location) } ?: return null

		val playerToLocation = location.distanceToPlayer()
		val warpPointToLocation = location.distance(warpPoint.location)
		if(playerToLocation - warpPointToLocation < 40) return null

		return NearestWarp(warpPoint)
	}

	fun unlockAll() {
		ChatUtils.addMessage(tr("nobaaddons.command.mythological.unlockedAllWaypoints", "Unlocked all burrow warp locations."))
		WarpPoint.entries.forEach { it.unlocked = true }
	}

	enum class WarpPoint(
		val location: NobaVec,
		private val extraBlocks: Int = 0,
		val ignored: () -> Boolean = { false },
		var unlocked: Boolean = true,
		warpName: String? = null
	) {
		HUB(NobaVec(-2.5, 70.0, -69.5)),
		CASTLE(NobaVec(-250, 130, 45), 10),
		CRYPT(NobaVec(-161.5, 61.0, -99.5), 20, { config.ignoreCrypt }),
		DARK_AUCTION(NobaVec(91.5, 75.0, 173.5), 5, warpName = "da"),
		MUSEUM(NobaVec(-75.5, 76.0, 80.5)),
		WIZARD(NobaVec(42.5, 122.0, 69.0), 10, { config.ignoreWizard }),
		STONKS(NobaVec(-52.5, 71.0, -52.5), 5, { config.ignoreStonks });

		val warpName = warpName ?: name.lowercase()
		val displayName = name.replace("_", " ").title()

		fun distance(other: NobaVec): Double = other.distance(location) + extraBlocks
	}
}