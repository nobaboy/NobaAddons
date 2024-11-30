package me.nobaboy.nobaaddons.commands.internal

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

object CommandUtil {
	private val commands: MutableList<ICommand> = mutableListOf()

	init {
		ClientCommandRegistrationCallback.EVENT.register { dispatch, _ ->
			commands.forEach { register(it, dispatch) }
		}
	}

	fun register(root: ICommand, dispatcher: CommandDispatcher<FabricClientCommandSource>) {
		val names = listOf(root.name, *root.aliases.toTypedArray())
		names.forEach { dispatcher.register(root.create(it)) }
	}

	fun addAll(command: LiteralArgumentBuilder<FabricClientCommandSource>, commands: List<ICommand>) {
		commands.forEach {
			val names = listOf(it.name, *it.aliases.toTypedArray())
			names.forEach { name -> command.then(it.create(name)) }
		}
	}

	fun register(command: ICommand) {
		commands.add(command)
	}
}