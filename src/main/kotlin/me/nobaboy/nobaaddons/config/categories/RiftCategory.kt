package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.utils.*
import me.nobaboy.nobaaddons.utils.tr

object RiftCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = buildCategory(tr("nobaaddons.config.rift", "Rift")) {
		group(tr("nobaaddons.config.rift.timers", "Rift Timers")) {
			val infusion = boolean(
				tr("nobaaddons.config.rift.timers.freeInfusions", "Free Infusions"),
				tr("nobaaddons.config.rift.timers.freeInfusions.tooltip", "Sends a message in chat when you regain a free Rift infusion"),
				default = defaults.rift.freeInfusionAlert,
				property = config.rift::freeInfusionAlert
			)
			val ss = boolean(
				tr("nobaaddons.config.rift.timers.splitSteal", "Split or Steal"),
				tr("nobaaddons.config.rift.timers.splitSteal.tooltip", "Sends a message in chat when the Split or Steal cooldown ends"),
				default = defaults.rift.splitStealAlert,
				property = config.rift::splitStealAlert
			)
			boolean(
				tr("nobaaddons.config.rift.timers.splitStealItemCooldown", "Display Cooldown on Ubik's Cube"),
				tr("nobaaddons.config.rift.timers.splitStealItemCooldown.tooltip", "Adds the Split or Steal cooldown time remaining to the Ubik's Cube item tooltip"),
				default = defaults.rift.splitStealItemCooldown,
				property = config.rift::splitStealItemCooldown
			)
			cycler(
				tr("nobaaddons.config.rift.timers.warpTarget", "Warp To"),
				tr("nobaaddons.config.rift.timers.warpTarget.tooltip", "Where clicking on the sent chat message should warp you"),
				default = defaults.rift.warpTarget,
				property = config.rift::warpTarget
			)// requires any(configOption(infusion), configOption(ss))
		}
	}
}