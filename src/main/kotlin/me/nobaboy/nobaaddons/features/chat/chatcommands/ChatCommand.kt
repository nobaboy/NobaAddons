package me.nobaboy.nobaaddons.features.chat.chatcommands

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.utils.CooldownManager
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class ChatCommand(defaultCooldown: Duration = 1.seconds) : CooldownManager(defaultCooldown) {
	val config get() = NobaConfig.INSTANCE.chat.chatCommands

	abstract val enabled: Boolean
	abstract val name: String
	open val aliases: List<String> get() = emptyList()
	open val usage: String get() = name

	open val hideFromHelp: Boolean = false

	abstract fun run(ctx: ChatContext)

	open fun nameMatches(name: String): Boolean =
		this.name.equals(name, ignoreCase = true) || (aliases.isNotEmpty() && aliases.any { it.equals(name, ignoreCase = true) })
}