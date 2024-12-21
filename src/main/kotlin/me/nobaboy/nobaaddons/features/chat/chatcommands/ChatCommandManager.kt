package me.nobaboy.nobaaddons.features.chat.chatcommands

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import java.util.regex.Matcher
import java.util.regex.Pattern

abstract class ChatCommandManager : CooldownManager() {
	private val commands = mutableListOf<IChatCommand>()
	private val lock = Object()

	protected abstract val enabled: Boolean
	protected abstract val pattern: Pattern

	protected fun register(command: IChatCommand) {
		commands.add(command)
	}

	fun getCommands(enabledOnly: Boolean = false): List<IChatCommand> =
		if(enabledOnly) commands.filter { it.enabled } else commands

	protected open fun matchMessage(message: String): Matcher? =
		pattern.matcher(message).takeIf { it.matches() }

	private fun getContext(message: String): ChatContext? {
		val match = matchMessage(message) ?: return null
		val user = match.group("username")
		val command = match.group("command")
		val args = match.group("argument")?.split(" ") ?: emptyList()
		return ChatContext(user, command, args, message)
	}

	fun processMessage(message: String) {
		if(!enabled) return

		synchronized(lock) {
			val ctx = getContext(message) ?: return
			val cmd = commands.asSequence()
				.filter { it.enabled }
				.firstOrNull {
					it.name.lowercaseEquals(ctx.command) ||
						it.aliases.any { alias ->
							alias.lowercaseEquals(ctx.command)
						}
				} ?: return

			if(!cmd.bypassCooldown && isOnCooldown()) return

			runCatching {
				cmd.run(ctx)
			}.onSuccess {
				if(!cmd.bypassCooldown) startCooldown()
			}.onFailure { ex ->
				NobaAddons.LOGGER.error("Failed to run chat command '${cmd.name}' | Command: '$cmd'.", ex)
			}
		}
	}
}