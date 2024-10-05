package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import net.minecraft.text.Text

object ChatCommandsCategory {
    fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
        return ConfigCategory.createBuilder()
            .name(Text.translatable("config.chatCommands"))

            // DM Commands
            .group(OptionGroup.createBuilder()
                .name(Text.translatable("config.chatCommands.dm"))
                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.dm.enabled"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.dm.enabled.tooltip")))
                    .binding(defaults.chatCommands.dm.enabled, defaults.chatCommands.dm::enabled ) { config.chatCommands.dm.enabled = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.dm.help"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.dm.help.tooltip")))
                    .binding(defaults.chatCommands.dm.help, defaults.chatCommands.dm::help ) { config.chatCommands.dm.help = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.dm.warpMe"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.dm.warpMe.tooltip")))
                    .binding(defaults.chatCommands.dm.warpMe, defaults.chatCommands.dm::warpMe ) { config.chatCommands.dm.warpMe = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.dm.partyMe"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.dm.partyMe.tooltip")))
                    .binding(defaults.chatCommands.dm.partyMe, defaults.chatCommands.dm::partyMe ) { config.chatCommands.dm.partyMe = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.dm.warpOut"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.dm.warpOut.tooltip")))
                    .binding(defaults.chatCommands.dm.warpOut, defaults.chatCommands.dm::warpOut ) { config.chatCommands.dm.warpOut = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .build()
            )

            // Party Commands
            .group(OptionGroup.createBuilder()
                .name(Text.translatable("config.chatCommands.party"))
                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.party.enabled"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.party.enabled.tooltip")))
                    .binding(defaults.chatCommands.party.enabled, defaults.chatCommands.party::enabled ) { config.chatCommands.party.enabled = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.party.help"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.party.help.tooltip")))
                    .binding(defaults.chatCommands.party.help, defaults.chatCommands.party::help ) { config.chatCommands.party.help = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.party.allInvite"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.party.allInvite.tooltip")))
                    .binding(defaults.chatCommands.party.allInvite, defaults.chatCommands.party::allInvite ) { config.chatCommands.party.allInvite = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.party.transfer"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.party.transfer.tooltip")))
                    .binding(defaults.chatCommands.party.transfer, defaults.chatCommands.party::transfer ) { config.chatCommands.party.transfer = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.party.warp"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.party.warp.tooltip")))
                    .binding(defaults.chatCommands.party.warp, defaults.chatCommands.party::warp ) { config.chatCommands.party.warp = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.party.coords"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.party.coords.tooltip")))
                    .binding(defaults.chatCommands.party.coords, defaults.chatCommands.party::coords ) { config.chatCommands.party.coords = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .build()
            )

            // Guild Commands
            .group(OptionGroup.createBuilder()
                .name(Text.translatable("config.chatCommands.guild"))
                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.guild.enabled"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.guild.enabled.tooltip")))
                    .binding(defaults.chatCommands.guild.enabled, defaults.chatCommands.guild::enabled ) { config.chatCommands.guild.enabled = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.guild.help"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.guild.help.tooltip")))
                    .binding(defaults.chatCommands.guild.help, defaults.chatCommands.guild::help ) { config.chatCommands.guild.help = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .option(Option.createBuilder<Boolean>()
                    .name(Text.translatable("config.chatCommands.guild.warpOut"))
                    .description(OptionDescription.of(Text.translatable("config.chatCommands.guild.warpOut.tooltip")))
                    .binding(defaults.chatCommands.guild.warpOut, defaults.chatCommands.guild::warpOut ) { config.chatCommands.guild.warpOut = it }
                    .controller(NobaConfigUtils::createBooleanController)
                    .build())

                .build()
            )

            .build()
    }
}