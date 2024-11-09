package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.CommandUtil
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.ui.NobaMainScreen
import me.nobaboy.nobaaddons.features.dungeons.SimonSaysTimer
import me.nobaboy.nobaaddons.features.general.RefillPearls
import me.nobaboy.nobaaddons.features.visuals.TemporaryWaypoint
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.ScreenUtils.queueOpen
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

@Suppress("unused")
object NobaCommand : Group("nobaaddons", aliases = listOf("noba"), executeRoot = true) {
	fun init() {
		CommandUtil.register(this)
	}

	override fun execute(ctx: CommandContext<FabricClientCommandSource>): Int {
		NobaMainScreen().queueOpen()
		return 0
	}

	val config = Command.command("config") {
		executes {
			NobaConfigManager.getConfigScreen(null).queueOpen()
		}
	}

	val refillPearls = Command.command("refillpearls") {
		executes {
			RefillPearls.refillPearls()
		}
	}

	val sendCoords = Command.command("sendcoords") {
		executes {
			ChatUtils.sendChatAsPlayer(LocationUtils.playerCoords())
		}
	}

	val waypoint = Command.command("waypoint") {
		buildCommand {
			ClientCommandManager.literal(name)
				.then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
					.then(ClientCommandManager.argument("y", DoubleArgumentType.doubleArg())
						.then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
							.executes(this::execute))))
		}

		executes(TemporaryWaypoint::addWaypoint)
	}

	object Debug : Group("debug") {
		val party = Command.command("party") {
			executes {
				PartyAPI.listMembers()
			}
		}
	}

	object SimonSays : Group("ss", aliases = listOf("simonsays")) {
		val clear = Command.command("clear") {
			executes {
				SimonSaysTimer.clearTimes()
			}
		}

		val average = Command.command("average") {
			executes {
				SimonSaysTimer.sendAverage()
			}
		}

		val personalBest = Command.command("personalbest", aliases = listOf("pb")) {
			executes {
				SimonSaysTimer.sendPersonalBest()
			}
		}
	}
}