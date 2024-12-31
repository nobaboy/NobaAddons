package me.nobaboy.nobaaddons.commands

import com.mojang.brigadier.arguments.DoubleArgumentType
import com.mojang.brigadier.arguments.IntegerArgumentType
import com.mojang.brigadier.arguments.StringArgumentType
import me.nobaboy.nobaaddons.api.skyblock.mythological.BurrowAPI
import me.nobaboy.nobaaddons.commands.debug.DebugCommands
import me.nobaboy.nobaaddons.commands.internal.ExecutableCommand
import me.nobaboy.nobaaddons.commands.internal.CommandBuilder
import me.nobaboy.nobaaddons.commands.internal.CommandUtil
import me.nobaboy.nobaaddons.commands.internal.CommandGroup
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
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.MCUtils.day
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.PingUtils
import me.nobaboy.nobaaddons.utils.ScreenUtils.queueOpen
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager

@Suppress("unused")
object NobaCommand : CommandGroup("nobaaddons", aliases = listOf("noba")) {
	fun init() {
		CommandUtil.register(this)
	}

	override val root = RootCommand {
		NobaMainScreen().queueOpen()
	}

	val config = ExecutableCommand("config") {
		NobaConfigManager.getConfigScreen(null).queueOpen()
	}

	val keybinds = ExecutableCommand("keybinds") {
		KeyBindsScreen(null).queueOpen()
	}

	val hud = ExecutableCommand("hud", aliases = listOf("gui")) {
		NobaHudScreen(null).queueOpen()
	}

	val ping = ExecutableCommand("ping") {
		PingUtils.sendPingPacket(sendMessage = true)
	}

	object Refill : CommandGroup("refill") {
		val pearls = ExecutableCommand("pearls") {
			RefillFromSacks.refill("ENDER_PEARL", 16)
		}

		val superboom = ExecutableCommand("superboom") {
			RefillFromSacks.refill("SUPERBOOM_TNT", 64)
		}

		val leaps = ExecutableCommand("leaps") {
			RefillFromSacks.refill("SPIRIT_LEAP", 16)
		}

		private val itemCommandBuilder: CommandBuilder = {
			it.then(ClientCommandManager.argument("id", StringArgumentType.string())
				.executes(this::execute)
				.then(ClientCommandManager.argument("count", IntegerArgumentType.integer(1))
					.executes(this::execute)))
		}

		val item = ExecutableCommand("item", builder = itemCommandBuilder) {
			val id = StringArgumentType.getString(it, "id").uppercase()
			val count = runCatching { IntegerArgumentType.getInteger(it, "count") }.getOrDefault(64)
			RefillFromSacks.refill(id, count)
		}
	}

	val sendCoords = ExecutableCommand("sendcoords") {
		ChatUtils.sendChatAsPlayer(LocationUtils.playerCoords())
	}

	private val waypointBuilder: CommandBuilder = {
		it.then(ClientCommandManager.argument("x", DoubleArgumentType.doubleArg())
			.then(ClientCommandManager.argument("y", DoubleArgumentType.doubleArg())
				.then(ClientCommandManager.argument("z", DoubleArgumentType.doubleArg())
					.executes(this::execute))))
	}

	val waypoint = ExecutableCommand("waypoint", builder = waypointBuilder, callback = TemporaryWaypoint::addWaypoint)

	val lockMouse = ExecutableCommand("lockmouse") {
		MouseLock.lockMouse()
	}

	object Diana : CommandGroup("mythological", aliases = listOf("mytho")) {
		val resetWarps = ExecutableCommand("resetwarps") {
			BurrowWarpLocations.unlockAll()
		}

		val resetBurrows = ExecutableCommand("clearburrows") {
			BurrowAPI.reset()
			InquisitorWaypoints.reset()
			BurrowWaypoints.reset()
			ChatUtils.addMessage(tr("nobaaddons.command.clearedBurrows", "Cleared all waypoints"))
		}
	}

	object SimonSays : CommandGroup("simonsays", aliases = listOf("ss")) {
		val clear = ExecutableCommand("clear") {
			SimonSaysTimer.clearTimes()
		}

		val average = ExecutableCommand("average") {
			SimonSaysTimer.sendAverage()
		}

		val personalBest = ExecutableCommand("personalbest", aliases = listOf("pb")) {
			SimonSaysTimer.sendPersonalBest()
		}
	}

	val day = ExecutableCommand("day") {
		val day = MCUtils.world?.day?.addSeparators()
		if(day == null) {
			ChatUtils.addMessage(tr("nobaaddons.command.currentDay.unknown", "I can't figure out what the current lobby day is!"))
			return@ExecutableCommand
		}
		ChatUtils.addMessage(tr("nobaaddons.command.currentDay", "This lobby is at day $day"))
	}

	val repo = RepoCommands
	val debug = DebugCommands
	val internal = InternalCommands
}