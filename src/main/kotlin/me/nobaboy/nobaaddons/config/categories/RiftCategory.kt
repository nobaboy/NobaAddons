package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.config.util.builders.CategoryBuilder
import me.nobaboy.nobaaddons.config.util.builders.OptionBuilder.Companion.descriptionText
import me.nobaboy.nobaaddons.features.rift.RiftTimers
import me.nobaboy.nobaaddons.utils.tr

object RiftCategory {
	fun create() = category(tr("nobaaddons.config.rift", "Rift")) {
		riftTimers()
	}

	private fun CategoryBuilder.riftTimers() {
		group(RiftTimers) {
			val infusion = add({ rift::freeInfusionAlert }) {
				name = tr("nobaaddons.config.rift.timers.freeInfusions", "Free Infusions")
				descriptionText = tr("nobaaddons.config.rift.timers.freeInfusions.tooltip", "Sends a message in chat when you regain a free Rift infusion")
				booleanController()
			}
			val ss = add({ rift::splitStealAlert }) {
				name = tr("nobaaddons.config.rift.timers.splitSteal", "Split or Steal")
				descriptionText = tr("nobaaddons.config.rift.timers.splitSteal.tooltip", "Sends a message in chat when the Split or Steal cooldown ends")
				booleanController()
			}
			add({ rift::splitStealItemCooldown }) {
				name = tr("nobaaddons.config.rift.timers.splitStealItemCooldown", "Display Cooldown on Ubik's Cube")
				descriptionText = tr("nobaaddons.config.rift.timers.splitStealItemCooldown.tooltip", "Adds the Split or Steal cooldown time remaining to the Ubik's Cube item tooltip")
				booleanController()
			}
			add({ rift::warpTarget }) {
				name = tr("nobaaddons.config.rift.timers.warpTarget", "Warp To")
				descriptionText = tr("nobaaddons.config.rift.timers.warpTarget.tooltip", "Where clicking on the sent chat message should warp you")
				require { option(infusion) or option(ss) }
				enumController()
			}
		}
	}
}