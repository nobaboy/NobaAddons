package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.events.MythologicalMobs
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.firstPartialMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.onPartialMatch
import me.nobaboy.nobaaddons.utils.TextUtils.gold
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.toShortString
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.network.OtherClientPlayerEntity
import kotlin.time.Duration.Companion.seconds

object InquisitorWaypoints {
	private val config get() = NobaConfig.events.mythological
	private val enabled: Boolean get() = config.alertInquisitor && DianaAPI.isActive

	private val INQUISITOR_DEAD_REGEX by Regex("(?:Party > )?(?:\\[[A-Z+]+] )?(?<username>[A-z0-9_]+): Inquisitor dead!").fromRepo("mythological.inquisitor_dead")
	private val INQUISITOR_SPAWN_REGEXES by Repo.list(
		Regex("(?<username>[A-z0-9_]+): [Xx]: (?<x>[0-9.-]+),? [Yy]: (?<y>[0-9.-]+),? [Zz]: (?<z>[0-9.-]+).*").fromRepo("mythological.inquisitor_spawn.coords"),
		Regex("(?<username>[A-z0-9_]+): A MINOS INQUISITOR has spawned near \\[.*] at Coords (?<x>[0-9.-]+) (?<y>[0-9.-]+) (?<z>[0-9.-]+)").fromRepo("mythological.inquisitor_spawn.inquisitorchecker"),
	)

	val inquisitors = mutableListOf<Inquisitor>()

	private val inquisitorSpawnTimes = mutableListOf<Timestamp>()
	private val nearbyInquisitors = mutableListOf<OtherClientPlayerEntity>()
	private var lastInquisitor: OtherClientPlayerEntity? = null

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		TickEvents.everySecond { onSecondPassed() }
		MythologicalEvents.MOB_DIG.register(this::onMobDig)
		EntityEvents.SPAWN.register(this::onEntitySpawn)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
	}

	private fun onSecondPassed() {
		if(!enabled) return

		inquisitors.removeIf { it.timestamp.isPast() }
		nearbyInquisitors.removeIf { !it.isAlive || EntityUtils.getEntityById(it.id) !== it }
	}

	private fun onMobDig(event: MythologicalEvents.MobDig) {
		if(!enabled) return
		if(event.mob == MythologicalMobs.MINOS_INQUISITOR) checkInquisitor()
	}

	private fun onEntitySpawn(event: EntityEvents.Spawn) {
		if(!enabled) return

		val entity = event.entity as? OtherClientPlayerEntity ?: return
		if(entity.name.string != "Minos Inquisitor") return

		nearbyInquisitors.add(entity)
		lastInquisitor = entity
		checkInquisitor()
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!enabled) return

		val message = event.cleaned

		INQUISITOR_SPAWN_REGEXES.firstPartialMatch(message) {
			val username = groups["username"]?.value ?: return
			if(username == MCUtils.playerName) return
			if(inquisitors.any { it.spawner == username }) return

			val x = groups["x"]!!.value.toDouble()
			val y = groups["y"]!!.value.toDouble()
			val z = groups["z"]!!.value.toDouble()
			val location = NobaVec(x, y, z)

			inquisitors.add(Inquisitor(username, location))
			RenderUtils.drawTitle("INQUISITOR!", NobaColor.DARK_RED, subtext = username.toText().gold())
			config.notificationSound.play()
		}

		INQUISITOR_DEAD_REGEX.onPartialMatch(message) {
			inquisitors.removeIf { it.spawner == groups["username"]?.value }
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
		lastInquisitor?.takeIf { it.isAlive }?.let {
			val (x, y, z) = it.getNobaVec().roundToBlock().toDoubleArray()
			val message = "x: $x, y: $y, z: $z | Minos Inquisitor at [ ${SkyBlockAPI.prefixedZone} ]"
			config.announceChannel.send(message)
		}
	}

	fun tryRemove(inquisitor: Inquisitor) {
		if(nearbyInquisitors.isNotEmpty()) return

		ChatUtils.addMessage(tr("nobaaddons.events.mythological.couldntFindInquisitor", "Couldn't find ${inquisitor.spawner}'s Inquisitor."))
		inquisitors.remove(inquisitor)
	}

	fun reset() {
		inquisitors.clear()
		inquisitorSpawnTimes.clear()
		nearbyInquisitors.clear()
		lastInquisitor = null
	}

	data class Inquisitor(
		val spawner: String,
		val location: NobaVec,
		val timestamp: Timestamp = Timestamp.now() + 75.seconds
	) {
		val remainingTime: String get() = timestamp.timeRemaining().toShortString()
	}
}