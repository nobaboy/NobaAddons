package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.features.chat.CopyChatFeature
import me.nobaboy.nobaaddons.features.chat.filters.ChatFilterOption

class ChatConfig {
	@Object val alerts = Alerts()
	@Object val filters = Filters()
	@Object val chatCommands = ChatCommands()
	@Object val copyChat = CopyChat()

	class CopyChat {
		var enabled = false
		var mode = CopyChatFeature.CopyWith.RIGHT_CLICK
	}

	class Alerts {
		var mythicSeaCreatureSpawn = false
		var vanquisherSpawn = false
	}

	class Filters {
		var hideAbilityCooldownMessage = false
		var hideImplosionDamageMessage = false
		var hideMoltenWaveDamageMessage = false
		var hideGuidedBatDamageMessage = false
		var hideGiantsSlamDamageMessage = false
		var hideThrowDamageMessage = false
		var hideRayOfHopeDamageMessage = false

		var hideSeaCreatureSpawnMessage = false
		var seaCreatureMaximumRarity = Rarity.RARE

		var blessingMessage = ChatFilterOption.SHOWN
		var healerOrbMessage = ChatFilterOption.SHOWN
		var pickupObtainMessage = false
		var allowKeyMessage = false
		var allow5050ItemMessage = false

		var hideProfileInfo = false
		var hideTipMessages = false
	}

	class ChatCommands {
		@Object val dm = DMCommands()
		@Object val party = PartyCommands()
		@Object val guild = GuildCommands()

		class DMCommands {
			var enabled = false
			var help = false
			var warpMe = false
			var partyMe = false
			var warpOut = false
		}

		class PartyCommands : ObjectProperty<PartyCommands>("party") {
			var enabled = false
			var help = false
			var allInvite = false
			var transfer = false
			var warp = false
			var coords = false
			var joinInstanced = false
		}

		class GuildCommands : ObjectProperty<GuildCommands>("guild") {
			var enabled = false
			var help = false
			var warpOut = false
		}
	}
}