package me.nobaboy.nobaaddons.commands.adapters

import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.Codec
import dev.celestialfault.commander.ArgumentHandler
import me.nobaboy.nobaaddons.ui.ElementAlignment
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EnumArgumentType
import net.minecraft.util.StringIdentifiable
import kotlin.reflect.KParameter

object ElementAlignmentHandler : ArgumentHandler<ElementAlignment, FabricClientCommandSource> {
	override fun argument(parameter: KParameter): ArgumentType<ElementAlignment> = ElementAlignmentArgumentType

	override fun parse(ctx: CommandContext<FabricClientCommandSource>, name: String): ElementAlignment =
		ElementAlignmentArgumentType.getAlignment(ctx, name)

	private val CODEC: Codec<ElementAlignment> = StringIdentifiable.createCodec { ElementAlignment.entries.toTypedArray() }

	object ElementAlignmentArgumentType : EnumArgumentType<ElementAlignment>(CODEC, { ElementAlignment.entries.toTypedArray() }) {
		fun getAlignment(context: CommandContext<out CommandSource>, id: String): ElementAlignment {
			return context.getArgument(id, ElementAlignment::class.java)
		}
	}
}