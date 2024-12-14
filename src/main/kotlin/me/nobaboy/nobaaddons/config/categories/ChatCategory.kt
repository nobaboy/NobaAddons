package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import net.minecraft.text.Text

object ChatCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.chat"))

			// region Alerts
			.buildGroup(Text.translatable("nobaaddons.config.chat.alerts")) {
				// region Crimson Isle
				label(Text.translatable("nobaaddons.config.label.crimsonIsle")).build()

				boolean(
					Text.translatable("nobaaddons.config.chat.alerts.mythicSeaCreatureSpawn"),
					Text.translatable("nobaaddons.config.chat.alerts.mythicSeaCreatureSpawn.tooltip"),
					default = defaults.chat.alerts.mythicSeaCreatureSpawn,
					property = config.chat.alerts::mythicSeaCreatureSpawn
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.alerts.vanquisherSpawn"),
					Text.translatable("nobaaddons.config.chat.alerts.vanquisherSpawn.tooltip"),
					default = defaults.chat.alerts.vanquisherSpawn,
					property = config.chat.alerts::vanquisherSpawn
				)
				// endregion
			}
			// endregion

			// region Filters
			.buildGroup(Text.translatable("nobaaddons.config.chat.filters")) {
				// region Crimson Isle
				label(Text.translatable("nobaaddons.config.label.crimsonIsle"))

				boolean(
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityCooldownMessage"),
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityCooldownMessage.tooltip"),
					default = defaults.chat.filters.hideAbilityCooldownMessage,
					property = config.chat.filters::hideAbilityCooldownMessage
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Implosion"),
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Wither Blade"),
					default = defaults.chat.filters.hideImplosionDamageMessage,
					property = config.chat.filters::hideImplosionDamageMessage
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Molten Wave"),
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Midas Staff"),
					default = defaults.chat.filters.hideMoltenWaveDamageMessage,
					property = config.chat.filters::hideMoltenWaveDamageMessage
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Guided Bat"),
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Spirit Sceptre"),
					default = defaults.chat.filters.hideGuidedBatDamageMessage,
					property = config.chat.filters::hideGuidedBatDamageMessage
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Giant's Slam"),
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Giant's Sword"),
					default = defaults.chat.filters.hideGiantsSlamDamageMessage,
					property = config.chat.filters::hideGiantsSlamDamageMessage
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Throw"),
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Livid Dagger"),
					default = defaults.chat.filters.hideThrowDamageMessage,
					property = config.chat.filters::hideThrowDamageMessage
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Ray of Hope"),
					Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Staff of the Rising Sun"),
					default = defaults.chat.filters.hideRayOfHopeDamageMessage,
					property = config.chat.filters::hideRayOfHopeDamageMessage
				)
				// endregion

				// region Mobs
				label(Text.translatable("nobaaddons.config.label.mobs"))

				boolean(
					Text.translatable("nobaaddons.config.chat.filters.hideSeaCreatureSpawnMessage"),
					Text.translatable("nobaaddons.config.chat.filters.hideSeaCreatureSpawnMessage.tooltip"),
					default = defaults.chat.filters.hideSeaCreatureSpawnMessage,
					property = config.chat.filters::hideSeaCreatureSpawnMessage
				)
				cycler(
					Text.translatable("nobaaddons.config.chat.filters.seaCreatureMaximumRarity"),
					default = defaults.chat.filters.seaCreatureMaximumRarity,
					property = config.chat.filters::seaCreatureMaximumRarity
				)
				// endregion

				// region Dungeons
				label(Text.translatable("nobaaddons.config.label.dungeons"))

				cycler(
					Text.translatable("nobaaddons.config.chat.filters.blessingMessage"),
					default = defaults.chat.filters.blessingMessage,
					property = config.chat.filters::blessingMessage
				)
				cycler(
					Text.translatable("nobaaddons.config.chat.filters.healerOrbMessage"),
					default = defaults.chat.filters.healerOrbMessage,
					property = config.chat.filters::healerOrbMessage
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.filters.pickupObtainMessage"),
					default = defaults.chat.filters.pickupObtainMessage,
					property = config.chat.filters::pickupObtainMessage
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.filters.allowKeyMessage"),
					Text.translatable("nobaaddons.config.chat.filters.allowKeyMessage.tooltip"),
					default = defaults.chat.filters.allowKeyMessage,
					property = config.chat.filters::allowKeyMessage
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.filters.allow5050ItemMessage"),
					Text.translatable("nobaaddons.config.chat.filters.allow5050ItemMessage.tooltip"),
					default = defaults.chat.filters.allow5050ItemMessage,
					property = config.chat.filters::allow5050ItemMessage
				)
				// endregion

				// region Miscellaneous
				label(Text.translatable("nobaaddons.config.label.miscellaneous"))

				boolean(
					Text.translatable("nobaaddons.config.chat.filters.hideTipMessages"),
					Text.translatable("nobaaddons.config.chat.filters.hideTipMessages.tooltip"),
					default = defaults.chat.filters.hideTipMessages,
					property = config.chat.filters::hideTipMessages
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.filters.hideProfileInfo"),
					Text.translatable("nobaaddons.config.chat.filters.hideProfileInfo.tooltip"),
					default = defaults.chat.filters.hideProfileInfo,
					property = config.chat.filters::hideProfileInfo
				)
				// endregion
			}
			// endregion

			// region Chat Commands
			.buildGroup(Text.translatable("nobaaddons.config.chat.chatCommands")) {
				// region DM
				label(Text.translatable("nobaaddons.config.chat.chatCommands.label.dm"))

				boolean(
					Text.translatable("nobaaddons.config.enabled"),
					Text.translatable("nobaaddons.config.chat.chatCommands.dm.enabled.tooltip"),
					default = defaults.chat.chatCommands.dm.enabled,
					property = config.chat.chatCommands.dm::enabled
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.chatCommands.help"),
					Text.translatable("nobaaddons.config.chat.chatCommands.help.tooltip"),
					default = defaults.chat.chatCommands.dm.help,
					property = config.chat.chatCommands.dm::help
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.chatCommands.dm.warpMe"),
					Text.translatable("nobaaddons.config.chat.chatCommands.dm.warpMe.tooltip"),
					default = defaults.chat.chatCommands.dm.warpMe,
					property = config.chat.chatCommands.dm::warpMe
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.chatCommands.dm.partyMe"),
					Text.translatable("nobaaddons.config.chat.chatCommands.dm.partyMe.tooltip"),
					default = defaults.chat.chatCommands.dm.partyMe,
					property = config.chat.chatCommands.dm::partyMe
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.chatCommands.warpOut"),
					Text.translatable("nobaaddons.config.chat.chatCommands.warpOut.tooltip"),
					default = defaults.chat.chatCommands.dm.warpOut,
					property = config.chat.chatCommands.dm::warpOut
				)
				// endregion

				// region Party
				label(Text.translatable("nobaaddons.config.chat.chatCommands.label.party"))

				boolean(
					Text.translatable("nobaaddons.config.enabled"),
					Text.translatable("nobaaddons.config.chat.chatCommands.party.enabled.tooltip"),
					default = defaults.chat.chatCommands.party.enabled,
					property = config.chat.chatCommands.party::enabled
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.chatCommands.help"),
					Text.translatable("nobaaddons.config.chat.chatCommands.help.tooltip"),
					default = defaults.chat.chatCommands.party.help,
					property = config.chat.chatCommands.party::help
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.chatCommands.party.allInvite"),
					Text.translatable("nobaaddons.config.chat.chatCommands.party.allInvite.tooltip"),
					default = defaults.chat.chatCommands.party.allInvite,
					property = config.chat.chatCommands.party::allInvite
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.chatCommands.party.transfer"),
					Text.translatable("nobaaddons.config.chat.chatCommands.party.transfer.tooltip"),
					default = defaults.chat.chatCommands.party.transfer,
					property = config.chat.chatCommands.party::transfer
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.chatCommands.party.warp"),
					Text.translatable("nobaaddons.config.chat.chatCommands.party.warp.tooltip"),
					default = defaults.chat.chatCommands.party.warp,
					property = config.chat.chatCommands.party::warp
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.chatCommands.party.coords"),
					Text.translatable("nobaaddons.config.chat.chatCommands.party.coords.tooltip"),
					default = defaults.chat.chatCommands.party.coords,
					property = config.chat.chatCommands.party::coords
				)
				// endregion

				// region Guild
				label(Text.translatable("nobaaddons.config.chat.chatCommands.label.guild"))

				boolean(
					Text.translatable("nobaaddons.config.enabled"),
					Text.translatable("nobaaddons.config.chat.chatCommands.guild.enabled.tooltip"),
					default = defaults.chat.chatCommands.guild.enabled,
					property = config.chat.chatCommands.guild::enabled
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.chatCommands.help"),
					Text.translatable("nobaaddons.config.chat.chatCommands.help.tooltip"),
					default = defaults.chat.chatCommands.guild.help,
					property = config.chat.chatCommands.guild::help
				)
				boolean(
					Text.translatable("nobaaddons.config.chat.chatCommands.guild.warpOut"),
					Text.translatable("nobaaddons.config.chat.chatCommands.guild.warpOut.tooltip"),
					default = defaults.chat.chatCommands.guild.warpOut,
					property = config.chat.chatCommands.guild::warpOut
				)
				// endregion
			}
			// endregion

			.build()
	}
}