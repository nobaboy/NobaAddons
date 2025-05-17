package me.nobaboy.nobaaddons.features.chat.chatcommands.impl

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommandManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatContext
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.AbstractPartyChatCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.AllInviteCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.CancelCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.CoordsCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.JoinInstanceCommands
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.PingCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.TpsCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.TransferCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.WarpCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.HelpCommand
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CommonPatterns

object PartyCommands : ChatCommandManager() {
	private val config get() = NobaConfig.chat.chatCommands.party
	override val enabled: Boolean get() = config.enabled && onHypixel()

	override val source: ChatContext.ChatCommandSource = ChatContext.ChatCommandSource.PARTY
	override val pattern by Regex("^Party > ${CommonPatterns.PLAYER_NAME_WITH_RANK_STRING}: [!?.](?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_ ]+)?").fromRepo("chat_commands.party")

	init {
		register(HelpCommand(this, config::help))
		register(AllInviteCommand())
		register(CoordsCommand())
		register(JoinInstanceCommands())
		register(PingCommand())
		register(TpsCommand())
		register(TransferCommand())
		register(WarpCommand())
		register(CancelCommand())
	}

	private fun validatePermission(ctx: ChatContext, cmd: ChatCommand): Boolean {
		if(cmd !is AbstractPartyChatCommand) return true

		val party = PartyAPI.party ?: return true
		val executor = party.members.firstOrNull { it.name == ctx.user }
		val clientPlayer = party.members.firstOrNull { it.isMe }

		// hypixel why the fuck is this party role enum class ordered inversely to how you'd expect it to be when
		// doing comparisons on it
		// why do i have to check if the role is *greater* than another to know if someone is *below* that role
		return when {
			executor != null && executor.role > cmd.requireExecutorIs -> false
			clientPlayer != null && clientPlayer.role > cmd.requireClientPlayerIs -> false
			else -> true
		}
	}

	override suspend fun executeCommand(ctx: ChatContext, cmd: ChatCommand) {
		if(!validatePermission(ctx, cmd)) return
		super.executeCommand(ctx, cmd)
	}
}