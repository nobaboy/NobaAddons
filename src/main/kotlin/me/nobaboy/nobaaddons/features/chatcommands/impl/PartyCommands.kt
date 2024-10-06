package me.nobaboy.nobaaddons.features.chatcommands.impl

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.chatcommands.ChatCommandManager
import me.nobaboy.nobaaddons.features.chatcommands.impl.party.AllInviteCommand
import me.nobaboy.nobaaddons.features.chatcommands.impl.party.CancelCommand
import me.nobaboy.nobaaddons.features.chatcommands.impl.party.CoordsCommand
import me.nobaboy.nobaaddons.features.chatcommands.impl.party.TransferCommand
import me.nobaboy.nobaaddons.features.chatcommands.impl.party.WarpCommand
import me.nobaboy.nobaaddons.features.chatcommands.impl.shared.HelpCommand
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.clean
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import java.util.regex.Matcher
import java.util.regex.Pattern

object PartyCommands : ChatCommandManager() {
	private val config get() = NobaConfigManager.get().chatCommands.party
	private val chatPattern: Pattern =
		Pattern.compile("^Party > .*?(?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+).*?: [!?.](?<command>[A-z0-9_]+) ?(?<argument>[A-z0-9_]+)?")

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
			processMessage(message.string.clean(), isEnabled())
		}
	}

	private fun isEnabled() = config.enabled
}