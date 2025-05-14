package me.nobaboy.nobaaddons.commands.impl

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.ArgumentHandler
import dev.celestialfault.commander.Commander
import dev.celestialfault.commander.ICommand
import me.nobaboy.nobaaddons.commands.adapters.*
import me.nobaboy.nobaaddons.core.DebugFlag
import me.nobaboy.nobaaddons.core.Skill
import me.nobaboy.nobaaddons.ui.ElementAlignment
import me.nobaboy.nobaaddons.utils.JavaUtils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.NbtPathArgumentType
import net.minecraft.command.argument.TextArgumentType
import kotlin.reflect.KParameter

typealias Context = CommandContext<FabricClientCommandSource>

object CommandUtil {
	private val commands: MutableList<ICommand<FabricClientCommandSource>> = mutableListOf()

	val commander = Commander<FabricClientCommandSource>().apply {
		addHandler(FormattingHandler)
		addHandler(NbtPathArgumentType.nbtPath())
		addHandler(RarityHandler)
		addHandler(JavaUtils.enumArgument(ElementAlignment::class.java))
		addHandler(JavaUtils.enumArgument(Skill::class.java))
		addHandler(JavaUtils.enumArgument(DebugFlag::class.java))
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

	private inline fun <reified T : Any, S : CommandSource> Commander<S>.addHandler(handler: ArgumentHandler<T, S>) {
		addHandler(T::class, handler)
	}

	private inline fun <reified T : Any, S : CommandSource> Commander<S>.addHandler(handler: ArgumentType<T>) {
		addHandler(object : ArgumentHandler<T, S> {
			override fun argument(parameter: KParameter): ArgumentType<T> = handler
			override fun parse(ctx: CommandContext<S>, name: String): T = ctx.getArgument(name)
		})
	}

	inline fun <reified T> CommandContext<*>.getArgument(name: String): T = getArgument(name, T::class.java)
}