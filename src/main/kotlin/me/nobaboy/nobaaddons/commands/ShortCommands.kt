package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.CommandDispatcher
import me.nobaboy.nobaaddons.commands.impl.CommandUtil
import me.nobaboy.nobaaddons.commands.impl.NobaShortClientCommand
import me.nobaboy.nobaaddons.config.NobaConfig
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess

object ShortCommands {
	private val commander by CommandUtil::commander
	private val config by NobaConfig.general::shortCommands

	init {
		ClientCommandRegistrationCallback.EVENT.register(this::register)
	}

	private fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, @Suppress("unused") registryAccess: CommandRegistryAccess) {
		if(config.registerCalculateCommands) {
			commander.register(NobaShortClientCommand("calccata", CalculateCommands::cata, CalculateCommands), dispatcher)
			commander.register(NobaShortClientCommand("calcpet", CalculateCommands::pet, CalculateCommands), dispatcher)
			commander.register(NobaShortClientCommand("calcskill", CalculateCommands::skill, CalculateCommands), dispatcher)
			commander.register(NobaShortClientCommand("calctax", CalculateCommands::tax, CalculateCommands), dispatcher)
		}
		if(config.registerInstanceCommands) {
			InstanceCommands.register(dispatcher)
		}
	}
}