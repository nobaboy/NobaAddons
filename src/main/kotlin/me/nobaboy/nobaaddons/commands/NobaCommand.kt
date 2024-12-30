package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import me.nobaboy.nobaaddons.api.skyblock.mythological.BurrowAPI
import me.nobaboy.nobaaddons.commands.debug.DebugCommands
import me.nobaboy.nobaaddons.commands.internal.Command
import me.nobaboy.nobaaddons.commands.internal.CommandBuilder
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
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager

@Suppress("unused")
object NobaCommand : Group("nobaaddons", aliases = listOf("noba")) {
	fun init() {
		CommandUtil.register(this)
	}

	override val root = RootCommand {
		NobaMainScreen().queueOpen()
	}

	val config = Command("config") {
		NobaConfigManager.getConfigScreen(null).queueOpen()
	}

	val keybinds = Command("keybinds") {
		KeyBindsScreen(null).queueOpen()
	}

	val hud = Command("hud", aliases = listOf("gui")) {
		NobaHudScreen(null).queueOpen()
	}

	val ping = Command("ping") {
		PingUtils.sendPingPacket(sendMessage = true)
	}

	object Refill : Group("refill") {
		val pearls = Command("pearls") {
			RefillFromSacks.refill("ENDER_PEARLS", 16)
		}

		val superboom = Command("superboom") {
			RefillFromSacks.refill("SUPERBOOM_TNT", 64)
		}

		val leaps = Command("leaps") {
			RefillFromSacks.refill("SPIRIT_LEAP", 16)
		}

		private val itemCommandBuilder: CommandBuilder = {
			it.then(ClientCommandManager.argument("id", StringArgumentType.string())
				.executes(this::execute)
				.then(ClientCommandManager.argument("count", IntegerArgumentType.integer(1))
					.executes(this::execute)))
		}

		val item = Command("item", commandBuilder = itemCommandBuilder) {
			val id = StringArgumentType.getString(it, "id")
			val count = runCatching { IntegerArgumentType.getInteger(it, "count") }.getOrDefault(64)
			RefillFromSacks.refill(id, count)
		}
	}

	val sendCoords = Command("sendcoords") {
		ChatUtils.sendChatAsPlayer(LocationUtils.playerCoords())
	}

	private val waypointBuilder: CommandBuilder = {
		it.then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
			.then(ClientCommandManager.argument("y", DoubleArgumentType.doubleArg())
				.then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
					.executes(this::execute))))
	}

	val waypoint = Command("waypoint", commandBuilder = waypointBuilder, callback = TemporaryWaypoint::addWaypoint)

	val lockMouse = Command("lockmouse") {
		MouseLock.lockMouse()
	}

	object Diana : Group("mythological", aliases = listOf("mytho")) {
		val resetWarps = Command("resetwarps") {
			BurrowWarpLocations.unlockAll()
		}

		val resetBurrows = Command("clearburrows") {
			BurrowAPI.reset()
			InquisitorWaypoints.reset()
			BurrowWaypoints.reset()
			ChatUtils.addMessage(tr("nobaaddons.command.clearedBurrows", "Cleared all waypoints"))
		}
	}

	object SimonSays : Group("simonsays", aliases = listOf("ss")) {
		val clear = Command("clear") {
			SimonSaysTimer.clearTimes()
		}

		val average = Command("average") {
			SimonSaysTimer.sendAverage()
		}

		val personalBest = Command("personalbest", aliases = listOf("pb")) {
			SimonSaysTimer.sendPersonalBest()
		}
	}

	val repo = RepoCommands
	val debug = DebugCommands
	val internal = InternalCommands
}