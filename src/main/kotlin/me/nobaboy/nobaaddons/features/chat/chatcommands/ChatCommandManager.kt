package me.nobaboy.nobaaddons.features.chat.chatcommands

import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.HypixelUtils
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import java.util.Collections

abstract class ChatCommandManager {
	private val commands = mutableListOf<ChatCommand>()
	private val lock = Object()

	fun commands(): List<ChatCommand> = Collections.unmodifiableList(commands)

	protected fun onHypixel(): Boolean {
		return HypixelUtils.onHypixel || (FabricLoader.getInstance().isDevelopmentEnvironment && MinecraftClient.getInstance().isInSingleplayer)
	}

	protected abstract val source: ChatContext.ChatCommandSource
	protected abstract val enabled: Boolean
	protected abstract val pattern: Regex

	protected fun register(command: ChatCommand) {
		commands.add(command)
	}

	protected open fun matchMessage(message: String): MatchResult? =
		pattern.matchEntire(message)

	private fun getContext(message: String): ChatContext? {
		val match = matchMessage(message) ?: return null
		val user = match.groups["username"]?.value!!
		val command = match.groups["command"]?.value!!
		val args = match.groups["argument"]?.value?.split(" ") ?: emptyList()
		return ChatContext(source, user, command, args, message)
	}

	fun processMessage(message: String) {
		if(!enabled) return

		synchronized(lock) {
			val ctx = getContext(message) ?: return
			val cmd = commands.asSequence()
				.filter { it.enabled }
				.firstOrNull {
					it.name.equals(ctx.command, ignoreCase = true)
						|| it.aliases.any { alias -> alias.equals(ctx.command, ignoreCase = true) }
				} ?: return

			if(cmd.isOnCooldown()) return

			try {
				cmd.run(ctx)
			} catch(ex: Throwable) {
				ErrorManager.logError("Chat command '${ctx.command}' threw an error", ex, "Command" to ctx.fullMessage)
			}
		}
	}
}