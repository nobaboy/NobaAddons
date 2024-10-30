package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.tree.CommandNode
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager.getConfigScreen
import me.nobaboy.nobaaddons.config.ui.NobaMainScreen
import me.nobaboy.nobaaddons.features.general.RefillPearls
import me.nobaboy.nobaaddons.features.visuals.TemporaryWaypoint
import me.nobaboy.nobaaddons.utils.LocationUtils.playerCoords
import me.nobaboy.nobaaddons.utils.ScreenUtils.queueOpen
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.command.CommandRegistryAccess

object NobaCommand {
	fun init() {
		ClientCommandRegistrationCallback.EVENT.register(this::register)
	}

	private fun register(dispatcher: CommandDispatcher<FabricClientCommandSource>, ignored: CommandRegistryAccess) {
		val mainCommand: CommandNode<FabricClientCommandSource> =
			dispatcher.register(
				ClientCommandManager.literal("nobaaddons").apply {
					then(ClientCommandManager.literal("config").executes {
						getConfigScreen(null).queueOpen()
					})

					then(ClientCommandManager.literal("refillPearls").executes {
						RefillPearls.refillPearls()
						Command.SINGLE_SUCCESS
					})

					then(ClientCommandManager.literal("sendCoords").executes {
						ChatUtils.sendChatAsPlayer(playerCoords())
						Command.SINGLE_SUCCESS
					})

					then(ClientCommandManager.literal("waypoint")
						.then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
							.then(ClientCommandManager.argument("y", DoubleArgumentType.doubleArg())
								.then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
									.executes(TemporaryWaypoint::addWaypoint)))))

					then(ClientCommandManager.literal("debugParty").executes {
						PartyAPI.listMembers()
						Command.SINGLE_SUCCESS
					})

					executes { NobaMainScreen().queueOpen() }
				}
			)

			dispatcher.register(ClientCommandManager.literal("noba").executes {
				NobaMainScreen().queueOpen()
			}.redirect(mainCommand))
	}
}