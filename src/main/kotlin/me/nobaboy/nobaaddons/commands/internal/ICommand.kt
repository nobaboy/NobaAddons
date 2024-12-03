package me.nobaboy.nobaaddons.commands.internal

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

interface ICommand {
	val name: String
	val aliases: List<String>
	val enabled: Boolean
	fun create(name: String): LiteralArgumentBuilder<FabricClientCommandSource>
	fun execute(ctx: CommandContext<FabricClientCommandSource>): Int
}