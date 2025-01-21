package me.nobaboy.nobaaddons.commands.adapters

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.ArgumentHandler
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.command.argument.TextArgumentType
import net.minecraft.text.Text
import kotlin.reflect.KParameter

class TextHandler(private val access: CommandRegistryAccess) : ArgumentHandler<Text, FabricClientCommandSource> {
	override fun argument(parameter: KParameter): ArgumentType<Text> = TextArgumentType.text(access)

	override fun parse(ctx: CommandContext<FabricClientCommandSource>, name: String): Text =
		ctx.getArgument(name, Text::class.java)
}