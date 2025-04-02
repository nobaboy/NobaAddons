package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.features.chat.CopyChatFeature
import me.nobaboy.nobaaddons.features.chat.filters.ChatFilterOption

class ChatConfig : ObjectProperty<ChatConfig>("chat") {
	val filters by Filters()
	val chatCommands by ChatCommands()
	val copyChat by CopyChat()

	class CopyChat : ObjectProperty<CopyChat>("copy") {
		var enabled by Property.of("enabled", false)
		var mode by Property.of("button", Serializer.enum(), CopyChatFeature.CopyWith.RIGHT_CLICK)
	}

	class Filters : ObjectProperty<Filters>("filters") {
		var hideAbilityCooldownMessage by Property.of<Boolean>("hideAbilityCooldownMessage", false)
		var hideImplosionDamageMessage by Property.of<Boolean>("hideImplosionDamageMessage", false)
		var hideMoltenWaveDamageMessage by Property.of<Boolean>("hideMoltenWaveDamageMessage", false)
		var hideGuidedBatDamageMessage by Property.of<Boolean>("hideGuidedBatDamageMessage", false)
		var hideGiantsSlamDamageMessage by Property.of<Boolean>("hideGiantsSlamDamageMessage", false)
		var hideThrowDamageMessage by Property.of<Boolean>("hideThrowDamageMessage", false)
		var hideRayOfHopeDamageMessage by Property.of<Boolean>("hideRayOfHopeDamageMessage", false)

		var blessingMessage by Property.of("blessingMessage", Serializer.enum(), ChatFilterOption.SHOWN)
		var healerOrbMessage by Property.of("healerOrbMessage",Serializer.enum(), ChatFilterOption.SHOWN)
		var pickupObtainMessage by Property.of<Boolean>("pickupObtainMessage", false)
		var allowKeyMessage by Property.of<Boolean>("allowKeyMessage", false)
		var allow5050ItemMessage by Property.of<Boolean>("allow5050ItemMessage", false)

		var hideProfileInfo by Property.of<Boolean>("hideProfileInfo", false)
		var hideTipMessages by Property.of<Boolean>("hideTipMessages", false)
	}

	class ChatCommands : ObjectProperty<ChatCommands>("chatCommands") {
		val dm by DMCommands()
		val party by PartyCommands()
		val guild by GuildCommands()

		class DMCommands : ObjectProperty<DMCommands>("dm") {
			var enabled by Property.of<Boolean>("enabled", false)
			var help by Property.of<Boolean>("help", false)
			var warpMe by Property.of<Boolean>("warpMe", false)
			var partyMe by Property.of<Boolean>("partyMe", false)
			var warpOut by Property.of<Boolean>("warpOut", false)
		}

		class PartyCommands : ObjectProperty<PartyCommands>("party") {
			var enabled by Property.of<Boolean>("enabled", false)
			var help by Property.of<Boolean>("help", false)
			var allInvite by Property.of<Boolean>("allInvite", false)
			var transfer by Property.of<Boolean>("transfer", false)
			var warp by Property.of<Boolean>("warp", false)
			var coords by Property.of<Boolean>("coords", false)
			var joinInstanced by Property.of<Boolean>("joinInstanced", false)
		}

		class GuildCommands : ObjectProperty<GuildCommands>("guild") {
			var enabled by Property.of<Boolean>("enabled", false)
			var help by Property.of<Boolean>("help", false)
			var warpOut by Property.of<Boolean>("warpOut", false)
		}
	}
}