package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.utils.*
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr

object MiningCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = buildCategory(tr("nobaaddons.config.mining", "Mining")) {
		// region Worm Alert
		buildGroup(tr("nobaaddons.config.mining.wormAlert", "Worm Alert")) {
			val enabled = boolean(
				CommonText.Config.ENABLED,
				default = defaults.mining.wormAlert.enabled,
				property = defaults.mining.wormAlert::enabled
			)
			color(
				CommonText.Config.ALERT_COLOR,
				default = defaults.mining.wormAlert.alertColor,
				property = defaults.mining.wormAlert::alertColor
			) requires config(enabled)
		}
		// endregion

		// region Glacite Mineshaft
		buildGroup(tr("nobaaddons.config.mining.glaciteMineshaft", "Glacite Mineshaft")) {
			// region Corpses
			label(tr("nobaaddons.config.mining.glaciteMineshaft.label.corpses", "Corpses"))

			val locate = boolean(
				tr("nobaaddons.config.mining.glaciteMineshaft.corpseLocator", "Locate Corpses"),
				tr("nobaaddons.config.mining.glaciteMineshaft.corpseLocator.tooltip", "Marks corpses with a waypoint when they're in your line of sight"),
				default = defaults.mining.glaciteMineshaft.corpseLocator,
				property = config.mining.glaciteMineshaft::corpseLocator
			)
			boolean(
				tr("nobaaddons.config.mining.glaciteMineshaft.autoShareCorpses", "Auto Share Corpses"),
				tr("nobaaddons.config.mining.glaciteMineshaft.autoShareCorpses.tooltip", "Automatically shares the coordinates of the nearest corpse within 5 blocks in party chat"),
				default = defaults.mining.glaciteMineshaft.autoShareCorpses,
				property = config.mining.glaciteMineshaft::autoShareCorpses
			) requires config(locate)
			// endregion

			// region Miscellaneous
			label(CommonText.Config.LABEL_MISC)

			boolean(
				tr("nobaaddons.config.mining.glaciteMineshaft.entranceWaypoint", "Entrance Waypoint"),
				tr("nobaaddons.config.mining.glaciteMineshaft.entranceWaypoint.tooltip", "Adds a waypoint at the mineshaft entrance"),
				default = defaults.mining.glaciteMineshaft.entranceWaypoint,
				property = config.mining.glaciteMineshaft::entranceWaypoint
			)
			boolean(
				tr("nobaaddons.config.mining.glaciteMineshaft.ladderWaypoint", "Ladder Waypoint"),
				tr("nobaaddons.config.mining.glaciteMineshaft.ladderWaypoint.tooltip", "Adds a waypoint at the bottom of the entrance ladder shaft"),
				default = defaults.mining.glaciteMineshaft.ladderWaypoint,
				property = config.mining.glaciteMineshaft::ladderWaypoint
			)
			// endregion
		}
		// endregion
	}
}