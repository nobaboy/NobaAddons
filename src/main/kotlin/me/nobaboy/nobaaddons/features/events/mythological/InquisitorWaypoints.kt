package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.api.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.events.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatcher
import me.nobaboy.nobaaddons.utils.RegexUtils.matchMatchers
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import me.nobaboy.nobaaddons.utils.SoundUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.network.OtherClientPlayerEntity
import java.util.regex.Pattern
import kotlin.time.Duration.Companion.seconds

object InquisitorWaypoints {
	private val config get() = NobaConfigManager.config.events.mythological

	private val inquisitorDigUpPattern = Pattern.compile("^[A-z ]+! You dug out a Minos Inquisitor!")
	private val inquisitorDeadPattern = Pattern.compile("(?:Party > )?(?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+): Inquisitor dead!")

	private val inquisitorSpawnPatterns = listOf(
		Pattern.compile("(?:Party > )?(?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+): [Xx]: (?<x>[0-9.-]+),? [Yy]: (?<y>[0-9.-]+),? [Zz]: (?<z>[0-9.-]+).*"),
		Pattern.compile("(?:Party > )?(?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+): A MINOS INQUISITOR has spawned near \\[.*] at Coords (?<x>[0-9.-]+) (?<y>[0-9.-]+) (?<z>[0-9.-]+)"),
	)

	private val inquisitorsNearby = mutableListOf<OtherClientPlayerEntity>()
	private var lastInquisitorId: Int? = null

	private val inquisitorSpawnTimes = mutableListOf<Timestamp>()

	val waypoints = mutableListOf<Inquisitor>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		SecondPassedEvent.EVENT.register { onSecondPassed() }
		MythologicalEvents.INQUISITOR_SPAWN.register(this::onInquisitorSpawn)
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onSecondPassed() {
		if(!isEnabled()) return

		inquisitorsNearby.removeIf { !it.isAlive || EntityUtils.getEntityByID(it.id) !== it }
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

		inquisitorSpawnPatterns.matchMatchers(message) {
			val username = group("username")

			if(username == MCUtils.playerName) return
			if(waypoints.any { it.spawner == username }) return

			val x = group("x").toDouble()
			val y = group("y").toDouble()
			val z = group("z").toDouble()
			val location = NobaVec(x, y, z)

			val inquisitor = Inquisitor(username, location, Timestamp.now())
			waypoints.add(inquisitor)

			RenderUtils.drawTitle("INQUISITOR!", NobaColor.DARK_RED)
			RenderUtils.drawTitle(username, NobaColor.GOLD, scale = 3.0f, height = 1.7)
			if(config.zeldaSecretSoundOnInquisitor) SoundUtils.zeldaSecretSound.play() else SoundUtils.dingSound.play()
		}

		inquisitorDeadPattern.matchMatcher(message) {
			waypoints.removeIf { it.spawner == group("username") }
		}
	}

	fun reset() {
		waypoints.clear()
		inquisitorsNearby.clear()
		inquisitorSpawnTimes.clear()
		lastInquisitorId = null
	}

	private fun checkInquisitor() {
		inquisitorSpawnTimes.add(Timestamp.now())

		val lastTwo = inquisitorSpawnTimes.takeLast(2)
		if(lastTwo.size != 2) return
		if(lastTwo.any { it.elapsedSince() >= 1.5.seconds }) return

		inquisitorSpawnTimes.clear()
		shareInquisitor()
	}

	private fun shareInquisitor() {
		lastInquisitorId?.let {
			val inquisitor = EntityUtils.getEntityByID(it) ?: return
			if(!inquisitor.isAlive) return

			val (x, y, z) = inquisitor.getNobaVec().roundToBlock().toDoubleArray()

			val message = "x: $x, y: $y, z: $z | Minos Inquisitor at [ ${SkyBlockAPI.prefixedZone} ]"

			if(config.alertOnlyInParty) {
				if(PartyAPI.party != null) HypixelCommands.partyChat(message)
			} else {
				ChatUtils.sendChatAsPlayer(message)
			}
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