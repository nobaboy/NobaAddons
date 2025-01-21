package me.nobaboy.nobaaddons.commands.adapters

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.ArgumentHandler
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.argument.NbtPathArgumentType
import kotlin.reflect.KParameter

object NbtPathHandler : ArgumentHandler<NbtPathArgumentType.NbtPath, FabricClientCommandSource> {
	override fun argument(parameter: KParameter): ArgumentType<NbtPathArgumentType.NbtPath> {
		return NbtPathArgumentType.nbtPath()
	}

	override fun parse(ctx: CommandContext<FabricClientCommandSource>, name: String): NbtPathArgumentType.NbtPath {
		return ctx.getArgument(name, NbtPathArgumentType.NbtPath::class.java)
	}
}