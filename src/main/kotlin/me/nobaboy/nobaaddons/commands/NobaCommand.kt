package me.nobaboy.nobaaddons.commands

import me.nobaboy.nobaaddons.api.skyblock.mythological.BurrowAPI
import me.nobaboy.nobaaddons.commands.annotations.Command
import me.nobaboy.nobaaddons.commands.annotations.Group
import me.nobaboy.nobaaddons.commands.annotations.RootCommand
import me.nobaboy.nobaaddons.commands.debug.DebugCommands
import me.nobaboy.nobaaddons.commands.impl.*
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
import net.minecraft.util.Formatting

@Suppress("unused")
@Group("nobaaddons", "noba")
object NobaCommand {
	fun init() {
		CommandUtil.registerRoot(this)
	}

	@RootCommand
	fun root(ctx: Context) {
		NobaMainScreen().queueOpen()
	}

	@Command
	fun config(ctx: Context) {
		NobaConfigManager.getConfigScreen(null).queueOpen()
	}

	@Command
	fun keybinds(ctx: Context) {
		KeyBindsScreen(null).queueOpen()
	}

	@Command
	fun hud(ctx: Context) {
		NobaHudScreen(null).queueOpen()
	}

	@Command
	fun ping(ctx: Context) {
		PingUtils.sendPingPacket(sendMessage = true)
	}

	@Group
	object Refill {
		@Command
		fun pearls(ctx: Context) {
			RefillFromSacks.refill("ENDER_PEARL", 16)
		}

		@Command
		fun superboom(ctx: Context) {
			RefillFromSacks.refill("SUPERBOOM_TNT", 64)
		}

		@Command
		fun leaps(ctx: Context) {
			RefillFromSacks.refill("SPIRIT_LEAP", 16)
		}

		@Command
		fun item(ctx: Context, item: String, count: Int = 64) {
			RefillFromSacks.refill(item, count)
		}
	}

	@Command
	fun sendCoords(ctx: Context) {
		ChatUtils.sendChatAsPlayer(LocationUtils.playerCoords())
	}

	@Command
	fun waypoint(ctx: Context, x: Double, y: Double, z: Double) {
		if(!TemporaryWaypoint.enabled) {
			ChatUtils.addMessage(tr("nobaaddons.temporaryWaypoint.notEnabled", "Temporary Waypoints are not enabled in the mod config"), color = Formatting.RED)
			return
		}
		TemporaryWaypoint.addWaypoint(x, y, z, "Temporary Waypoint")
		ChatUtils.addMessage(tr("nobaaddons.temporaryWaypoint.createdFromCommand", "Added a waypoint at $x, $y, $z. This waypoint will last until you walk near it."))
	}

	@Command
	fun lockMouse(ctx: Context) {
		MouseLock.lockMouse()
	}

	@Group("mythological", "mytho")
	object Diana {
		@Command
		fun resetWarps(ctx: Context) {
			BurrowWarpLocations.unlockAll()
		}

		@Command
		fun clearBurrows(ctx: Context) {
			BurrowAPI.reset()
			InquisitorWaypoints.reset()
			BurrowWaypoints.reset()
			ChatUtils.addMessage(tr("nobaaddons.command.clearedBurrows", "Cleared all waypoints"))
		}
	}

	@Group(aliases = ["ss"])
	object SimonSays {
		@Command
		fun clear(ctx: Context) {
			SimonSaysTimer.clearTimes()
		}

		@Command
		fun average(ctx: Context) {
			SimonSaysTimer.sendAverage()
		}

		@Command(aliases = ["pb"])
		fun personalBest(ctx: Context) {
			SimonSaysTimer.sendPersonalBest()
		}
	}

	@Command
	fun day(ctx: Context) {
		val day = MCUtils.world?.day?.addSeparators()
		if(day == null) {
			ChatUtils.addMessage(tr("nobaaddons.command.currentDay.unknown", "I can't figure out what the current lobby day is!"))
			return
		}
		ChatUtils.addMessage(tr("nobaaddons.command.currentDay", "This lobby is at day $day"))
	}

	val repo = AnnotatedGroup(RepoCommands)
	val debug = AnnotatedGroup(DebugCommands)
	val internal = AnnotatedGroup(InternalCommands)
}