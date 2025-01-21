package me.nobaboy.nobaaddons.commands.adapters

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.ArgumentHandler
import me.nobaboy.nobaaddons.core.Rarity
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlin.reflect.KParameter

object RarityHandler : ArgumentHandler<Rarity, FabricClientCommandSource> {
	override fun argument(parameter: KParameter): ArgumentType<Rarity> = Rarity.RarityArgumentType

	override fun parse(ctx: CommandContext<FabricClientCommandSource>, name: String): Rarity =
		Rarity.RarityArgumentType.getItemRarity(ctx, name)
}