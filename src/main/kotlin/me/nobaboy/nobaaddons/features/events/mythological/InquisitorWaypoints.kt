package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.ChatMessageEvents
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.events.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.firstPartialMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.gold
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.client.network.OtherClientPlayerEntity
import kotlin.time.Duration.Companion.seconds

object InquisitorWaypoints {
	private val config get() = NobaConfig.INSTANCE.events.mythological
	private val enabled: Boolean get() = config.alertInquisitor && DianaAPI.isActive

	private val inquisitorDigUpPattern by Regex("^[A-z ]+! You dug out a Minos Inquisitor!").fromRepo("mythological.inquisitor")
	private val inquisitorDeadPattern by Regex("(?:Party > )?(?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+): Inquisitor dead!").fromRepo("mythological.inquisitor_dead")

	private val inquisitorSpawnPatterns by Repo.list(
		Regex("(?<username>[A-z0-9_]+): [Xx]: (?<x>[0-9.-]+),? [Yy]: (?<y>[0-9.-]+),? [Zz]: (?<z>[0-9.-]+).*").fromRepo("mythological.inquisitor_spawn.coords"),
		Regex("(?<username>[A-z0-9_]+): A MINOS INQUISITOR has spawned near \\[.*] at Coords (?<x>[0-9.-]+) (?<y>[0-9.-]+) (?<z>[0-9.-]+)").fromRepo("mythological.inquisitor_spawn.inquisitorchecker"),
	)

	private val inquisitorsNearby = mutableListOf<OtherClientPlayerEntity>()
	private var lastInquisitorId: Int? = null

	private val inquisitorSpawnTimes = mutableListOf<Timestamp>()

	val waypoints = mutableListOf<Inquisitor>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		SecondPassedEvent.EVENT.register { onSecondPassed() }
		MythologicalEvents.INQUISITOR_SPAWN.register(this::onInquisitorSpawn)
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onSecondPassed() {
		if(!enabled) return

		inquisitorsNearby.removeIf { !it.isAlive || EntityUtils.getEntityById(it.id) !== it }
		waypoints.removeIf { it.spawnTime.elapsedSince() > 75.seconds }
	}

	private fun onInquisitorSpawn(event: MythologicalEvents.InquisitorSpawn) {
		val inquisitor = event.entity

		inquisitorsNearby.add(inquisitor)
		lastInquisitorId = inquisitor.id

		checkInquisitor()
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return

		if(inquisitorDigUpPattern matches message) checkInquisitor()

		inquisitorSpawnPatterns.firstPartialMatch(message) {
			val username = groups["username"]!!.value

			if(username == MCUtils.playerName) return
			if(waypoints.any { it.spawner == username }) return

			val x = groups["x"]!!.value.toDouble()
			val y = groups["y"]!!.value.toDouble()
			val z = groups["z"]!!.value.toDouble()
			val location = NobaVec(x, y, z)

			val inquisitor = Inquisitor(username, location, Timestamp.now())
			waypoints.add(inquisitor)

			RenderUtils.drawTitle("INQUISITOR!", NobaColor.DARK_RED, subtext = username.toText().gold())
			config.notificationSound.play()
		}

		inquisitorDeadPattern.onFullMatch(message) {
			waypoints.removeIf { it.spawner == groups["username"]!!.value }
		}
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
			val inquisitor = EntityUtils.getEntityById(it) ?: return
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

		ChatUtils.addMessage(tr("nobaaddons.events.mythological.couldntFindInquisitor", "Couldn't find ${inquisitor.spawner}'s Inquisitor."))
		waypoints.remove(inquisitor)
	}

	fun reset() {
		waypoints.clear()
		inquisitorsNearby.clear()
		inquisitorSpawnTimes.clear()
		lastInquisitorId = null
	}

	data class Inquisitor(val spawner: String, val location: NobaVec, val spawnTime: Timestamp)
}