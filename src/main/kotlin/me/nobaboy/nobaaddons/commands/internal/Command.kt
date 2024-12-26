package me.nobaboy.nobaaddons.commands.internal

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.ErrorManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

typealias CommandBuilder = Command.(LiteralArgumentBuilder<FabricClientCommandSource>) -> LiteralArgumentBuilder<FabricClientCommandSource>
private val DEFAULT_BUILDER: CommandBuilder = { it.executes(this::execute) }

class Command(
	override val name: String,
	override val aliases: List<String> = listOf(),
	override val enabled: Boolean = true,
	private val commandBuilder: CommandBuilder = DEFAULT_BUILDER,
	private val callback: (CommandContext<FabricClientCommandSource>) -> Unit,
) : ICommand {
	override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
		try {
			callback(ctx)
		} catch(e: Throwable) {
			ErrorManager.logError("Command '$name' threw an unhandled exception", e, ignorePreviousErrors = true)
		}
		return 0
	}

	override fun create(name: String): LiteralArgumentBuilder<FabricClientCommandSource> {
		return commandBuilder(this, ClientCommandManager.literal(name))
	}

	companion object {
		/**
		 * Utility method, creates a [Command] wrapping the provided [command] method with [NobaAddons.runAsync]
		 */
		fun async(
			name: String,
			aliases: List<String> = emptyList(),
			enabled: Boolean = true,
			commandBuilder: CommandBuilder = DEFAULT_BUILDER,
			command: suspend (CommandContext<FabricClientCommandSource>) -> Unit
		) = Command(name, aliases, enabled, commandBuilder) {
			NobaAddons.runAsync {
				try {
					command(it)
				} catch(e: Throwable) {
					ErrorManager.logError("Command '$name' threw an unhandled exception", e, ignorePreviousErrors = true)
				}
			}
		}
	}
}