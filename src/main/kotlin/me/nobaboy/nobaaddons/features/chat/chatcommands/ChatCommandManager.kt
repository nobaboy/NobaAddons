package me.nobaboy.nobaaddons.features.chat.chatcommands

import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.api.HypixelAPI
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.MCUtils
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.MinecraftClient
import java.util.Collections
import kotlin.time.Duration.Companion.seconds

abstract class ChatCommandManager {
	private val commands = mutableListOf<ChatCommand>()
	private val lock = Mutex()

	fun commands(): List<ChatCommand> = Collections.unmodifiableList(commands)

	protected fun onHypixel(): Boolean {
		return HypixelAPI.onHypixel || (FabricLoader.getInstance().isDevelopmentEnvironment && MinecraftClient.getInstance().isInSingleplayer)
	}

	protected abstract val source: ChatContext.ChatCommandSource
	protected abstract val enabled: Boolean
	protected abstract val pattern: Regex

	init {
		ChatMessageEvents.CHAT.register(this::onChatMessage)
	}

	protected open fun onChatMessage(event: ChatMessageEvents.Chat) {
		processMessage(event.cleaned)
	}

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

	fun processMessage(message: String) = NobaAddons.runAsync { processMessageInternal(message) }

	private suspend fun processMessageInternal(message: String) {
		if(!enabled) return

		val ctx = getContext(message) ?: return
		val cmd = commands
			.asSequence()
			.filter { it.enabled }
			.firstOrNull { it.nameMatches(ctx.command) }
			?: return

		lock.withLock(null) { executeCommand(ctx, cmd) }
	}

	protected open suspend fun executeCommand(ctx: ChatContext, cmd: ChatCommand) {
		if(cmd.isOnCooldown()) return
		if(ctx.user == MCUtils.playerName) {
			// wait a short bit to avoid sending commands too fast
			delay(0.2.seconds)
		}

		try {
			cmd.run(ctx)
		} catch(ex: Throwable) {
			ErrorManager.logError("Chat command '${ctx.command}' threw an error", ex, "Command" to ctx.fullMessage)
		}
	}
}