package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.arguments.StringArgumentType
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.CommandBuilder
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager

@Suppress("unused")
object InternalCommands : Group("internal") {
	private val requiresId: CommandBuilder = { it.then(ClientCommandManager.argument("id", StringArgumentType.string()).executes(this::execute)) }

	val action = Command("action", commandBuilder = requiresId) {
		ChatUtils.processClickAction(StringArgumentType.getString(it, "id"))
	}

	val copyError = Command("copyerror", commandBuilder = requiresId) {
		ErrorManager.copyError(StringArgumentType.getString(it, "id"))
	}
}