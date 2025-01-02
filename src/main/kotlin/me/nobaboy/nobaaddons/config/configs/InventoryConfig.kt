package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry

class InventoryConfig {
	@SerialEntry
	val pickupLog = PickupLog()

	class PickupLog {
		@SerialEntry
		var enabled: Boolean = true // TODO disable by default

		@SerialEntry
		var timeoutSeconds: Int = 5
	}
}