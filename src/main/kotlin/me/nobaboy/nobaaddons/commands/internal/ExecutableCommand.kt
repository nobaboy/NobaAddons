package me.nobaboy.nobaaddons.commands.internal

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import me.nobaboy.nobaaddons.NobaAddons
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

typealias CommandBuilder = ExecutableCommand.(LiteralArgumentBuilder<FabricClientCommandSource>) -> LiteralArgumentBuilder<FabricClientCommandSource>
private val DEFAULT_BUILDER: CommandBuilder = { it.executes(this::execute) }

class ExecutableCommand(
	override val name: String,
	override val aliases: List<String> = listOf(),
	override val enabled: Boolean = true,
	private val builder: CommandBuilder = DEFAULT_BUILDER,
	private val callback: (Context) -> Unit,
) : ICommand {
	override fun execute(ctx: Context): Int {
		ICommand.executeCatching(name, ctx, callback)
		return 0
	}

	override fun create(name: String): LiteralArgumentBuilder<FabricClientCommandSource> {
		return builder(this, ClientCommandManager.literal(name))
	}

	companion object {
		/**
		 * Utility method, creates a [ExecutableCommand] wrapping the provided [command] method with [NobaAddons.runAsync]
		 */
		fun async(
			name: String,
			aliases: List<String> = emptyList(),
			enabled: Boolean = true,
			commandBuilder: CommandBuilder = DEFAULT_BUILDER,
			command: suspend (Context) -> Unit
		) = ExecutableCommand(name, aliases, enabled, commandBuilder) {
			NobaAddons.runAsync {
				try {
					command(it)
				} catch(e: Throwable) {
					ICommand.handleCaught(name, e)
				}
			}
		}
	}
}