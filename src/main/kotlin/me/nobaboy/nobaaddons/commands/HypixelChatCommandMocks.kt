package me.nobaboy.nobaaddons.commands

import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Greedy
import me.nobaboy.nobaaddons.commands.impl.CommandUtil
import me.nobaboy.nobaaddons.commands.impl.NobaClientCommand
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import me.nobaboy.nobaaddons.utils.mc.chat.ChatUtils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader

@OptIn(UntranslatedMessage::class)
internal object HypixelChatCommandMocks {
	private val commander by CommandUtil::commander

	internal fun init() {
		if(!FabricLoader.getInstance().isDevelopmentEnvironment) return
		ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
			if(!MCUtils.client.isInSingleplayer) return@register
			commander.register(NobaClientCommand(::guildChat, this), dispatcher)
			commander.register(NobaClientCommand(::partyChat, this), dispatcher)
			commander.register(NobaClientCommand(::message, this), dispatcher)
		}
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