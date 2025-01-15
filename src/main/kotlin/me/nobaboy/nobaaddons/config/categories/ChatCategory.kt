package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.config.NobaConfigUtils.requires
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Rarity.Companion.toArray
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

object ChatCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.chat", "Chat")) {
		// region Copy Chat
		buildGroup(tr("nobaaddons.config.chat.copyChat", "Copy Chat")) {
			val enabled = boolean(CommonText.Config.ENABLED, default = defaults.chat.copyChat.enabled, property = config.chat.copyChat::enabled)
			cycler(
				tr("nobaaddons.config.chat.copyChat.mode", "Copy Chat With"),
				default = defaults.chat.copyChat.mode,
				property = config.chat.copyChat::mode
			) requires enabled
		}
		// endregion

		// region Alerts
		buildGroup(tr("nobaaddons.config.chat.alerts", "Alerts")) {
			// region Crimson Isle
			label(CommonText.Config.LABEL_CRIMSON_ISLE)

			boolean(
				tr("nobaaddons.config.chat.alerts.mythicSeaCreatureSpawn", "Alert Mythic Sea Creature Catches"),
				tr("nobaaddons.config.chat.alerts.mythicSeaCreatureSpawn.tooltip", "Sends your current location in all chat when catching a mythic sea creature"),
				default = defaults.chat.alerts.mythicSeaCreatureSpawn,
				property = config.chat.alerts::mythicSeaCreatureSpawn
			)
			boolean(
				tr("nobaaddons.config.chat.alerts.vanquisherSpawn", "Alert Vanquisher Spawn"),
				tr("nobaaddons.config.chat.alerts.vanquisherSpawn.tooltip", "Sends your current location in all chat when you spawn a Vanquisher"),
				default = defaults.chat.alerts.vanquisherSpawn,
				property = config.chat.alerts::vanquisherSpawn
			)
			// endregion
		}
		// endregion

		// region Filters
		buildGroup(tr("nobaaddons.config.chat.filters", "Filters")) {
			// region Crimson Isle
			label(CommonText.Config.LABEL_CRIMSON_ISLE)

			fun abilityDamageTitle(source: Text) = tr("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Hide $source Damage")
			fun abilityDamageDescription(source: Text) = tr("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Hides the ability damage message from $source")

			boolean(
				tr("nobaaddons.config.chat.filters.hideAbilityCooldownMessage", "Hide Ability Cooldown Messages"),
				tr("nobaaddons.config.chat.filters.hideAbilityCooldownMessage.tooltip", "Hides the 'This ability is on cooldown for Xs.' message from chat"),
				default = defaults.chat.filters.hideAbilityCooldownMessage,
				property = config.chat.filters::hideAbilityCooldownMessage
			)
			boolean(
				abilityDamageTitle(tr("nobaaddons.label.ability.implosion", "Implosion")),
				abilityDamageDescription(tr("nobaaddons.label.ability.implosionOrImpact", "Implosion/Wither Impact")),
				default = defaults.chat.filters.hideImplosionDamageMessage,
				property = config.chat.filters::hideImplosionDamageMessage
			)
			boolean(
				abilityDamageTitle(tr("nobaaddons.label.ability.moltenWave", "Molten Wave")),
				abilityDamageDescription(tr("nobaaddons.label.item.midasStaff", "Midas Staff")),
				default = defaults.chat.filters.hideMoltenWaveDamageMessage,
				property = config.chat.filters::hideMoltenWaveDamageMessage
			)
			boolean(
				abilityDamageTitle(tr("nobaaddons.label.ability.guidedBat", "Guided Bat")),
				abilityDamageDescription(tr("nobaaddons.label.item.spiritSceptre", "Spirit Sceptre")),
				default = defaults.chat.filters.hideGuidedBatDamageMessage,
				property = config.chat.filters::hideGuidedBatDamageMessage
			)
			boolean(
				abilityDamageTitle(tr("nobaaddons.label.ability.giantsSlam", "Giant's Slam")),
				abilityDamageDescription(tr("nobaaddons.label.item.giantsSword", "Giant's Sword")),
				default = defaults.chat.filters.hideGiantsSlamDamageMessage,
				property = config.chat.filters::hideGiantsSlamDamageMessage
			)
			boolean(
				abilityDamageTitle(tr("nobaaddons.label.item.lividDagger", "Livid Dagger")),
				abilityDamageDescription(tr("nobaaddons.label.item.lividDagger", "Livid Dagger")),
				default = defaults.chat.filters.hideThrowDamageMessage,
				property = config.chat.filters::hideThrowDamageMessage
			)
			boolean(
				abilityDamageTitle(tr("nobaaddons.label.ability.rayOfHope", "Ray of Hope")),
				abilityDamageDescription(tr("nobaaddons.label.item.staffOfRisingSun", "Staff of the Rising Sun")),
				default = defaults.chat.filters.hideRayOfHopeDamageMessage,
				property = config.chat.filters::hideRayOfHopeDamageMessage
			)
			// endregion

			// region Mobs
			label(CommonText.Config.LABEL_MOBS)

			boolean(
				tr("nobaaddons.config.chat.filters.hideSeaCreatureSpawnMessage", "Hide Sea Creature Catch Message"),
				tr("nobaaddons.config.chat.filters.hideSeaCreatureSpawnMessage.tooltip", "Hides the catch message for sea creatures of the below set rarity and under"),
				default = defaults.chat.filters.hideSeaCreatureSpawnMessage,
				property = config.chat.filters::hideSeaCreatureSpawnMessage
			)
			cycler(
				tr("nobaaddons.config.chat.filters.seaCreatureMaximumRarity", "Hide Rarity and Below"),
				default = defaults.chat.filters.seaCreatureMaximumRarity,
				property = config.chat.filters::seaCreatureMaximumRarity,
				onlyInclude = (Rarity.COMMON..Rarity.MYTHIC).toArray()
			)
			// endregion

			// region Dungeons
			label(CommonText.Config.LABEL_DUNGEONS)

			cycler(
				tr("nobaaddons.config.chat.filters.blessingMessage", "Blessing Message"),
				default = defaults.chat.filters.blessingMessage,
				property = config.chat.filters::blessingMessage
			)
			cycler(
				tr("nobaaddons.config.chat.filters.healerOrbMessage", "Healer Orb Message"),
				default = defaults.chat.filters.healerOrbMessage,
				property = config.chat.filters::healerOrbMessage
			)
			boolean(
				tr("nobaaddons.config.chat.filters.pickupObtainMessage", "Hide Item Obtain Messages"),
				default = defaults.chat.filters.pickupObtainMessage,
				property = config.chat.filters::pickupObtainMessage
			)
			boolean(
				tr("nobaaddons.config.chat.filters.allowKeyMessage", "Allow Key Message"),
				tr("nobaaddons.config.chat.filters.allowKeyMessage.tooltip", "Allows key messages even when item obtain messages are hidden"),
				default = defaults.chat.filters.allowKeyMessage,
				property = config.chat.filters::allowKeyMessage
			)
			boolean(
				tr("nobaaddons.config.chat.filters.allow5050ItemMessage", "Allow 50/50 Item Messages"),
				tr("nobaaddons.config.chat.filters.allow5050ItemMessage.tooltip", "Allows 50/50 item quality pickups to be shown even when item obtain messages are hidden"),
				default = defaults.chat.filters.allow5050ItemMessage,
				property = config.chat.filters::allow5050ItemMessage
			)
			// endregion

			// region Miscellaneous
			label(CommonText.Config.LABEL_MISC)

			boolean(
				tr("nobaaddons.config.chat.filters.hideTipMessages", "Hide Tip Messages"),
				tr("nobaaddons.config.chat.filters.hideTipMessages.tooltip", "Hides messages related to /tip"),
				default = defaults.chat.filters.hideTipMessages,
				property = config.chat.filters::hideTipMessages
			)
			boolean(
				tr("nobaaddons.config.chat.filters.hideProfileInfo", "Hide Profile Info Messages"),
				tr("nobaaddons.config.chat.filters.hideProfileInfo.tooltip", "Hides messages showing the active profile and profile ID"),
				default = defaults.chat.filters.hideProfileInfo,
				property = config.chat.filters::hideProfileInfo
			)
			// endregion
		}
		// endregion

		// region Chat Commands
		buildGroup(tr("nobaaddons.config.chat.chatCommands", "Chat Commands")) {
			// region DM
			label(tr("nobaaddons.config.chat.chatCommands.label.dm", "DM Commands"))

			val helpTitle = tr("nobaaddons.config.chat.chatCommands.help", "!help Command")
			val helpDescription = tr("nobaaddons.config.chat.chatCommands.help.tooltip", "Responds with a list of available commands")

			val warpOutTitle = tr("nobaaddons.config.chat.chatCommands.warpOut", "!warpout Command")
			val warpOutDescription = tr("nobaaddons.config.chat.chatCommands.warpOut.tooltip", "Warps the specified player to your lobby when used")

			val dmEnabled = boolean(
				CommonText.Config.ENABLED,
				tr("nobaaddons.config.chat.chatCommands.dm.enabled.tooltip", "Enables chat commands when other players /msg you"),
				default = defaults.chat.chatCommands.dm.enabled,
				property = config.chat.chatCommands.dm::enabled
			)
			boolean(
				helpTitle, helpDescription,
				default = defaults.chat.chatCommands.dm.help,
				property = config.chat.chatCommands.dm::help
			) requires dmEnabled
			boolean(
				tr("nobaaddons.config.chat.chatCommands.dm.warpMe", "!warpme Command"),
				tr("nobaaddons.config.chat.chatCommands.dm.warpMe.tooltip", "Warps the messaging player to your lobby"),
				default = defaults.chat.chatCommands.dm.warpMe,
				property = config.chat.chatCommands.dm::warpMe
			) requires dmEnabled
			boolean(
				tr("nobaaddons.config.chat.chatCommands.dm.partyMe", "!partyme Command"),
				tr("nobaaddons.config.chat.chatCommands.dm.partyMe.tooltip", "Invites the messaging player to your party"),
				default = defaults.chat.chatCommands.dm.partyMe,
				property = config.chat.chatCommands.dm::partyMe
			) requires dmEnabled
			boolean(
				warpOutTitle, warpOutDescription,
				default = defaults.chat.chatCommands.dm.warpOut,
				property = config.chat.chatCommands.dm::warpOut
			) requires dmEnabled
			// endregion

			// region Party
			label(tr("nobaaddons.config.chat.chatCommands.label.party", "Party Commands"))

			val partyEnabled = boolean(
				CommonText.Config.ENABLED,
				tr("nobaaddons.config.chat.chatCommands.party.enabled.tooltip", "Enables chat commands in party chat"),
				default = defaults.chat.chatCommands.party.enabled,
				property = config.chat.chatCommands.party::enabled
			)
			boolean(
				helpTitle, helpDescription,
				default = defaults.chat.chatCommands.party.help,
				property = config.chat.chatCommands.party::help
			) requires partyEnabled
			boolean(
				tr("nobaaddons.config.chat.chatCommands.party.allInvite", "!allinvite Command"),
				tr("nobaaddons.config.chat.chatCommands.party.allInvite.tooltip", "Runs '/p settings allinvite' when used if you're the party leader"),
				default = defaults.chat.chatCommands.party.allInvite,
				property = config.chat.chatCommands.party::allInvite
			) requires partyEnabled
			boolean(
				tr("nobaaddons.config.chat.chatCommands.party.transfer", "!transfer Command"),
				tr("nobaaddons.config.chat.chatCommands.party.transfer.tooltip", "Transfers the party to the player using the command (or the specified player, if any) if you're the party leader"),
				default = defaults.chat.chatCommands.party.transfer,
				property = config.chat.chatCommands.party::transfer
			) requires partyEnabled
			boolean(
				tr("nobaaddons.config.chat.chatCommands.party.warp", "!warp Command"),
				tr("nobaaddons.config.chat.chatCommands.party.warp.tooltip", "Warps the party to your current lobby (with an optional seconds delay) if you're the leader"),
				default = defaults.chat.chatCommands.party.warp,
				property = config.chat.chatCommands.party::warp
			) requires partyEnabled
			boolean(
				tr("nobaaddons.config.chat.chatCommands.party.coords", "!coords Command"),
				tr("nobaaddons.config.chat.chatCommands.party.coords.tooltip", "Responds with your current in-game coordinates"),
				default = defaults.chat.chatCommands.party.coords,
				property = config.chat.chatCommands.party::coords
			) requires partyEnabled
			boolean(
				tr("nobaaddons.config.chat.chatCommands.party.joinInstanced", "Join Instanced Islands Commands"),
				tr("nobaaddons.config.chat.chatCommands.party.joinInstanced.tooltip", "Commands such as !m6, !f7, !t5, etc."),
				default = defaults.chat.chatCommands.party.joinInstanced,
				property = config.chat.chatCommands.party::joinInstanced
			) requires partyEnabled
			// endregion

			// region Guild
			label(tr("nobaaddons.config.chat.chatCommands.label.guild", "Guild Command"))

			val guildEnabled = boolean(
				CommonText.Config.ENABLED,
				tr("nobaaddons.config.chat.chatCommands.guild.enabled.tooltip", "Enables chat commands in guild chat"),
				default = defaults.chat.chatCommands.guild.enabled,
				property = config.chat.chatCommands.guild::enabled
			)
			boolean(
				helpTitle, helpDescription,
				default = defaults.chat.chatCommands.guild.help,
				property = config.chat.chatCommands.guild::help
			) requires guildEnabled
			boolean(
				warpOutTitle, warpOutDescription,
				default = defaults.chat.chatCommands.guild.warpOut,
				property = config.chat.chatCommands.guild::warpOut
			) requires guildEnabled
			// endregion
		}
		// endregion
	}
}