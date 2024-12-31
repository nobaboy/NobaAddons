package me.nobaboy.nobaaddons.commands.internal

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.CommandSyntaxException
import me.nobaboy.nobaaddons.utils.ErrorManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

typealias Context = CommandContext<FabricClientCommandSource>

interface ICommand {
	val name: String
	val aliases: List<String>
	val enabled: Boolean
	fun create(name: String): LiteralArgumentBuilder<FabricClientCommandSource>
	fun execute(ctx: Context): Int

	companion object {
		@Suppress("SameReturnValue")
		fun executeCatching(name: String, context: Context, command: (Context) -> Unit): Int {
			try {
				command(context)
				return 0
			} catch(e: Throwable) {
				handleCaught(name, e)
				return 0
			}
		}

		fun handleCaught(name: String, error: Throwable) {
			when(error) {
				// rethrow syntax errors, as the game will send a better message for these
				is CommandSyntaxException -> throw error
				else -> ErrorManager.logError("Command '$name' threw an unhandled exception", error, ignorePreviousErrors = true)
			}
		}
	}
}