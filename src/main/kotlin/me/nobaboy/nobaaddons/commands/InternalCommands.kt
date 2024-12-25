package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.arguments.StringArgumentType
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager

@Suppress("unused")
object InternalCommands : Group("internal") {
	val action = Command(
		"action",
		commandBuilder = { it.then(ClientCommandManager.argument("id", StringArgumentType.string()).executes(this::execute)) }
	) { ChatUtils.processClickAction(StringArgumentType.getString(it, "id")) }

	val copyError = Command(
		"copyerror",
		commandBuilder = { it.then(ClientCommandManager.argument("id", StringArgumentType.string()).executes(this::execute)) }
	) { ErrorManager.copyError(StringArgumentType.getString(it, "id")) }
}