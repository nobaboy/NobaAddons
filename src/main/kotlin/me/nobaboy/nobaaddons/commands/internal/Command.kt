package me.nobaboy.nobaaddons.commands.internal

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.NobaAddons
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

typealias CommandBuilder = Command.(LiteralArgumentBuilder<FabricClientCommandSource>) -> LiteralArgumentBuilder<FabricClientCommandSource>

class Command(
	override val name: String,
	override val aliases: List<String> = listOf(),
	override val enabled: Boolean = true,
	private val callback: CommandContext<FabricClientCommandSource>.() -> Unit,
	private val commandBuilder: CommandBuilder,
) : ICommand {
	override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
		runCatching {
			callback(ctx)
		}.onFailure {
			NobaAddons.LOGGER.error("Failed to execute command", it)
		}
		return 0
	}

	override fun create(name: String): LiteralArgumentBuilder<FabricClientCommandSource> {
		return commandBuilder(this, ClientCommandManager.literal(name))
	}

	class Builder(private val name: String, private val aliases: List<String>) {
		private lateinit var executes: CommandContext<FabricClientCommandSource>.() -> Unit
		var enabled = true
		private var builder: CommandBuilder = {
			it.executes(this::execute)
		}

		fun buildCommand(builder: CommandBuilder) {
			this.builder = builder
		}

		fun executes(callback: CommandContext<FabricClientCommandSource>.() -> Unit) {
			this.executes = callback
		}

		fun build(): Command {
			return Command(
				name = name,
				aliases = aliases,
				enabled = enabled,
				callback = executes,
				commandBuilder = builder,
			)
		}
	}

	companion object {
		inline fun command(name: String, aliases: List<String> = listOf(), builder: Builder.() -> Unit): Command {
			return Builder(name, aliases).also(builder).build()
		}
	}
}