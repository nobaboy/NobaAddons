package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.events.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockIslandChangeEvent
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.getNobaVec
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.server.network.ServerPlayerEntity
import java.util.regex.Pattern
import kotlin.time.Duration.Companion.seconds

object InquisitorWaypoints {
	private val config get() = NobaConfigManager.config.events.mythological

	private val inquisitorAlertPattern = Pattern.compile("(?i)(?<party>Party >)?(?<username>[A-z0-9_]+): Inquisitor spawned at x: (?<x>[0-9.-]+),? y: (?<y>[0-9.-]+),? z: (?<z>[0-9.-]+)")
	private val inquisitorDeadPattern = Pattern.compile("(?i)(?<party>Party >)?(?<username>[A-z0-9_]+): Inquisitor dead!")

	private val inquisitorDigUpPattern = Pattern.compile(".* You dug out a Minos Inquisitor!")

	private val inquisitorsNearby = mutableListOf<ServerPlayerEntity>()
	private var lastInquisitorId: Int? = null

	private val inquisitorSpawnTimes = mutableListOf<Timestamp>()

	val waypoints = mutableListOf<Inquisitor>()

	fun init() {
		SkyBlockIslandChangeEvent.EVENT.register { reset() }
		SecondPassedEvent.EVENT.register { onSecondPassed() }
		MythologicalEvents.INQUISITOR_SPAWN.register(this::onInquisitorSpawn)
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onSecondPassed() {
		if(!isEnabled()) return

		inquisitorsNearby.removeIf { !it.isAlive }
		waypoints.removeIf { it.spawnTime.elapsedSince() > 75.seconds }
	}

	private fun onInquisitorSpawn(event: MythologicalEvents.InquisitorSpawn) {
		val inquisitor = event.entity

		inquisitorsNearby.add(inquisitor)
		lastInquisitorId = inquisitor.id

		checkInquisitor()
	}

	private fun onChatMessage(message: String) {
		if(!isEnabled()) return

		if(inquisitorDigUpPattern.matches(message)) checkInquisitor()

		inquisitorAlertPattern.matchMatcher(message) {
			val username = group("username")

			if(username == MCUtils.playerName) return
			if(waypoints.any { it.spawner == username }) return

			val x = group("x").toDouble()
			val y = group("y").toDouble()
			val z = group("z").toDouble()
			val location = NobaVec(x, y, z)

			val inquisitor = Inquisitor(username, location, Timestamp.now())
			waypoints.add(inquisitor)
		}

		inquisitorDeadPattern.matchMatcher(message) {
			waypoints.removeIf { it.spawner == group("username") }
		}
	}

	private fun reset() {
		waypoints.clear()
		inquisitorsNearby.clear()
		inquisitorSpawnTimes.clear()
		lastInquisitorId = null
	}

	private fun checkInquisitor() {
		inquisitorSpawnTimes.add(Timestamp.now())

		val lastTwo = inquisitorSpawnTimes.takeLast(2)
		if(lastTwo.size != 2) return
		if(lastTwo.none { it.elapsedSince() < 1.5.seconds }) return

		inquisitorSpawnTimes.clear()
		shareInquisitor()
	}

	private fun shareInquisitor() {
		lastInquisitorId?.let {
			val inquisitor = EntityUtils.getEntityByID(it) ?: return
			if(!inquisitor.isAlive) return

			val (x, y, z) = inquisitor.getNobaVec().toDoubleArray()

			val message = "Inquisitor spawned at x: $x, y: $y, z: $z"

			if(config.alertOnlyInParty) HypixelCommands.partyChat(message)
				else ChatUtils.sendChatAsPlayer(message)
		}
	}

	fun tryRemove(inquisitor: Inquisitor) {
		if(inquisitorsNearby.isNotEmpty()) return

		ChatUtils.addMessage("Couldn't find ${inquisitor.spawner}'s Inquisitor.")
		waypoints.remove(inquisitor)
	}

	data class Inquisitor(val spawner: String, val location: NobaVec, val spawnTime: Timestamp)

	private fun isEnabled() = DianaAPI.isActive() && config.alertInquisitor
}