package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.core.MobRarity
import me.nobaboy.nobaaddons.features.chat.filters.ChatFilterOption

class ChatConfig {
	@SerialEntry
	val alerts: Alerts = Alerts()

	@SerialEntry
	val filters: Filters = Filters()

	@SerialEntry
	val chatCommands: ChatCommands = ChatCommands()

	class Alerts {
		@SerialEntry
		var mythicSeaCreatureSpawn: Boolean = false

		@SerialEntry
		var vanquisherSpawn: Boolean = false
	}

	class Filters {
		@SerialEntry
		var hideAbilityCooldownMessage: Boolean = false

		@SerialEntry
		var hideImplosionDamageMessage: Boolean = false

		@SerialEntry
		var hideMoltenWaveDamageMessage: Boolean = false

		@SerialEntry
		var hideSpiritSceptreDamageMessage: Boolean = false

		@SerialEntry
		var hideGiantSwordDamageMessage: Boolean = false

		@SerialEntry
		var hideLividDaggerDamageMessage: Boolean = false

		@SerialEntry
		var hideRayOfHopeDamageMessage: Boolean = false

		@SerialEntry
		var hideSeaCreatureSpawnMessage: Boolean = false

		@SerialEntry
		var seaCreatureMaximumRarity: MobRarity = MobRarity.RARE

		@SerialEntry
		var blessingMessage: ChatFilterOption = ChatFilterOption.SHOWN

		@SerialEntry
		var healerOrbMessage: ChatFilterOption = ChatFilterOption.SHOWN

		@SerialEntry
		var pickupObtainMessage: Boolean = false

		@SerialEntry
		var allowKeyMessage: Boolean = false

		@SerialEntry
		var allow5050ItemMessage: Boolean = false

		@SerialEntry
		var hideProfileInfo: Boolean = false

		@SerialEntry
		var hideTipMessages: Boolean = false
	}

	class ChatCommands {
		@SerialEntry
		val dm: DMCommandsConfig = DMCommandsConfig()

		@SerialEntry
		val party: PartyCommandsConfig = PartyCommandsConfig()

		@SerialEntry
		val guild: GuildCommandsConfig = GuildCommandsConfig()
	}

	class DMCommandsConfig {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var help: Boolean = false

		@SerialEntry
		var warpMe: Boolean = false

		@SerialEntry
		var partyMe: Boolean = false

		@SerialEntry
		var warpOut: Boolean = false
	}

	class PartyCommandsConfig {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var help: Boolean = false

		@SerialEntry
		var allInvite: Boolean = false

		@SerialEntry
		var transfer: Boolean = false

		@SerialEntry
		var warp: Boolean = false

		@SerialEntry
		var coords: Boolean = false
	}

	class GuildCommandsConfig {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var help: Boolean = false

		@SerialEntry
		var warpOut: Boolean = false
	}
}