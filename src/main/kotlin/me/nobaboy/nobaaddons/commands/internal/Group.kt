package me.nobaboy.nobaaddons.commands.internal

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.utils.ErrorManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

abstract class Group(
	override val name: String,
	override val enabled: Boolean = true,
	override val aliases: List<String> = listOf(),
) : ICommand {
	private val commands: List<ICommand> by lazy {
		buildList<ICommand> {
			addAll(this@Group::class.memberProperties
				.asSequence()
				.filter { it.returnType.isSubtypeOf(ICommand::class.starProjectedType) }
				.map { it.getter.call(this@Group) as ICommand })
			addAll(this@Group::class.nestedClasses
				.asSequence()
				.filter { it.isSubclassOf(Group::class) }
				.filter { it.objectInstance != null }
				.map { it.objectInstance!! as Group })
		}
	}

	override fun create(name: String): LiteralArgumentBuilder<FabricClientCommandSource> {
		val root = ClientCommandManager.literal(name)
		CommandUtil.addAll(root, commands)
		this.root?.let { command ->
			root.executes { ctx ->
				try {
					command.invoke(ctx)
				} catch(e: Throwable) {
					ErrorManager.logError("Command '$name' threw an unhandled exception", e, ignorePreviousErrors = true)
				}
				0
			}
		}
		return root
	}

	open val root: RootCommand? = null
	final override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int = throw UnsupportedOperationException()

	fun interface RootCommand {
		fun invoke(ctx: CommandContext<FabricClientCommandSource>)
	}
}