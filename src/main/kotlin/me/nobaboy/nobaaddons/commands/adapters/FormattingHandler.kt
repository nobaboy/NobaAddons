package me.nobaboy.nobaaddons.commands.adapters

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.ArgumentHandler
import me.nobaboy.nobaaddons.commands.impl.CommandUtil.getArgument
import me.nobaboy.nobaaddons.utils.JavaUtils
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.util.Formatting
import kotlin.reflect.KParameter
import kotlin.reflect.full.hasAnnotation

object FormattingHandler : ArgumentHandler<Formatting, FabricClientCommandSource> {
	@Target(AnnotationTarget.TYPE)
	annotation class ColorOnly

	override fun argument(parameter: KParameter): ArgumentType<Formatting> =
		if(parameter.type.hasAnnotation<ColorOnly>()) COLOR_ARGUMENT_TYPE else FORMATTING_ARGUMENT_TYPE

	override fun parse(ctx: CommandContext<FabricClientCommandSource>, name: String): Formatting =
		ctx.getArgument(name)

	private val FORMATTING_ARGUMENT_TYPE = JavaUtils.enumArgument(Formatting::class.java)
	private val COLOR_ARGUMENT_TYPE = JavaUtils.enumArgument { Formatting.entries.filter { it.colorValue != null }.toTypedArray() }
}