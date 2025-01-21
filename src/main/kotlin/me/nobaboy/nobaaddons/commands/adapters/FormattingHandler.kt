package me.nobaboy.nobaaddons.commands.adapters

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.ArgumentHandler
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EnumArgumentType
import net.minecraft.util.Formatting
import net.minecraft.util.StringIdentifiable
import kotlin.reflect.KParameter
import kotlin.reflect.full.hasAnnotation

class FormattingHandler<S : CommandSource> : ArgumentHandler<Formatting, S> {
	@Target(AnnotationTarget.TYPE)
	annotation class ColorOnly

	object FormattingArgumentType : EnumArgumentType<Formatting>(
		StringIdentifiable.createCodec(Formatting.entries::toTypedArray),
		Formatting.entries::toTypedArray
	)

	object ColorFormattingArgumentType : EnumArgumentType<Formatting>(
		StringIdentifiable.createCodec { Formatting.entries.filter {  it.colorValue != null }.toTypedArray() },
		{ Formatting.entries.filter { it.colorValue != null }.toTypedArray() }
	)

	override fun argument(parameter: KParameter): ArgumentType<Formatting> =
		if(parameter.type.hasAnnotation<ColorOnly>()) ColorFormattingArgumentType else FormattingArgumentType

	override fun parse(ctx: CommandContext<S>, name: String): Formatting =
		ctx.getArgument(name, Formatting::class.java)
}