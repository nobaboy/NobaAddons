package me.nobaboy.nobaaddons.features.chat.chatcommands.impl

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.ChatCommandManager
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.AllInviteCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.CancelCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.CoordsCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.TransferCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.party.WarpCommand
import me.nobaboy.nobaaddons.features.chat.chatcommands.impl.shared.HelpCommand
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import java.util.regex.Matcher
import java.util.regex.Pattern

object PartyCommands : ChatCommandManager() {
	private val config get() = NobaConfigManager.config.chat.chatCommands.party
	private val chatPattern =
		Pattern.compile("^Party > .*?(?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+).*?: [!?.](?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_ ]+)?")

	init {
		register(HelpCommand(this, "pc", config::help))
		register(TransferCommand())
		register(AllInviteCommand())
		register(WarpCommand())
		register(CancelCommand())
		register(CoordsCommand())
	}

	override fun matchMessage(message: String): Matcher? {
		chatPattern.matchMatcher(message) {
			return this
		}
		return null
	}

	fun init() {
		ClientReceiveMessageEvents.GAME.register { message, _ ->
			processMessage(message.string.cleanFormatting(), isEnabled())
		}
	}

	private fun isEnabled() = config.enabled
}