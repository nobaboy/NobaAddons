package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.events.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.events.MythologicalMobs
import me.nobaboy.nobaaddons.events.EventDispatcher.Companion.registerIf
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.EntityEvents
import me.nobaboy.nobaaddons.events.impl.interact.BlockInteractionEvent
import me.nobaboy.nobaaddons.events.impl.interact.GenericInteractEvent
import me.nobaboy.nobaaddons.events.impl.interact.ItemUseEvent
import me.nobaboy.nobaaddons.events.impl.render.ParticleEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.RegexUtils.firstPartialMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.onPartialMatch
import me.nobaboy.nobaaddons.utils.TextUtils.aqua
import me.nobaboy.nobaaddons.utils.TextUtils.gold
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.toShortString
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyBlockId
import me.nobaboy.nobaaddons.utils.math.ParticlePathFitter
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.sound.SoundUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.particle.ParticleTypes
import kotlin.time.Duration.Companion.seconds

object MythologicalWaypoints {
	private val config get() = NobaConfig.events.mythological

	private val INQUISITOR_DEAD_REGEX by Regex("(?<username>[A-z0-9_]+): Inquisitor dead!").fromRepo("mythological.inquisitor_dead")
	private val INQUISITOR_SPAWN_REGEXES by Repo.list(
		Regex("(?<username>[A-z0-9_]+): [Xx]: (?<x>[0-9.-]+),? [Yy]: (?<y>[0-9.-]+),? [Zz]: (?<z>[0-9.-]+).*").fromRepo("mythological.inquisitor_spawn.coords"),
		Regex("(?<username>[A-z0-9_]+): A MINOS INQUISITOR has spawned near \\[.*] at Coords (?<x>[0-9.-]+) (?<y>[0-9.-]+) (?<z>[0-9.-]+)").fromRepo("mythological.inquisitor_spawn.inquisitorchecker"),
	)

	private val burrows = mutableMapOf<NobaVec, BurrowType>()

	private val particlePath = ParticlePathFitter(3)
	private var guessLocation: NobaVec? = null

	private var lastAbilityUse = Timestamp.distantPast()

	private var inquisitors = mutableListOf<Inquisitor>()
	private val inquisitorSpawnTimes = mutableListOf<Timestamp>()
	private var nearbyInquisitors = mutableListOf<OtherClientPlayerEntity>()
	private var lastInquisitor: OtherClientPlayerEntity? = null

	private var nearestWarp: BurrowWarpLocations.WarpPoint? = null
	private var lastSuggestTime = Timestamp.distantPast()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		MythologicalEvents.BURROW_FIND.register(this::onBurrowFind)
		MythologicalEvents.BURROW_DIG.register(this::onBurrowDig)
		MythologicalEvents.MOB_DIG.register(this::onMobDig)
		ParticleEvents.PARTICLE.register(this::onParticle)
		ItemUseEvent.EVENT.register(this::onItemUse)
		BlockInteractionEvent.EVENT.registerIf<BlockInteractionEvent.Interact>(this::onItemUse)
		EntityEvents.SPAWN.register(this::onEntitySpawn)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onBurrowFind(event: MythologicalEvents.BurrowFind) {
		event.location.takeIf { it !in burrows }?.let { burrows[it] = event.type }

		if(config.dingOnBurrowFind) SoundUtils.plingSound.play()
		if(config.removeGuessOnBurrowFind) guessLocation = null
	}

	private fun onBurrowDig(event: MythologicalEvents.BurrowDig) {
		burrows.remove(event.location)
		if(config.removeGuessOnBurrowFind) guessLocation = null
	}

	private fun onMobDig(event: MythologicalEvents.MobDig) {
		if(!config.alertInquisitor || !DianaAPI.isActive) return
		if(event.mob == MythologicalMobs.MINOS_INQUISITOR) checkInquisitor()
	}

	private fun onParticle(event: ParticleEvents.Particle) {
		if(!config.burrowGuess || !DianaAPI.isActive) return
		if(event.type != ParticleTypes.DRIPPING_LAVA) return
		if(event.speed != -0.5f || event.count != 2) return
		if(lastAbilityUse.elapsedSince() > 3.seconds) return

		val location = event.location
		val lastPoint = particlePath.lastPoint
		if(lastPoint != null && lastPoint.distanceSq(location) > 9) return

		particlePath.addPoint(location)
		guessLocation = particlePath.solve()?.lower(0.5)?.roundToBlock() ?: return
	}

	private fun onItemUse(event: GenericInteractEvent) {
		if(!config.burrowGuess || !DianaAPI.isActive) return
		if(lastAbilityUse.elapsedSince() <= 3.seconds) return
		if(event.itemInHand.skyBlockId != DianaAPI.SPADE) return

		particlePath.reset()
		lastAbilityUse = Timestamp.now()
	}

	private fun onEntitySpawn(event: EntityEvents.Spawn) {
		if(!config.alertInquisitor || !DianaAPI.isActive) return

		val entity = event.entity as? OtherClientPlayerEntity ?: return
		if(entity.name.string != "Minos Inquisitor") return

		nearbyInquisitors.add(entity)
		lastInquisitor = entity
		checkInquisitor()
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!DianaAPI.isActive) return

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

