package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.BurrowAPI
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.events.MythologicalMobs
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.BlockUtils.getBlockAt
import me.nobaboy.nobaaddons.utils.BlockUtils.inLoadedChunk
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.RegexUtils.firstPartialMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.onPartialMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.gold
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.block.Blocks
import net.minecraft.client.network.OtherClientPlayerEntity
import kotlin.time.Duration.Companion.seconds

object MythologicalWaypoints {
	private val config get() = NobaConfig.INSTANCE.events.mythological

	private val INQUISITOR_DEAD_REGEX by Regex("(?<username>[A-z0-9_]+): Inquisitor dead!").fromRepo("mythological.inquisitor_dead")
	private val INQUISITOR_SPAWN_REGEXES by Repo.list(
		Regex("(?<username>[A-z0-9_]+): [Xx]: (?<x>[0-9.-]+),? [Yy]: (?<y>[0-9.-]+),? [Zz]: (?<z>[0-9.-]+).*").fromRepo("mythological.inquisitor_spawn.coords"),
		Regex("(?<username>[A-z0-9_]+): A MINOS INQUISITOR has spawned near \\[.*] at Coords (?<x>[0-9.-]+) (?<y>[0-9.-]+) (?<z>[0-9.-]+)").fromRepo("mythological.inquisitor_spawn.inquisitorchecker"),
	)

	private var guessLocation: NobaVec? = null

	private var inquisitors = mutableListOf<Inquisitor>()
	private var lastInquisitorId: Int? = null
	private var nearbyInquisitors = mutableListOf<OtherClientPlayerEntity>()
	private val inquisitorSpawnTimes = mutableListOf<Timestamp>()

	private var nearestWarp: BurrowWarpLocations.WarpPoint? = null
	private var lastSuggestTime = Timestamp.distantPast()

	private val validBlocks = listOf(
		Blocks.AIR,
		Blocks.OAK_LEAVES,
		Blocks.SPRUCE_LEAVES,
		Blocks.SPRUCE_FENCE,
		Blocks.SHORT_GRASS,
		Blocks.TALL_GRASS,
		Blocks.FERN,
		Blocks.POPPY,
		Blocks.DANDELION,
		Blocks.OXEYE_DAISY,
		Blocks.BLUE_ORCHID,
		Blocks.AZURE_BLUET,
		Blocks.ROSE_BUSH,
		Blocks.LILAC
	)

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		MythologicalEvents.BURROW_GUESS.register(this::onBurrowGuess)
		MythologicalEvents.BURROW_FIND.register(this::onBurrowFind)
		MythologicalEvents.MOB_DIG.register(this::onMobDig)
		EntityEvents.SPAWN.register(this::onEntitySpawn)
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onBurrowGuess(event: MythologicalEvents.BurrowGuess) {
		guessLocation = event.location
	}

	private fun onBurrowFind(event: MythologicalEvents.BurrowFind) {
		if(config.dingOnBurrowFind) SoundUtils.plingSound.play()
		if(!config.removeGuessOnBurrowFind) return

		guessLocation?.let { guess ->
			val adjustedGuess = findValidLocation(guess)
			if(adjustedGuess.distance(event.location) < 10) guessLocation = null
		}
	}

	private fun onMobDig(event: MythologicalEvents.MobDig) {
		if(!config.alertInquisitor || !DianaAPI.isActive) return
		if(event.mob == MythologicalMobs.MINOS_INQUISITOR) checkInquisitor()
	}

	private fun onEntitySpawn(event: EntityEvents.Spawn) {
		if(!config.alertInquisitor || !DianaAPI.isActive) return

		val entity = event.entity as? OtherClientPlayerEntity ?: return
		if(entity.name.string != "Minos Inquisitor") return

		lastInquisitorId = entity.id
		nearbyInquisitors.add(entity)
		checkInquisitor()
	}

	private fun onChatMessage(message: String) {
		if(!DianaAPI.isActive) return

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

		if(message == "You haven't unlocked this fast travel destination!") {
			nearestWarp?.let {
				ChatUtils.addMessage(tr("nobaaddons.events.mythological.warpNotUnlocked", "It appears as you don't have the ${it.displayName} warp location unlocked."))
				ChatUtils.addMessage(tr("nobaaddons.events.mythological.resetCommandHint", "Once you have unlocked that location, use '/noba mythological resetwarps'."), false)
				BurrowWarpLocations.lock(it)
				nearestWarp = null
			}
		}
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!DianaAPI.isActive) return

		inquisitors.removeIf { it.spawnTime.elapsedSince() > 75.seconds }
		nearbyInquisitors.removeIf { !it.isAlive || EntityUtils.getEntityById(it.id) !== it }

		suggestNearestWarp()
		renderInquisitorWaypoints(context)

		if(config.inquisitorFocusMode && inquisitors.isNotEmpty()) return

		if(config.burrowGuess) renderGuessWaypoint(context)
		if(config.findNearbyBurrows) renderBurrowWaypoints(context)
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

