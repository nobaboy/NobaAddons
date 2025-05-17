package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.CommandDispatcher
import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Greedy
import me.nobaboy.nobaaddons.commands.impl.CommandUtil
import me.nobaboy.nobaaddons.commands.impl.NobaClientCommand
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.command.CommandRegistryAccess

@OptIn(UntranslatedMessage::class)
internal object HypixelChatCommandMocks {
	private val commander by CommandUtil::commander

	init {
		if(FabricLoader.getInstance().isDevelopmentEnvironment) {
			ClientCommandRegistrationCallback.EVENT.register(this::register)
		}
	}

	private fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, @Suppress("unused") registryAccess: CommandRegistryAccess) {
		if(!MCUtils.client.isInSingleplayer) return
		commander.register(NobaClientCommand(::guildChat, this), dispatcher)
		commander.register(NobaClientCommand(::partyChat, this), dispatcher)
		commander.register(NobaClientCommand(::message, this), dispatcher)
	}

	@Command("gc")
	fun guildChat(message: @Greedy String) {
		ChatUtils.addMessage("Guild > ${MCUtils.playerName}: $message", prefix = false)
	}

	@Command("pc")
	fun partyChat(message: @Greedy String) {
		ChatUtils.addMessage("Party > ${MCUtils.playerName}: $message", prefix = false)
	}

	@Command("msg")
	fun message(user: String, message: @Greedy String) {
		ChatUtils.addMessage("To $user: $message", prefix = false)
	}
}