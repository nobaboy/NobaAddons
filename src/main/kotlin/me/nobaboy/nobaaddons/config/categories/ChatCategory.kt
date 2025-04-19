package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Rarity.Companion.toArray
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

object ChatCategory {
	fun create() = category(tr("nobaaddons.config.chat", "Chat")) {
		add({ chat::displayCurrentChannel }) {
			name = tr("nobaaddons.config.chat.displayCurrentChannel", "Display Current Chat Channel")
			descriptionText = tr("nobaaddons.config.chat.displayCurrentChannel.tooltip", "Displays the current /chat channel you're in above the chat text box")
			booleanController()
		}

		copyChat()
		filters()
		chatCommands()
	}

	private fun ConfigCategory.Builder.copyChat() {
		group(tr("nobaaddons.config.chat.copyChat", "Copy Chat")) {
			val enabled = add({ chat.copyChat::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ chat.copyChat::mode }) {
				name = tr("nobaaddons.config.chat.copyChat.mode", "Copy Chat With")
				enumController()
				require { option(enabled) }
			}
		}
	}

	private fun OptionBuilder<Boolean>.itemAbilityFilterName(ability: Text, item: Text?) {
		this.name = tr("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Hide $ability Damage")
		descriptionText = tr("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Hides the ability damage message from ${item ?: ability}")
	}

	private fun ConfigCategory.Builder.filters() {
		group(tr("nobaaddons.config.chat.filters", "Filters")) {
			// region Crimson Isle
			label(CommonText.Config.LABEL_CRIMSON_ISLE)

			add({ chat.filters::hideAbilityCooldownMessage }) {
				name = tr("nobaaddons.config.chat.filters.hideAbilityCooldownMessage", "Hide Ability Cooldown Messages")
				descriptionText = tr("nobaaddons.config.chat.filters.hideAbilityCooldownMessage.tooltip", "Hides the 'This ability is on cooldown for Xs.' message from chat")
				booleanController()
			}

			add({ chat.filters::hideImplosionDamageMessage }) {
				itemAbilityFilterName(
					tr("nobaaddons.label.ability.implosion", "Implosion"),
					tr("nobaaddons.label.ability.implosionOrImpact", "Implosion/Wither Impact")
				)
				booleanController()
			}
			add({ chat.filters::hideMoltenWaveDamageMessage }) {
				itemAbilityFilterName(
					tr("nobaaddons.label.ability.moltenWave", "Molten Wave"),
					tr("nobaaddons.label.item.midasStaff", "Midas Staff")
				)
				booleanController()
			}
			add({ chat.filters::hideGuidedBatDamageMessage }) {
				itemAbilityFilterName(
					tr("nobaaddons.label.ability.guidedBat", "Guided Bat"),
					tr("nobaaddons.label.item.spiritSceptre", "Spirit Sceptre")
				)
				booleanController()
			}
			add({ chat.filters::hideGiantsSlamDamageMessage }) {
				itemAbilityFilterName(
					tr("nobaaddons.label.ability.giantsSlam", "Giant's Slam"),
					tr("nobaaddons.label.item.giantsSword", "Giant's Sword")
				)
				booleanController()
			}
			add({ chat.filters::hideThrowDamageMessage }) {
				itemAbilityFilterName(tr("nobaaddons.label.item.lividDagger", "Livid Dagger"), null)
				booleanController()
			}
			add({ chat.filters::hideRayOfHopeDamageMessage }) {
				itemAbilityFilterName(
					tr("nobaaddons.label.ability.rayOfHope", "Ray of Hope"),
					tr("nobaaddons.label.item.staffOfRisingSun", "Staff of the Rising Sun")
				)
				booleanController()
			}
			// endregion

			// region Mobs
			label(CommonText.Config.LABEL_MOBS)

			add({ chat.filters::hideSeaCreatureCatchMessage }) {
				name = tr("nobaaddons.config.chat.filters.hideSeaCreatureCatchMessage", "Hide Sea Creature Catch Message")
				descriptionText = tr("nobaaddons.config.chat.filters.hideSeaCreatureCatchMessage.tooltip", "Hides the catch message for sea creatures of the below set rarity and under")
				booleanController()
			}
			add({ chat.filters::seaCreatureMaxRarity }) {
				name = tr("nobaaddons.config.chat.filters.seaCreatureMaxRarity", "Sea Creature Max Rarity")
				enumController(onlyInclude = (Rarity.COMMON..Rarity.MYTHIC).toArray())
			}
			// endregion

			// region Dungeons
			label(CommonText.Config.LABEL_DUNGEONS)

			add({ chat.filters::blessingMessage }) {
				name = tr("nobaaddons.config.chat.filters.blessingMessage", "Blessing Message")
				enumController()
			}
			add({ chat.filters::healerOrbMessage }) {
				name = tr("nobaaddons.config.chat.filters.healerOrbMessage", "Healer Orb Message")
				enumController()
			}
			add({ chat.filters::pickupObtainMessage }) {
				name = tr("nobaaddons.config.chat.filters.pickupObtainMessage", "Hide Item Obtain Messages")
				booleanController()
			}
			add({ chat.filters::allowKeyMessage }) {
				name = tr("nobaaddons.config.chat.filters.allowKeyMessage", "Allow Key Message")
				descriptionText = tr("nobaaddons.config.chat.filters.allowKeyMessage.tooltip", "Allows key messages even when item obtain messages are hidden")
				booleanController()
			}
			add({ chat.filters::allow5050ItemMessage }) {
				name = tr("nobaaddons.config.chat.filters.allow5050ItemMessage", "Allow 50/50 Item Messages")
				descriptionText = tr("nobaaddons.config.chat.filters.allow5050ItemMessage.tooltip", "Allows 50/50 item quality pickups to be shown even when item obtain messages are hidden")
				booleanController()
			}
			// endregion

			// region Miscellaneous
			label(CommonText.Config.LABEL_MISC)

			add({ chat.filters::hideTipMessages }) {
				name = tr("nobaaddons.config.chat.filters.hideTipMessages", "Hide /tip Messages")
				booleanController()
			}
			add({ chat.filters::hideProfileInfo }) {
				name = tr("nobaaddons.config.chat.filters.hideProfileInfo", "Hide Profile Info Messages")
				descriptionText = tr("nobaaddons.config.chat.filters.hideProfileInfo.tooltip", "Hides messages showing the active profile and profile ID")
				booleanController()
			}
			// endregion
		}
	}

