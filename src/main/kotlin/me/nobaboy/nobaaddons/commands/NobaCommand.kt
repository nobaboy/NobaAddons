package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.api.skyblock.mythological.BurrowAPI
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.CommandUtil
import me.nobaboy.nobaaddons.commands.internal.Group
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.features.dungeons.SimonSaysTimer
import me.nobaboy.nobaaddons.features.events.mythological.BurrowWarpLocations
import me.nobaboy.nobaaddons.features.events.mythological.BurrowWaypoints
import me.nobaboy.nobaaddons.features.events.mythological.InquisitorWaypoints
import me.nobaboy.nobaaddons.features.general.RefillFromSacks
import me.nobaboy.nobaaddons.features.qol.MouseLock
import me.nobaboy.nobaaddons.features.visuals.TemporaryWaypoint
import me.nobaboy.nobaaddons.screens.NobaHudScreen
import me.nobaboy.nobaaddons.screens.NobaMainScreen
import me.nobaboy.nobaaddons.screens.keybinds.KeyBindsScreen
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.PingUtils
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

	val keybinds = Command.command("keybinds") {
		executes {
			KeyBindsScreen(null).queueOpen()
		}
	}

	val hud = Command.command("hud") {
		executes {
			NobaHudScreen(null).queueOpen()
		}
	}

	val ping = Command.command("ping") {
		executes {
			PingUtils.sendPingPacket(sendMessage = true)
		}
	}

	object Refill : Group("refill") {
		val pearls = Command.command("pearls") {
			executes {
				RefillFromSacks.refill("ENDER_PEARLS", 16)
			}
		}

		val superboom = Command.command("superboom") {
			executes {
				RefillFromSacks.refill("SUPERBOOM_TNT", 64)
			}
		}

		val leaps = Command.command("leaps") {
			executes {
				RefillFromSacks.refill("SPIRIT_LEAP", 16)
			}
		}

		val item = Command.command("item") {
			buildCommand {
				it.then(ClientCommandManager.argument("id", StringArgumentType.string())
					.executes(this::execute)
					.then(ClientCommandManager.argument("count", IntegerArgumentType.integer(1))
						.executes(this::execute)))
			}

			executes {
				val id = StringArgumentType.getString(this, "id")
				val count = runCatching { IntegerArgumentType.getInteger(this, "count") }.getOrDefault(64)
				RefillFromSacks.refill(id, count)
			}
		}
	}

	val sendCoords = Command.command("sendcoords") {
		executes {
			ChatUtils.sendChatAsPlayer(LocationUtils.playerCoords())
		}
	}

	val waypoint = Command.command("waypoint") {
		buildCommand {
			it.then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
				.then(ClientCommandManager.argument("y", DoubleArgumentType.doubleArg())
					.then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
						.executes(this::execute))))
		}

		executes(TemporaryWaypoint::addWaypoint)
	}

	val lockMouse = Command.command("lockmouse") {
		executes {
			MouseLock.lockMouse()
		}
	}

	object Diana : Group("mythological", aliases = listOf("mytho")) {
		val resetWarps = Command.command("resetwarps") {
			executes {
				BurrowWarpLocations.unlockAll()
			}
		}

		val resetBurrows = Command.command("clearburrows") {
			executes {
				BurrowAPI.reset()
				InquisitorWaypoints.reset()
				BurrowWaypoints.reset()
				ChatUtils.addMessage("Cleared all waypoints")
			}
		}
	}

	object SimonSays : Group("simonsays", aliases = listOf("ss")) {
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

	val debug = DebugCommands
}