	private fun renderInquisitorWaypoints(context: WorldRenderContext) {
		if(nearbyInquisitors.isEmpty()) {
			val removedInquisitors = inquisitors.filter { it.location.distanceToPlayer() < 5 }
			inquisitors.removeAll(removedInquisitors)

			removedInquisitors.forEach {
				ChatUtils.addMessage(tr("nobaaddons.events.mythological.couldNotFindInquisitor", "Couldn't find ${it.spawner}'s Inquisitor."))
			}
		}

		inquisitors.forEach { inquisitor ->
			val location = inquisitor.location
			val adjustedLocation = location.center().raise()
			val yOffset = if(config.showInquisitorDespawnTime) -20f else -10f

			RenderUtils.renderWaypoint(context, location, NobaColor.DARK_RED, throughBlocks = true)
			RenderUtils.renderText(
				adjustedLocation,
				tr("nobaaddons.events.mythological.inquisitor", "Inquisitor"),
				NobaColor.DARK_RED,
				yOffset = yOffset,
				hideThreshold = 5.0,
				throughBlocks = true
			)
			RenderUtils.renderText(
				adjustedLocation,
				inquisitor.spawner,
				NobaColor.GOLD,
				yOffset = yOffset + 10f,
				hideThreshold = 5.0,
				throughBlocks = true
			)

			if(config.showInquisitorDespawnTime) {
				val formattedTime = (75 - inquisitor.spawnTime.elapsedSince().inWholeSeconds)
				RenderUtils.renderText(
					adjustedLocation,
					tr("nobaaddons.events.mythological.inquisitorDespawnsIn", "Despawns in ${formattedTime}s"),
					NobaColor.GRAY,
					hideThreshold = 5.0,
					throughBlocks = true
				)
			}
		}
	}

	private fun renderGuessWaypoint(context: WorldRenderContext) {
		guessLocation?.let {
			val adjustedLocation = findValidLocation(it)
			val distance = adjustedLocation.distanceToPlayer()

			RenderUtils.renderWaypoint(context, adjustedLocation, NobaColor.AQUA, throughBlocks = distance > 10)
			RenderUtils.renderText(
				adjustedLocation.center().raise(),
				tr("nobaaddons.events.mythological.burrowGuessWaypoint", "Burrow Guess"),
				NobaColor.AQUA,
				yOffset = -10f,
				hideThreshold = 5.0,
				throughBlocks = true
			)

			if(distance > 5) {
				val formattedDistance = distance.toInt().addSeparators()
				RenderUtils.renderText(adjustedLocation.center().raise(), "${formattedDistance}m", NobaColor.GRAY, hideThreshold = 5.0, throughBlocks = true)
			}
		}
	}

	private fun renderBurrowWaypoints(context: WorldRenderContext) {
		BurrowAPI.burrows.forEach {
			RenderUtils.renderWaypoint(context, it.location, it.type.color, throughBlocks = true)
			RenderUtils.renderText(it.location.center().raise(), it.type.displayName, it.type.color, yOffset = -5f, hideThreshold = 5.0, throughBlocks = true)
		}
	}

	private fun findValidLocation(location: NobaVec): NobaVec {
		return when {
			!location.inLoadedChunk() -> location.copy(y = LocationUtils.playerLocation.y)
			else -> findGroundLevel(location) ?: findFirstSolidBlock(location)
		}
	}

	private fun findGroundLevel(location: NobaVec): NobaVec? {
		for(y in 140 downTo 65) {
			if(location.isGroundAt(y)) return location.copy(y = y.toDouble())
		}
		return null
	}

	private fun findFirstSolidBlock(location: NobaVec): NobaVec {
		for(y in 65..140) {
			if(location.copy(y = y.toDouble()).getBlockAt() == Blocks.AIR) {
				return location.copy(y = (y - 1).toDouble())
			}
		}
		return location.copy(y = LocationUtils.playerLocation.y)
	}

	private fun NobaVec.isGroundAt(y: Int): Boolean {
		val groundBlock = copy(y = y.toDouble()).getBlockAt()
		val aboveBlock = copy(y = (y + 1).toDouble()).getBlockAt()
		return groundBlock == Blocks.GRASS_BLOCK && aboveBlock in validBlocks
	}

	private fun suggestNearestWarp() {
		if(!config.findNearestWarp || !DianaAPI.isActive) return
		if(lastSuggestTime.elapsedSince() < 1.seconds) return

		val targetLocation = getTargetLocation() ?: return
		nearestWarp = BurrowWarpLocations.getNearestWarp(targetLocation) ?: return
		lastSuggestTime = Timestamp.now()

		RenderUtils.drawTitle(tr("nobaaddons.events.mythological.warpToPoint", "Warp to ${nearestWarp?.displayName}"), NobaColor.GRAY, 2f, 30, 1.seconds)
	}

	private fun getTargetLocation(): NobaVec? =
		inquisitors.firstOrNull()?.location ?: guessLocation?.let { findValidLocation(it) }

	fun useNearestWarp() {
		if(!DianaAPI.isActive) return

		nearestWarp?.let {
			ChatUtils.queueCommand("warp ${it.warpName}")
			nearestWarp = null
		}
	}

	fun reset() {
		guessLocation = null
		inquisitors.clear()
		lastInquisitorId = null
		inquisitorSpawnTimes.clear()
		nearestWarp = null
	}

	data class Inquisitor(val spawner: String, val location: NobaVec, val spawnTime: Timestamp = Timestamp.now())
}