		inquisitors.removeIf { it.timestamp.isPast() }
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
		lastInquisitor?.takeIf { it.isAlive }?.let {
			val (x, y, z) = it.getNobaVec().roundToBlock().toDoubleArray()
			val message = "x: $x, y: $y, z: $z | Minos Inquisitor at [ ${SkyBlockAPI.prefixedZone} ]"
			config.announceChannel.send(message)
		}
	}

	private fun renderInquisitorWaypoints(context: WorldRenderContext) {
		if(nearbyInquisitors.isEmpty()) {
			val removedInquisitors = inquisitors.filter { it.location.center().distanceToPlayer() < 5 }
			inquisitors.removeAll(removedInquisitors)

			removedInquisitors.forEach {
				ChatUtils.addMessage(tr("nobaaddons.events.mythological.couldNotFindInquisitor", "Couldn't find ${it.spawner}'s Inquisitor."))
			}
		}

		inquisitors.forEach {
			val location = it.location
			val adjustedLocation = location.center().raise()
			val yOffset = if(config.showInquisitorDespawnTime) -20f else -10f

			RenderUtils.renderWaypoint(context, location, NobaColor.DARK_RED, throughBlocks = true)
			RenderUtils.renderText(
				context,
				adjustedLocation,
				tr("nobaaddons.events.mythological.inquisitor", "Inquisitor"),
				color = NobaColor.DARK_RED,
				yOffset = yOffset,
				hideThreshold = 5.0,
				throughBlocks = true
			)
			RenderUtils.renderText(
				context,
				adjustedLocation,
				it.spawner,
				color = NobaColor.GOLD,
				yOffset = yOffset + 10f,
				hideThreshold = 5.0,
				throughBlocks = true
			)

			if(config.showInquisitorDespawnTime) {
				RenderUtils.renderText(
					context,
					adjustedLocation,
					tr("nobaaddons.events.mythological.inquisitorDespawnsIn", "Despawns in ${it.remainingTime}"),
					color = NobaColor.GRAY,
					hideThreshold = 5.0,
					throughBlocks = true
				)
			}
		}
	}

	private fun renderGuessWaypoint(context: WorldRenderContext) {
		if(guessLocation in burrows) return

		guessLocation?.let {
			val adjustedLocation = it.center().raise()
			val distance = it.center().distanceToPlayer()
			val formattedDistance = distance.toInt().addSeparators()

			RenderUtils.renderWaypoint(context, it, NobaColor.AQUA, throughBlocks = distance > 10)
			RenderUtils.renderText(
				context,
				adjustedLocation,
				tr("nobaaddons.events.mythological.burrowGuessWaypoint", "Burrow Guess"),
				color = NobaColor.AQUA,
				yOffset = -10f,
				hideThreshold = 5.0,
				throughBlocks = true
			)
			RenderUtils.renderText(
				context,
				adjustedLocation,
				"${formattedDistance}m",
				color = NobaColor.GRAY,
				hideThreshold = 5.0,
				throughBlocks = true
			)
		}
	}

	private fun renderBurrowWaypoints(context: WorldRenderContext) {
		burrows.forEach { (location, type) ->
			val text = if(location == guessLocation) {
				type.displayName.copy().append(" (Guess)".toText().aqua())
			} else {
				type.displayName
			}

			RenderUtils.renderWaypoint(context, location, type.color, throughBlocks = true)
			RenderUtils.renderText(
				context,
				location.center().raise(),
				text,
				color = type.color,
				yOffset = -5f,
				hideThreshold = 5.0,
				throughBlocks = true
			)
		}
	}

	private fun suggestNearestWarp() {
		if(!config.findNearestWarp || !DianaAPI.isActive) return
		if(lastSuggestTime.elapsedSince() < 1.seconds) return

		val targetLocation = getTargetLocation() ?: return
		nearestWarp = BurrowWarpLocations.getNearestWarp(targetLocation) ?: return
		lastSuggestTime = Timestamp.now()

		RenderUtils.drawTitle(tr("nobaaddons.events.mythological.warpToPoint", "Warp to ${nearestWarp?.displayName}"), NobaColor.GRAY, 2f, 30, 1.seconds)
	}

	private fun getTargetLocation(): NobaVec? = inquisitors.firstOrNull()?.location ?: guessLocation

	fun useNearestWarp() {
		if(!DianaAPI.isActive) return

		nearestWarp?.let {
			ChatUtils.queueCommand("warp ${it.warpName}")
			nearestWarp = null
		}
	}

	fun reset() {
		burrows.clear()
		particlePath.reset()
		guessLocation = null
		inquisitors.clear()
		inquisitorSpawnTimes.clear()
		nearbyInquisitors.clear()
		lastInquisitor = null
		nearestWarp = null
	}

	private data class Inquisitor(
		val spawner: String,
		val location: NobaVec,
		val timestamp: Timestamp = Timestamp.now() + 75.seconds
	) {
		val remainingTime: String get() = timestamp.timeRemaining().toShortString()
	}
}