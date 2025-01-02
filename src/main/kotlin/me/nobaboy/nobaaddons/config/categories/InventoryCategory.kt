package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.config.NobaConfigUtils.slider
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr

object InventoryCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.inventory", "Inventory")) {
		buildGroup(tr("nobaaddons.config.inventory.pickupLog", "Item Pickup Log")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.inventory.pickupLog.enabled,
				property = config.inventory.pickupLog::enabled
			)
			slider(
				tr("nobaaddons.config.inventory.pickupLog.timeout", "Expire After Seconds"),
				min = 1,
				max = 10,
				step = 1,
				default = defaults.inventory.pickupLog.timeoutSeconds,
				property = defaults.inventory.pickupLog::timeoutSeconds
			)
		}
	}
}