package me.nobaboy.nobaaddons.commands.impl

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.ArgumentHandler
import dev.celestialfault.commander.Commander
import dev.celestialfault.commander.ICommand
import dev.celestialfault.commander.annotations.ExperimentalCommanderApi
import dev.celestialfault.commander.types.brigadier.EnumArgumentTypeImpl
import me.nobaboy.nobaaddons.commands.adapters.*
import me.nobaboy.nobaaddons.core.DebugFlag
import me.nobaboy.nobaaddons.core.Skill
import me.nobaboy.nobaaddons.ui.ElementAlignment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.NbtPathArgumentType
import net.minecraft.command.argument.TextArgumentType

typealias Context = CommandContext<FabricClientCommandSource>

object CommandUtil {
	private val commands: MutableList<ICommand<FabricClientCommandSource>> = mutableListOf()

	@OptIn(ExperimentalCommanderApi::class)
	val commander = Commander<FabricClientCommandSource>().apply {
		addHandler(FormattingHandler)
		addHandler(NbtPathArgumentType.nbtPath())
		addHandler(RarityHandler)
		addHandler(EnumArgumentTypeImpl(ElementAlignment::class.java))
		addHandler(EnumArgumentTypeImpl(Skill::class.java))
		addHandler(EnumArgumentTypeImpl(DebugFlag::class.java))
	}

	init {
		ClientCommandRegistrationCallback.EVENT.register { dispatch, access ->
			commander.addHandler(TextArgumentType.text(access))
			commands.forEach { commander.register(it, dispatch) }
		}
	}

	fun registerRoot(command: Any) {
		commands.add(NobaClientCommandGroup(command))
	}

	fun register(command: ICommand<FabricClientCommandSource>) {
		commands.add(command)
	}

	inline fun <reified T : Any, S : CommandSource> Commander<S>.addHandler(type: ArgumentType<T>) {
		addHandler(ArgumentHandler.of(type))
	}

	inline fun <reified T> CommandContext<*>.getArgument(name: String): T = getArgument(name, T::class.java)
}