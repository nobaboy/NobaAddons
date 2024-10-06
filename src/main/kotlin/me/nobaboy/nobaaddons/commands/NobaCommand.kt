package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.tree.CommandNode
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager.getConfigScreen
import me.nobaboy.nobaaddons.config.NobaMainScreen
import me.nobaboy.nobaaddons.utils.ScreenUtils.queueOpen
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess

object NobaCommand {
	fun init() {
		ClientCommandRegistrationCallback.EVENT.register(this::register)
	}

	private fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, ignored: CommandRegistryAccess) {
		val main: CommandNode<FabricClientCommandSource> =
			dispatcher.register(ClientCommandManager.literal("nobaaddons")
				.then(ClientCommandManager.literal("config")
					.executes { getConfigScreen(null).queueOpen() })

				.then(ClientCommandManager.literal("debugParty")
					.executes { PartyAPI.listMembers() })


				.executes { NobaMainScreen().queueOpen() }
			)

		dispatcher.register(ClientCommandManager.literal("noba")
			.executes { NobaMainScreen().queueOpen() }
			.redirect(main))
	}
}