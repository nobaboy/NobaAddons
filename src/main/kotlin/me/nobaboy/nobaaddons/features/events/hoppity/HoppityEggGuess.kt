package me.nobaboy.nobaaddons.features.events.hoppity

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.events.hoppity.HoppityAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.events.HoppityData
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.InteractEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.render.ParticleEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToPlayer
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyBlockId
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.particle.ParticleTypes
import kotlin.math.abs
import kotlin.time.Duration.Companion.seconds

object HoppityEggGuess {
	private val config get() = NobaConfig.events.hoppity
	private val enabled: Boolean get() = config.eggGuess && HoppityAPI.isActive

	private const val MAX_STEPS = 2500
	private const val STEP = 0.1

	private var lastAbilityUse = Timestamp.distantPast()
	private val particleLocations = mutableListOf<NobaVec>()
	private var guessLocation: NobaVec? = null

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		TickEvents.everySecond { onSecondPassed() }
		ParticleEvents.PARTICLE.register(this::onParticle)
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
		InteractEvents.USE_ITEM.register(this::onUseItem)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderWaypoints)
	}

	private fun onSecondPassed() {
		if(!enabled) return
		if(lastAbilityUse.elapsedSince() > 3.seconds) particleLocations.clear()
	}

	private fun onParticle(event: ParticleEvents.Particle) {
		if(!enabled) return
		if(event.type != ParticleTypes.HAPPY_VILLAGER) return
		if(event.speed != 0f || event.count != 1) return
		if(lastAbilityUse.elapsedSince() > 3.seconds) return

		val location = event.location
		particleLocations.add(location)
		if(particleLocations.size < 3) return

		val timeSteps = particleLocations.indices.map { it.toDouble() }
		val xCurve = fitParabola(timeSteps, particleLocations.map { it.x })
		val yCurve = fitParabola(timeSteps, particleLocations.map { it.y })
		val zCurve = fitParabola(timeSteps, particleLocations.map { it.z })

		guessLocation = predictFutureLocation(xCurve, yCurve, zCurve)
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return
		if(message.startsWith("HOPPITY'S HUNT You found a Chocolate")) guessLocation = null
	}

	private fun onUseItem(event: InteractEvents.UseItem) {
		if(!enabled) return

		val itemId = event.itemInHand.skyBlockId ?: return
		if(itemId != HoppityAPI.LOCATOR) return

		lastAbilityUse = Timestamp.now()
		return
	}

	private fun renderWaypoints(context: WorldRenderContext) {
		if(!enabled) return

		guessLocation?.let {
			val distance = it.distanceToPlayer()

			RenderUtils.renderWaypoint(context, it, NobaColor.AQUA, throughBlocks = true)
			RenderUtils.renderText(context, it.center().raise(), "Egg Guess", color = NobaColor.AQUA, yOffset = -10f, throughBlocks = true)

			if(distance > 5) {
				val formattedDistance = distance.toInt().addSeparators()
				RenderUtils.renderText(context, it.center().raise(), "${formattedDistance}m", color = NobaColor.GRAY, hideThreshold = 5.0, throughBlocks = true)
			}
		}
	}

	private fun fitParabola(time: List<Double>, values: List<Double>): Triple<Double, Double, Double> {
		val sizeT = time.size.toDouble()

		val sumT1 = time.sum()
		val sumT2 = time.sumOf { it * it }
		val sumT3 = time.sumOf { it * it * it }
		val sumT4 = time.sumOf { it * it * it * it }

		val sumV = values.sum()
		val sumTV = time.zip(values).sumOf { it.first * it.second }
		val sumT2V = time.zip(values).sumOf { it.first * it.first * it.second }

		val matrix = arrayOf(
			doubleArrayOf(sumT4, sumT3, sumT2),
			doubleArrayOf(sumT3, sumT2, sumT1),
			doubleArrayOf(sumT2, sumT1, sizeT)
		)
		val vector = doubleArrayOf(sumT2V, sumTV, sumV)

		val coefficients = solveLinearSystem(matrix, vector)
		return Triple(coefficients[0], coefficients[1], coefficients[2])
	}

	private fun solveLinearSystem(matrix: Array<DoubleArray>, vector: DoubleArray): DoubleArray {
		val size = matrix.size
		val augmentedMatrix = Array(size) { i -> matrix[i] + doubleArrayOf(vector[i]) }

		repeat(size) { i ->
			val maxRow = (i until size).maxByOrNull { abs(augmentedMatrix[it][i]) } ?: i
			augmentedMatrix[i] = augmentedMatrix[maxRow].also { augmentedMatrix[maxRow] = augmentedMatrix[i] }

			for(k in i + 1 until size) {
				val factor = augmentedMatrix[k][i] / augmentedMatrix[i][i]
				for(j in i ..size) {
					augmentedMatrix[k][j] -= factor * augmentedMatrix[i][j]
				}
			}
		}

		val result = DoubleArray(size)
		for(i in size - 1 downTo 0) {
			result[i] = augmentedMatrix[i][size] / augmentedMatrix[i][i]
			for(k in 0 until i) {
				augmentedMatrix[k][size] -= augmentedMatrix[k][i] * result[i]
			}
		}

		return result
	}

	private fun predictFutureLocation(
		xCurve: Triple<Double, Double, Double>,
		yCurve: Triple<Double, Double, Double>,
		zCurve: Triple<Double, Double, Double>,
	): NobaVec? {
		val eggLocations = HoppityData.getEggsByIsland(SkyBlockAPI.currentIsland) ?: return null
		var closestLocation: NobaVec? = null
		var closestDistance = Double.MAX_VALUE

		repeat(MAX_STEPS) { i ->
			val time = i * STEP

			val x = xCurve.first * time * time + xCurve.second * time + xCurve.third
			val y = yCurve.first * time * time + yCurve.second * time + yCurve.third
			val z = zCurve.first * time * time + zCurve.second * time + zCurve.third
			val currentPoint = NobaVec(x, y, z)

			for(eggLocation in eggLocations) {
				val distance = currentPoint.distance(eggLocation)
				if(distance < closestDistance) {
					closestDistance = distance
					closestLocation = eggLocation
				}
			}
		}

		return closestLocation
	}

	private fun reset() {
		particleLocations.clear()
		guessLocation = null
	}
}