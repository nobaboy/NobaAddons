package me.nobaboy.nobaaddons.commands.impl

import com.mojang.brigadier.context.CommandContext
import dev.celestialfault.commander.Commander
import dev.celestialfault.commander.ICommand
import me.nobaboy.nobaaddons.commands.adapters.*
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.ui.ElementAlignment
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.argument.NbtPathArgumentType
import net.minecraft.text.Text
import net.minecraft.util.Formatting

typealias Context = CommandContext<FabricClientCommandSource>

object CommandUtil {
	private val commands: MutableList<ICommand<FabricClientCommandSource>> = mutableListOf()

	val commander = Commander<FabricClientCommandSource>()

	init {
		val commander = Commander<FabricClientCommandSource>()
		commander.addHandler(Formatting::class, FormattingHandler())
		commander.addHandler(NbtPathArgumentType.NbtPath::class, NbtPathHandler)
		commander.addHandler(Rarity::class, RarityHandler)
		commander.addHandler(ElementAlignment::class, ElementAlignmentHandler)

		ClientCommandRegistrationCallback.EVENT.register { dispatch, access ->
			commander.addHandler(Text::class, TextHandler(access))
			commands.forEach { commander.register(it, dispatch) }
		}
	}

	fun registerRoot(command: Any) {
		commands.add(NobaClientCommandGroup(command))
	}

	fun register(command: ICommand<FabricClientCommandSource>) {
		commands.add(command)
	}
}