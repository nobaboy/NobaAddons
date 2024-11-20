package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockIslandChangeEvent
import me.nobaboy.nobaaddons.utils.BlockUtils.getBlockAt
import me.nobaboy.nobaaddons.utils.BlockUtils.inLoadedChunk
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.block.Blocks
import kotlin.math.roundToInt

// TODO: Add a focus mode for inquisitors
object MythologicalWaypoints {
	private val config get() = NobaConfigManager.config.events.mythological

	private val validBlocks = listOf(
		Blocks.AIR,
		Blocks.OAK_LEAVES,
		Blocks.BIRCH_LEAVES,
		Blocks.TALL_GRASS,
		Blocks.LILAC,
		Blocks.SUNFLOWER,
		Blocks.PEONY,
		Blocks.ROSE_BUSH,
		Blocks.POPPY,
		Blocks.DANDELION,
		Blocks.SPRUCE_FENCE
	)

	private var burrows = mutableMapOf<NobaVec, BurrowType>()

	private var guessLocation: NobaVec? = null
	private var nearestWarp: WarpLocations.WarpPoint? = null

	private var lastDugBurrow: NobaVec? = null

	fun init() {
		SkyBlockIslandChangeEvent.EVENT.register { reset() }
		MythologicalEvents.BURROW_GUESS.register(this::onBurrowGuess)
		MythologicalEvents.BURROW_FIND.register(this::onBurrowFind)
		MythologicalEvents.BURROW_DIG.register(this::onBurrowDig)
//		MythologicalEvents.INQUISITOR_SPAWN.register(this::onInquisitorSpawn)
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onBurrowGuess(event: MythologicalEvents.BurrowGuess) {
		guessLocation = event.location
		update()
	}

	private fun onBurrowFind(event: MythologicalEvents.BurrowFind) {
		burrows[event.location] = event.type
		update()
	}

	private fun onBurrowDig(event: MythologicalEvents.BurrowDig) {
		burrows.remove(event.location)
		lastDugBurrow = event.location
		update()
	}

//	private fun onInquisitorSpawn(event: MythologicalEvents.InquisitorSpawn) {
//
//	}

	private fun onChatMessage(message: String) {
		if(!isEnabled()) return

		when {
			message.startsWith("☠ You were killed by") -> {
				burrows.keys.removeIf { it == lastDugBurrow }
			}
			message == "Poof! You have cleared all your griffin burrows!" -> reset()
		}
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!isEnabled()) return

		val playerLocation = LocationUtils.playerLocation()

		suggestNearestWarp()

		if(config.findNearbyBurrows) {
			burrows.forEach { (location, type) ->
				RenderUtils.renderWaypoint(context, location, type.color, throughBlocks = true)
				RenderUtils.renderText(context, location.center().raise(), type.text, type.color, yOffset = -5.0f)
			}
		}

		if(config.burrowGuess) {
			guessLocation?.let {
				val adjustedLocation = findValidLocation(it)
				val distance = adjustedLocation.distance(playerLocation).roundToInt()

				RenderUtils.renderWaypoint(context, adjustedLocation, NobaColor.YELLOW, throughBlocks = distance > 10)
				RenderUtils.renderText(context, adjustedLocation.center().raise(), "Guess", NobaColor.YELLOW, yOffset = -10.0f)

				if(distance > 5) {
					val formattedDistance = distance.toInt().addSeparators()
					RenderUtils.renderText(context, adjustedLocation.center().raise(), "${formattedDistance}m", NobaColor.YELLOW)
				}
			}
		}
	}

	private fun update() {
		if(config.findNearbyBurrows) tryRemoveGuess()
	}

	private fun reset() {
		guessLocation = null
		burrows.clear()
	}

	// TODO: Implement title hud
	private fun suggestNearestWarp() {

	}

	private fun tryRemoveGuess() {
		guessLocation?.let { guess ->
			val adjustedGuess = findValidLocation(guess)
			if(burrows.any { adjustedGuess.distance(it.key) < 40 }) guessLocation = null
		}
	}

	private fun findValidLocation(location: NobaVec): NobaVec {
		if(!location.inLoadedChunk()) return location.copy(y = LocationUtils.playerLocation().y)

		return findGroundLevel(location) ?: findFirstSolidBelowAir(location)
	}

	private fun findGroundLevel(location: NobaVec): NobaVec? {
		fun isGround(y: Double) = location.copy(y = y).getBlockAt() == Blocks.GRASS_BLOCK &&
			location.copy(y = y + 1).getBlockAt() in validBlocks

		for(y in 140 downTo 65) {
			if(isGround(y.toDouble())) return location.copy(y = y.toDouble())
		}
		return null
	}

	private fun findFirstSolidBelowAir(location: NobaVec): NobaVec {
		for(y in 65..140) {
			if(location.copy(y = y.toDouble()).getBlockAt() == Blocks.AIR) {
				return location.copy(y = (y - 1).toDouble())
			}
		}
		return location.copy(y = LocationUtils.playerLocation().y)
	}

	fun useNearestWarp() {
		nearestWarp?.let {
			val command = "warp ${it.warpName}"
			ChatUtils.queueCommand(command)
			nearestWarp = null
		}
	}

	private fun isEnabled() = DianaAPI.isActive()
}