	private fun ConfigCategory.Builder.chatCommands() {
		group(tr("nobaaddons.config.chat.chatCommands", "Chat Commands")) {
			// region DM
			label(tr("nobaaddons.config.chat.chatCommands.label.dm", "DM Commands"))

			val helpTitle = tr("nobaaddons.config.chat.chatCommands.help", "!help Command")
			val helpDescription = tr("nobaaddons.config.chat.chatCommands.help.tooltip", "Responds with a list of available commands")

			val warpOutTitle = tr("nobaaddons.config.chat.chatCommands.warpOut", "!warpout Command")
			val warpOutDescription = tr("nobaaddons.config.chat.chatCommands.warpOut.tooltip", "Warps the specified player to your lobby when used")

			val dmEnabled = add({ chat.chatCommands.dm::enabled }) {
				name = CommonText.Config.ENABLED
				descriptionText = tr("nobaaddons.config.chat.chatCommands.dm.enabled.tooltip", "Enables chat commands when other players /msg you")
				booleanController()
			}
			add({ chat.chatCommands.dm::help }) {
				name = helpTitle
				descriptionText = helpDescription
				require { option(dmEnabled) }
				booleanController()
			}
			add({ chat.chatCommands.dm::warpMe }) {
				name = tr("nobaaddons.config.chat.chatCommands.dm.warpMe", "!warpme Command")
				descriptionText = tr("nobaaddons.config.chat.chatCommands.dm.warpMe.tooltip", "Warps the messaging player to your lobby")
				require { option(dmEnabled) }
				booleanController()
			}
			add({ chat.chatCommands.dm::partyMe }) {
				name = tr("nobaaddons.config.chat.chatCommands.dm.partyMe", "!partyme Command")
				descriptionText = tr("nobaaddons.config.chat.chatCommands.dm.partyMe.tooltip", "Invites the messaging player to your party")
				require { option(dmEnabled) }
				booleanController()
			}
			add({ chat.chatCommands.dm::warpOut }) {
				name = warpOutTitle
				descriptionText = warpOutDescription
				require { option(dmEnabled) }
				booleanController()
			}
			// endregion

			// region Party
			label(tr("nobaaddons.config.chat.chatCommands.label.party", "Party Commands"))

			val partyEnabled = add({ chat.chatCommands.party::enabled }) {
				name = CommonText.Config.ENABLED
				descriptionText = tr("nobaaddons.config.chat.chatCommands.party.enabled.tooltip", "Enables chat commands in party chat")
				booleanController()
			}
			add({ chat.chatCommands.party::help }) {
				name = helpTitle
				descriptionText = helpDescription
				require { option(partyEnabled) }
				booleanController()
			}
			add({ chat.chatCommands.party::allInvite }) {
				name = tr("nobaaddons.config.chat.chatCommands.party.allInvite", "!allinvite Command")
				descriptionText = tr("nobaaddons.config.chat.chatCommands.party.allInvite.tooltip", "Runs '/p settings allinvite' when used if you're the party leader")
				require { option(partyEnabled) }
				booleanController()
			}
			add({ chat.chatCommands.party::transfer }) {
				name = tr("nobaaddons.config.chat.chatCommands.party.transfer", "!transfer Command")
				descriptionText = tr("nobaaddons.config.chat.chatCommands.party.transfer.tooltip", "Transfers the party to the player using the command (or the specified player, if any) if you're the party leader")
				require { option(partyEnabled) }
				booleanController()
			}
			add({ chat.chatCommands.party::warp }) {
				name = tr("nobaaddons.config.chat.chatCommands.party.warp", "!warp Command")
				descriptionText = tr("nobaaddons.config.chat.chatCommands.party.warp.tooltip", "Warps the party to your current lobby (with an optional seconds delay) if you're the leader")
				require { option(partyEnabled) }
				booleanController()
			}
			add({ chat.chatCommands.party::coords }) {
				name = tr("nobaaddons.config.chat.chatCommands.party.coords", "!coords Command")
				descriptionText = tr("nobaaddons.config.chat.chatCommands.party.coords.tooltip", "Responds with your current in-game coordinates")
				require { option(partyEnabled) }
				booleanController()
			}
			add({ chat.chatCommands.party::joinInstanced }) {
				name = tr("nobaaddons.config.chat.chatCommands.party.joinInstanced", "Instance Commands")
				descriptionText = tr("nobaaddons.config.chat.chatCommands.party.joinInstanced.tooltip", "Commands such as !m6 (Master Mode Catacombs), !f7 (Catacombs), !t5 (Kuudra), etc.")
				require { option(partyEnabled) }
				booleanController()
			}
			// endregion

			// region Guild
			label(tr("nobaaddons.config.chat.chatCommands.label.guild", "Guild Command"))

			val guildEnabled = add({ chat.chatCommands.guild::enabled }) {
				name = CommonText.Config.ENABLED
				descriptionText = tr("nobaaddons.config.chat.chatCommands.guild.enabled.tooltip", "Enables chat commands in guild chat")
				booleanController()
			}
			add({ chat.chatCommands.guild::help }) {
				name = helpTitle
				descriptionText = helpDescription
				require { option(guildEnabled) }
				booleanController()
			}
			add({ chat.chatCommands.guild::warpOut }) {
				name = warpOutTitle
				descriptionText = warpOutDescription
				require { option(guildEnabled) }
				booleanController()
			}
			// endregion
		}
	}
}