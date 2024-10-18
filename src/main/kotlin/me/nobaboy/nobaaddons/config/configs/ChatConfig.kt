package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.features.chat.filters.ChatFilterOption

class ChatConfig {
	@SerialEntry
	val filters: Filters = Filters()

	@SerialEntry
	val alerts: Alerts = Alerts()

	class Filters {
		@SerialEntry
		var hideAbilityDamageMessage: Boolean = false

		@SerialEntry
		var hideProfileInfo: Boolean = false

		@SerialEntry
		var hideTipMessages: Boolean = false

		@SerialEntry
		var blessingMessage: ChatFilterOption = ChatFilterOption.SHOWN

		@SerialEntry
		var healerOrbMessage: ChatFilterOption = ChatFilterOption.SHOWN

		@SerialEntry
		var pickupObtainMessage: Boolean = false

		@SerialEntry
		var allow5050ItemMessage: Boolean = false
	}

	class Alerts {
		@SerialEntry
		var mythicSeaCreatureSpawn: Boolean = false

		@SerialEntry
		var vanquisherSpawn: Boolean = false
	}
}