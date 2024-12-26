package me.nobaboy.nobaaddons.features.chat.chatcommands.impl

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommandManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.AllInviteCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.CancelCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.CoordsCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.TransferCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.WarpCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.HelpCommand
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

object PartyCommands : ChatCommandManager() {
	private val config get() = NobaConfigManager.config.chat.chatCommands.party

	override val enabled: Boolean get() = config.enabled
	override val pattern by Regex("^Party > (?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+): [!?.](?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_ ]+)?").fromRepo("chat_commands.party")

	init {
		register(HelpCommand(this, "pc", config::help))
		register(TransferCommand())
		register(AllInviteCommand())
		register(WarpCommand())
		register(CancelCommand())
		register(CoordsCommand())
	}

	fun init() {
		ClientReceiveMessageEvents.GAME.register { message, _ ->
			processMessage(message.string.cleanFormatting())
		}
	}
}