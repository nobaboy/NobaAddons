package me.nobaboy.nobaaddons.commands.adapters

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.ArgumentHandler
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Rarity.Companion.toArray
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

object RarityHandler : ArgumentHandler<Rarity, FabricClientCommandSource> {
	override fun argument(parameter: KParameter): ArgumentType<Rarity> {
		parameter.type.findAnnotation<Max>()?.let {
			return Rarity.RarityArgumentType((Rarity.COMMON..it.max).toArray())
		}
		return Rarity.RarityArgumentType()
	}

	override fun parse(ctx: CommandContext<FabricClientCommandSource>, name: String): Rarity =
		ctx.getArgument(name, Rarity::class.java)

	/**
	 * Use this annotation to only suggest rarities between [Rarity.COMMON]..[max]
	 */
	@Target(AnnotationTarget.TYPE)
	annotation class Max(val max: Rarity)
}