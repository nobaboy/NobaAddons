package me.nobaboy.nobaaddons.commands.impl

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.ArgumentHandler
import dev.celestialfault.commander.Commander
import dev.celestialfault.commander.ICommand
import me.nobaboy.nobaaddons.core.Rarity
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.NbtPathArgumentType
import kotlin.reflect.KParameter

typealias Context = CommandContext<FabricClientCommandSource>

object CommandUtil {
	private val commands: MutableList<ICommand<FabricClientCommandSource>> = mutableListOf()

	init {
		Commander.register(NbtPathArgumentType.NbtPath::class, NbtPathHandler)
		Commander.register(Rarity::class, RarityHandler)
		ClientCommandRegistrationCallback.EVENT.register { dispatch, _ ->
			commands.forEach { Commander.register(it, dispatch) }
		}
	}

	private object NbtPathHandler : ArgumentHandler<NbtPathArgumentType.NbtPath, CommandSource> {
		override fun argument(parameter: KParameter): ArgumentType<NbtPathArgumentType.NbtPath> {
			return NbtPathArgumentType.nbtPath()
		}

		override fun parse(ctx: CommandContext<CommandSource>, name: String): NbtPathArgumentType.NbtPath {
			return ctx.getArgument(name, NbtPathArgumentType.NbtPath::class.java)
		}
	}

	private object RarityHandler : ArgumentHandler<Rarity, CommandSource> {
		override fun argument(parameter: KParameter): ArgumentType<Rarity> = Rarity.RarityArgumentType

		override fun parse(ctx: CommandContext<CommandSource>, name: String): Rarity =
			Rarity.RarityArgumentType.getItemRarity(ctx, name)
	}

	fun registerRoot(command: Any) {
		commands.add(NobaClientCommandGroup(command))
	}

	fun register(command: ICommand<FabricClientCommandSource>) {
		commands.add(command)
	}
}