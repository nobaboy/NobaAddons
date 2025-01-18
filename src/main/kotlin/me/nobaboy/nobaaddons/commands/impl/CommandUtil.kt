package me.nobaboy.nobaaddons.commands.impl

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.ArgumentHandler
import dev.celestialfault.commander.Commander
import dev.celestialfault.commander.ICommand
import me.nobaboy.nobaaddons.core.Rarity
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.argument.NbtPathArgumentType
import net.minecraft.command.argument.TextArgumentType
import net.minecraft.text.Text
import kotlin.reflect.KParameter

typealias Context = CommandContext<FabricClientCommandSource>

object CommandUtil {
	private val commands: MutableList<ICommand<FabricClientCommandSource>> = mutableListOf()
	private lateinit var access: CommandRegistryAccess

	val commander = Commander<FabricClientCommandSource>()

	init {
		commander.addHandler(NbtPathArgumentType.NbtPath::class, NbtPathHandler)
		commander.addHandler(Rarity::class, RarityHandler)
		commander.addHandler(Text::class, TextHandler)
		ClientCommandRegistrationCallback.EVENT.register { dispatch, access ->
			this.access = access
			commands.forEach { commander.register(it, dispatch) }
		}
	}

	private object NbtPathHandler : ArgumentHandler<NbtPathArgumentType.NbtPath, FabricClientCommandSource> {
		override fun argument(parameter: KParameter): ArgumentType<NbtPathArgumentType.NbtPath> = NbtPathArgumentType.nbtPath()

		override fun parse(ctx: CommandContext<FabricClientCommandSource>, name: String): NbtPathArgumentType.NbtPath =
			ctx.getArgument(name, NbtPathArgumentType.NbtPath::class.java)
	}

	private object RarityHandler : ArgumentHandler<Rarity, FabricClientCommandSource> {
		override fun argument(parameter: KParameter): ArgumentType<Rarity> = Rarity.RarityArgumentType

		override fun parse(ctx: CommandContext<FabricClientCommandSource>, name: String): Rarity =
			Rarity.RarityArgumentType.getItemRarity(ctx, name)
	}

	private object TextHandler : ArgumentHandler<Text, FabricClientCommandSource> {
		override fun argument(parameter: KParameter): ArgumentType<Text> = TextArgumentType.text(access)

		override fun parse(ctx: CommandContext<FabricClientCommandSource>, name: String): Text =
			ctx.getArgument(name, Text::class.java)
	}

	fun registerRoot(command: Any) {
		commands.add(NobaClientCommandGroup(command))
	}

	fun register(command: ICommand<FabricClientCommandSource>) {
		commands.add(command)
	}
}