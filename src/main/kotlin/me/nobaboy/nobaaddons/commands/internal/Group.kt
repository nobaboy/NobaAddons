package me.nobaboy.nobaaddons.commands.internal

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType

abstract class Group(override val name: String, override val aliases: List<String> = listOf(), val executeRoot: Boolean = false): ICommand {
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
		if(executeRoot) root.executes(this::execute)
		return root
	}

	override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
		return 0
	}
}