package me.nobaboy.nobaaddons.commands

import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Group
import dev.celestialfault.commander.annotations.RootCommand
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.BurrowAPI
import me.nobaboy.nobaaddons.commands.debug.DebugCommands
import me.nobaboy.nobaaddons.commands.impl.CommandUtil
import me.nobaboy.nobaaddons.commands.impl.NobaClientCommandGroup
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.features.dungeons.SimonSaysTimer
import me.nobaboy.nobaaddons.features.events.mythological.BurrowWarpLocations
import me.nobaboy.nobaaddons.features.events.mythological.BurrowWaypoints
import me.nobaboy.nobaaddons.features.events.mythological.InquisitorWaypoints
import me.nobaboy.nobaaddons.features.general.RefillFromSacks
import me.nobaboy.nobaaddons.features.qol.MouseLock
import me.nobaboy.nobaaddons.features.visuals.TemporaryWaypoints
import me.nobaboy.nobaaddons.screens.NobaHudScreen
import me.nobaboy.nobaaddons.screens.NobaMainScreen
import me.nobaboy.nobaaddons.screens.keybinds.KeyBindsScreen
import me.nobaboy.nobaaddons.screens.notifications.ChatNotificationsScreen
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
	fun root() {
		NobaMainScreen().queueOpen()
	}

	@Command
	fun config() {
		NobaConfig.getConfigScreen(null).queueOpen()
	}

	@Command("hud", "gui")
	fun hud() {
		NobaHudScreen(null).queueOpen()
	}

	@Command
	fun notifications() {
		ChatNotificationsScreen(null).queueOpen()
	}

	@Command
	fun keybinds() {
		KeyBindsScreen(null).queueOpen()
	}

	@Command
	fun ping() {
		PingUtils.sendPingPacket(sendMessage = true)
	}

	@Command
	fun sendCoords() {
		ChatUtils.sendChatAsPlayer(LocationUtils.playerCoords())
	}

	@Command
	fun waypoint(x: Double, y: Double, z: Double) {
		if(!TemporaryWaypoints.enabled) {
			ChatUtils.addMessage(tr("nobaaddons.temporaryWaypoint.notEnabled", "Temporary Waypoints are not enabled in the mod config"), color = Formatting.RED)
			return
		}
		TemporaryWaypoints.addWaypoint(x, y, z, "Temporary Waypoint")
		ChatUtils.addMessage(tr("nobaaddons.temporaryWaypoint.createdFromCommand", "Added a waypoint at $x, $y, $z. This waypoint will last until you walk near it."))
	}

	@Command
	fun lockMouse() {
		MouseLock.lockMouse()
	}

	@Group
	object Refill {
		@Command
		fun pearls() {
			RefillFromSacks.refill("ENDER_PEARL", 16)
		}

		@Command
		fun superboom() {
			RefillFromSacks.refill("SUPERBOOM_TNT", 64)
		}

		@Command
		fun leaps() {
			RefillFromSacks.refill("SPIRIT_LEAP", 16)
		}

		@Command
		fun item(item: String, count: Int = 64) {
			RefillFromSacks.refill(item.uppercase(), count)
		}
	}

	@Group("mythological", "mytho")
	object Diana {
		@Command
		fun resetWarps() {
			BurrowWarpLocations.unlockAll()
		}

		@Command
		fun clearBurrows() {
			BurrowAPI.reset()
			InquisitorWaypoints.reset()
			BurrowWaypoints.reset()
			ChatUtils.addMessage(tr("nobaaddons.command.clearedBurrows", "Cleared all waypoints"))
		}
	}

	@Group(aliases = ["ss"])
	object SimonSays {
		@Command
		fun clear() {
			SimonSaysTimer.clearTimes()
		}

		@Command
		fun average() {
			SimonSaysTimer.sendAverage()
		}

		@Command(aliases = ["pb"])
		fun personalBest() {
			SimonSaysTimer.sendPersonalBest()
		}
	}

	@Command
	fun day() {
		val day = MCUtils.world?.day?.addSeparators()
		if(day == null) {
			ChatUtils.addMessage(tr("nobaaddons.command.currentDay.unknown", "I can't figure out what the current lobby day is!"))
			return
		}
		ChatUtils.addMessage(tr("nobaaddons.command.currentDay", "This lobby is at day $day"))
	}

	val repo = NobaClientCommandGroup(RepoCommands)
	val debug = NobaClientCommandGroup(DebugCommands)
	val internal = NobaClientCommandGroup(InternalCommands)
}