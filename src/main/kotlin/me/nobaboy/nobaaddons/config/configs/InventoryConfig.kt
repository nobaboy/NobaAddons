package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry

class InventoryConfig {
	@SerialEntry
	val itemPickupLog = PickupLog()

	class PickupLog {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var timeoutSeconds: Int = 5
	}
}