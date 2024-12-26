package me.nobaboy.nobaaddons.features.chat.chatcommands

import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals

abstract class ChatCommandManager : CooldownManager() {
	private val commands = mutableListOf<IChatCommand>()
	private val lock = Object()

	protected abstract val enabled: Boolean
	protected abstract val pattern: Regex

	protected fun register(command: IChatCommand) {
		commands.add(command)
	}

	fun getCommands(enabledOnly: Boolean = false): List<IChatCommand> =
		if(enabledOnly) commands.filter { it.enabled } else commands

	protected open fun matchMessage(message: String): MatchResult? =
		pattern.matchEntire(message)

	private fun getContext(message: String): ChatContext? {
		val match = matchMessage(message) ?: return null
		val user = match.groups["username"]?.value!!
		val command = match.groups["command"]?.value!!
		val args = match.groups["argument"]?.value?.split(" ") ?: emptyList()
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
				ErrorManager.logError("Chat command '${cmd.name}' threw an error", ex, "Command" to ctx.fullMessage)
			}
		}
	}
}