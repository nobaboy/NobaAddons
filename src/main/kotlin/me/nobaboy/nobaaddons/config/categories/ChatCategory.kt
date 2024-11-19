package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.features.chat.filters.ChatFilterOption
import net.minecraft.text.Text

object ChatCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.chat"))

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.chat.alerts"))
				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.label.crimsonIsle"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.alerts.mythicSeaCreatureSpawn"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.alerts.mythicSeaCreatureSpawn.tooltip")))
					.binding(defaults.chat.alerts.mythicSeaCreatureSpawn, config.chat.alerts::mythicSeaCreatureSpawn) { config.chat.alerts.mythicSeaCreatureSpawn = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.alerts.vanquisherSpawn"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.alerts.vanquisherSpawn.tooltip")))
					.binding(defaults.chat.alerts.vanquisherSpawn, config.chat.alerts::vanquisherSpawn) { config.chat.alerts.vanquisherSpawn = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.chat.filters"))
				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.label.itemAbilities"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.hideAbilityCooldownMessage", "Implosion"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filters.hideAbilityCooldownMessage.tooltip")))
					.binding(defaults.chat.filters.hideAbilityCooldownMessage, config.chat.filters::hideAbilityCooldownMessage) { config.chat.filters.hideAbilityCooldownMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Implosion"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Wither Blade")))
					.binding(defaults.chat.filters.hideImplosionDamageMessage, config.chat.filters::hideImplosionDamageMessage) { config.chat.filters.hideImplosionDamageMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Molten Wave"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Midas Staff")))
					.binding(defaults.chat.filters.hideMoltenWaveDamageMessage, config.chat.filters::hideMoltenWaveDamageMessage) { config.chat.filters.hideMoltenWaveDamageMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Guided Bat"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Spirit Sceptre")))
					.binding(defaults.chat.filters.hideSpiritSceptreDamageMessage, config.chat.filters::hideSpiritSceptreDamageMessage) { config.chat.filters.hideSpiritSceptreDamageMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Giant's Slam"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Giant's Sword")))
					.binding(defaults.chat.filters.hideGiantSwordDamageMessage, config.chat.filters::hideGiantSwordDamageMessage) { config.chat.filters.hideGiantSwordDamageMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Throw"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Livid Dagger")))
					.binding(defaults.chat.filters.hideLividDaggerDamageMessage, config.chat.filters::hideLividDaggerDamageMessage) { config.chat.filters.hideLividDaggerDamageMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage", "Ray of Hope"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filters.hideAbilityDamageMessage.tooltip", "Staff of the Rising Sun")))
					.binding(defaults.chat.filters.hideRayOfHopeDamageMessage, config.chat.filters::hideRayOfHopeDamageMessage) { config.chat.filters.hideRayOfHopeDamageMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.label.dungeons"))
					.build())

				.option(Option.createBuilder<ChatFilterOption>()
					.name(Text.translatable("nobaaddons.config.chat.filters.blessingMessage"))
					.binding(defaults.chat.filters.blessingMessage, config.chat.filters::blessingMessage) { config.chat.filters.blessingMessage = it }
					.controller(NobaConfigUtils::createCyclingController)
					.build())

				.option(Option.createBuilder<ChatFilterOption>()
					.name(Text.translatable("nobaaddons.config.chat.filters.healerOrbMessage"))
					.binding(defaults.chat.filters.healerOrbMessage, config.chat.filters::healerOrbMessage) { config.chat.filters.healerOrbMessage = it }
					.controller(NobaConfigUtils::createCyclingController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.pickupObtainMessage"))
					.binding(defaults.chat.filters.pickupObtainMessage, config.chat.filters::pickupObtainMessage) { config.chat.filters.pickupObtainMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.allowKeyMessage"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filters.allowKeyMessage.tooltip")))
					.binding(defaults.chat.filters.allowKeyMessage, config.chat.filters::allowKeyMessage) { config.chat.filters.allowKeyMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.allow5050ItemMessage"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filters.allow5050ItemMessage.tooltip")))
					.binding(defaults.chat.filters.allow5050ItemMessage, config.chat.filters::allow5050ItemMessage) { config.chat.filters.allow5050ItemMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.label.miscellaneous"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.hideTipMessages"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filters.hideTipMessages.tooltip")))
					.binding(defaults.chat.filters.hideTipMessages, config.chat.filters::hideTipMessages) { config.chat.filters.hideTipMessages = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filters.hideProfileInfo"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filters.hideProfileInfo.tooltip")))
					.binding(defaults.chat.filters.hideProfileInfo, config.chat.filters::hideProfileInfo) { config.chat.filters.hideProfileInfo = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.chat.chatCommands"))
				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.chat.chatCommands.label.dm"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.enabled"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.dm.enabled.tooltip")))
					.binding(defaults.chat.chatCommands.dm.enabled, config.chat.chatCommands.dm::enabled) { config.chat.chatCommands.dm.enabled = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.chatCommands.help"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.help.tooltip")))
					.binding(defaults.chat.chatCommands.dm.help, config.chat.chatCommands.dm::help) { config.chat.chatCommands.dm.help = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.chatCommands.dm.warpMe"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.dm.warpMe.tooltip")))
					.binding(defaults.chat.chatCommands.dm.warpMe, config.chat.chatCommands.dm::warpMe) { config.chat.chatCommands.dm.warpMe = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.chatCommands.dm.partyMe"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.dm.partyMe.tooltip")))
					.binding(defaults.chat.chatCommands.dm.partyMe, config.chat.chatCommands.dm::partyMe) { config.chat.chatCommands.dm.partyMe = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.chatCommands.warpOut"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.warpOut.tooltip")))
					.binding(defaults.chat.chatCommands.dm.warpOut, config.chat.chatCommands.dm::warpOut) { config.chat.chatCommands.dm.warpOut = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.chat.chatCommands.label.party"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.enabled"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.party.enabled.tooltip")))
					.binding(defaults.chat.chatCommands.party.enabled, config.chat.chatCommands.party::enabled) { config.chat.chatCommands.party.enabled = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.chatCommands.help"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.help.tooltip")))
					.binding(defaults.chat.chatCommands.party.help, config.chat.chatCommands.party::help) { config.chat.chatCommands.party.help = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.chatCommands.party.allInvite"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.party.allInvite.tooltip")))
					.binding(defaults.chat.chatCommands.party.allInvite, config.chat.chatCommands.party::allInvite) { config.chat.chatCommands.party.allInvite = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.chatCommands.party.transfer"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.party.transfer.tooltip")))
					.binding(defaults.chat.chatCommands.party.transfer, config.chat.chatCommands.party::transfer) { config.chat.chatCommands.party.transfer = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.chatCommands.party.warp"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.party.warp.tooltip")))
					.binding(defaults.chat.chatCommands.party.warp, config.chat.chatCommands.party::warp) { config.chat.chatCommands.party.warp = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.chatCommands.party.coords"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.party.coords.tooltip")))
					.binding(defaults.chat.chatCommands.party.coords, config.chat.chatCommands.party::coords) { config.chat.chatCommands.party.coords = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.chat.chatCommands.label.guild"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.enabled"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.guild.enabled.tooltip")))
					.binding(defaults.chat.chatCommands.guild.enabled, config.chat.chatCommands.guild::enabled) { config.chat.chatCommands.guild.enabled = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.chatCommands.help"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.help.tooltip")))
					.binding(defaults.chat.chatCommands.guild.help, config.chat.chatCommands.guild::help) { config.chat.chatCommands.guild.help = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.chatCommands.warpOut"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.chatCommands.warpOut.tooltip")))
					.binding(defaults.chat.chatCommands.guild.warpOut, config.chat.chatCommands.guild::warpOut) { config.chat.chatCommands.guild.warpOut = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}