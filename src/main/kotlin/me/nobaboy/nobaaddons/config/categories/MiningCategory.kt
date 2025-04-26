package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.config.util.builders.CategoryBuilder
import me.nobaboy.nobaaddons.config.util.builders.OptionBuilder.Companion.descriptionText
import me.nobaboy.nobaaddons.config.util.builders.label
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr

object MiningCategory {
	fun create() = category(tr("nobaaddons.config.mining", "Mining")) {
		wormAlert()
		glaciteMineshaft()
	}

	private fun CategoryBuilder.wormAlert() {
		group(tr("nobaaddons.config.mining.wormAlert", "Worm Alert")) {
			val enabled = add({ mining.wormAlert::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ mining.wormAlert::alertColor }, BiMapper.NobaAWTColorMapper) {
				name = CommonText.Config.ALERT_COLOR
				require { option(enabled) }
				colorController()
			}
		}
	}

	private fun CategoryBuilder.glaciteMineshaft() {
		group(tr("nobaaddons.config.mining.glaciteMineshaft", "Glacite Mineshaft")) {
			label(tr("nobaaddons.config.mining.glaciteMineshaft.label.corpses", "Corpses"))

			val locate = add({ mining.glaciteMineshaft::corpseLocator }) {
				name = tr("nobaaddons.config.mining.glaciteMineshaft.corpseLocator", "Locate Corpses")
				descriptionText = tr("nobaaddons.config.mining.glaciteMineshaft.corpseLocator.tooltip", "Marks corpses with a waypoint when they're in your line of sight")
				booleanController()
			}
			add({ mining.glaciteMineshaft::autoShareCorpses }) {
				name = tr("nobaaddons.config.mining.glaciteMineshaft.autoShareCorpses", "Auto Share Corpses")
				descriptionText = tr("nobaaddons.config.mining.glaciteMineshaft.autoShareCorpses.tooltip", "Automatically shares the coordinates of the nearest corpse within 5 blocks in party chat")
				require { option(locate) }
				booleanController()
			}

			label(CommonText.Config.LABEL_MISC)

			add({ mining.glaciteMineshaft::entranceWaypoint }) {
				name = tr("nobaaddons.config.mining.glaciteMineshaft.entranceWaypoint", "Entrance Waypoint")
				descriptionText = tr("nobaaddons.config.mining.glaciteMineshaft.entranceWaypoint.tooltip", "Adds a waypoint at the mineshaft entrance")
				booleanController()
			}
			add({ mining.glaciteMineshaft::ladderWaypoint }) {
				name = tr("nobaaddons.config.mining.glaciteMineshaft.ladderWaypoint", "Ladder Waypoint")
				descriptionText = tr("nobaaddons.config.mining.glaciteMineshaft.ladderWaypoint.tooltip", "Adds a waypoint at the bottom of the entrance ladder shaft")
				booleanController()
			}
		}
	}
}