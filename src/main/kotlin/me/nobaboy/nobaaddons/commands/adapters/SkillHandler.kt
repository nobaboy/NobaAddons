package me.nobaboy.nobaaddons.commands.adapters

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.ArgumentHandler
import me.nobaboy.nobaaddons.core.Skill
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import kotlin.reflect.KParameter

object SkillHandler : ArgumentHandler<Skill, FabricClientCommandSource> {
	override fun argument(parameter: KParameter): ArgumentType<Skill> = Skill.SkillArgumentType

	override fun parse(ctx: CommandContext<FabricClientCommandSource>, name: String): Skill =
		Skill.SkillArgumentType.getItemRarity(ctx, name)
}