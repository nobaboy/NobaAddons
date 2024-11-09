package me.nobaboy.nobaaddons.commands.internal

import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

object CommandUtil {
	private val commands: MutableList<ICommand> = mutableListOf()

	init {
		ClientCommandRegistrationCallback.EVENT.register { dispatch, _ ->
			commands.forEach { command ->
				val node = dispatch.register(command.create())
				command.aliases.forEach { alias ->
					val aliased = ClientCommandManager.literal(alias).redirect(node)
					if(command !is Group || command.executeRoot) {
						aliased.executes(command::execute)
					}
					dispatch.register(aliased)
				}
			}
		}
	}

	fun register(command: ICommand) {
		commands.add(command)
	}
}