package me.nobaboy.nobaaddons.api.skyblock.events.mythological

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.client.SoundEvents
import me.nobaboy.nobaaddons.events.impl.render.ParticleEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.Identifier
import kotlin.math.E
import kotlin.math.abs
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sin

/**
 * This is taken, ported and adapted from SoopyV2 which is licensed under the GPL-3.0.
 *
 * [Original source](https://github.com/Soopyboo32/SoopyV2/blob/master/src/features/events/index.js#L499-L753)
 *
 * This should've been in [BurrowAPI], but since it's a separate module I'll keep it as is
 */
object BurrowGuessAPI {
	private val config get() = NobaConfig.events.mythological
	private val enabled: Boolean get() = config.burrowGuess && DianaAPI.isActive

	private var dingIndex = 0
	private var hasDinged = false
	private var lastDingPitch = 0f
	private var firstPitch = 0f
	private var lastParticlePoint: NobaVec? = null
	private var lastParticlePoint2: NobaVec? = null
	private var firstParticlePoint: NobaVec? = null
	private var particlePoint: NobaVec? = null
	private var guessPoint: NobaVec? = null

	private var lastSoundPoint: NobaVec? = null
	private var locs = mutableListOf<NobaVec>()

	private var dingSlope = mutableListOf<Float>()

	private var distance: Double? = null
	private var distance2: Double? = null

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		SoundEvents.SOUND.register(this::onSound)
		ParticleEvents.PARTICLE.register(this::onParticle)
	}

	private fun onSound(event: SoundEvents.Sound) {
		if(!enabled) return
		if(event.id != Identifier.ofVanilla("block.note_block.harp")) return

		val pitch = event.pitch

		if(!hasDinged) firstPitch = pitch
		hasDinged = true

		if(pitch < lastDingPitch) {
			firstPitch = pitch
			dingIndex = 0
			dingSlope.clear()
			lastDingPitch = pitch
			lastParticlePoint = null
			lastParticlePoint2 = null
			lastSoundPoint = null
			firstParticlePoint = null
			distance = null
			locs.clear()
		}

		if(lastDingPitch == 0f) {
			lastDingPitch = pitch
			distance = null
			lastParticlePoint = null
			lastParticlePoint2 = null
			lastSoundPoint = null
			firstParticlePoint = null
			locs.clear()
			return
		}

		dingIndex++

		if(dingIndex > 1) dingSlope.add(pitch - lastDingPitch)
		if(dingSlope.size > 20) dingSlope.removeFirst()
		val slope = if(dingSlope.isNotEmpty()) dingSlope.reduce { a, b -> a + b }.toDouble() / dingSlope.size else 0.0

		val location = event.location

		lastSoundPoint = location
		lastDingPitch = pitch

		if(lastParticlePoint2 == null || particlePoint == null || firstParticlePoint == null) return

		distance2 = E / slope - firstParticlePoint?.distance(location)!!

		if(distance2!! > 1000) {
			distance2 = null
			guessPoint = null
			return
		}

		val lineDist = lastParticlePoint2?.distance(particlePoint!!)!!
		distance = distance2!!

		val changesHelp = particlePoint?.minus(lastParticlePoint2!!)!!
		val changes =  NobaVec(
			changesHelp.x / lineDist,
			changesHelp.y / lineDist,
			changesHelp.z / lineDist
		)

		lastSoundPoint?.let {
			guessPoint = NobaVec(
				it.x + changes.x * distance!!,
				it.y + changes.y * distance!!,
				it.z + changes.z * distance!!
			)
		}
	}

	private fun onParticle(event: ParticleEvents.Particle) {
		if(!enabled) return
		if(event.type != ParticleTypes.DRIPPING_LAVA) return

		val location = event.location

		lastSoundPoint?.let {
			if(abs(location.x - it.x) >= 2 || abs(location.y - it.y) >= 0.5 || abs(location.z - it.z) >= 2) return
		}

		if(locs.size < 100 && locs.isEmpty() || locs.last().distance(location) != 0.0) {
			var distMultiplier = 1.0
			if(locs.size > 2) {
				val predictedDist = 0.06507 * locs.size + 0.259
				val lastPos = locs.last()
				val actualDist = location.distance(lastPos)
				distMultiplier = actualDist / predictedDist
			}

			locs.add(location)

			if(locs.size > 5 && guessPoint != null) {
				val slopeThing = locs.zipWithNext { a, b ->
					atan((a.x - b.x) / (a.z - b.z))
				}

				val (a, b, c) = solveEquationThing(
					NobaVec(slopeThing.size - 5, slopeThing.size - 3, slopeThing.size - 1),
					NobaVec(
						slopeThing[slopeThing.size - 5],
						slopeThing[slopeThing.size - 3],
						slopeThing[slopeThing.size - 1]
					)
				)

				val pr1 = mutableListOf<NobaVec>()
				val pr2 = mutableListOf<NobaVec>()

				val start = slopeThing.size - 1
				val lastPos = locs[start].toDoubleArray()
				val lastPos2 = locs[start].toDoubleArray()

				var distCovered = 0.0

				val ySpeed = locs[locs.size - 1].x - locs[locs.size - 2].x / hypot(
					locs[locs.size - 1].x - locs[locs.size - 2].x,
					locs[locs.size - 1].z - locs[locs.size - 2].x
				)

				var i = start + 1
				while(distCovered < distance2!! && i < 10000) {
					val y = b / (i + a) + c

					val dist = distMultiplier * (0.06507 * i + 0.259)

					val xOff = dist * sin(y)
					val zOff = dist * cos(y)

					val density = 5

					for(j in 0..density) {
						lastPos[0] += xOff / density
						lastPos[1] += ySpeed * dist / density
						lastPos[2] += zOff / density

						lastPos2[0] -= xOff / density
						lastPos2[1] += ySpeed * dist / density
						lastPos2[2] -= zOff / density

						pr1.add(lastPos.toNobaVec())
						pr2.add(lastPos2.toNobaVec())

						lastSoundPoint?.let {
							distCovered = hypot(lastPos[0] - it.x, lastPos[2] - it.z)
						}

						if(distCovered > distance2!!) break
					}

					i++
				}

				if(pr1.isEmpty()) return

				val p1 = pr1.last()
				val p2 = pr2.last()

				guessPoint?.let {
					val d1 = ((p1.x - it.x).times(2 + (p1.z - it.z))).pow(2)
					val d2 = ((p2.x - it.x).times(2 + (p2.z - it.z))).pow(2)

					val point = if(d1 < d2) p1 else p2
					val finalLocation = NobaVec(floor(point.x), 255.0, floor(point.z))

					MythologicalEvents.BURROW_GUESS.invoke(MythologicalEvents.BurrowGuess(finalLocation))
				}
			}
		}

		if(lastParticlePoint == null) firstParticlePoint = location.clone()

		lastParticlePoint2 = lastParticlePoint
		lastParticlePoint = particlePoint

		particlePoint = location.clone()

		if(lastParticlePoint2 == null || firstParticlePoint == null || distance2 == null || lastSoundPoint == null) return

		val lineDist = lastParticlePoint2?.distance(particlePoint!!)!!
		distance = distance2!!

		val changesHelp = particlePoint?.minus(lastParticlePoint2!!)!!
		val changes =  NobaVec(
			changesHelp.x / lineDist,
			changesHelp.y / lineDist,
			changesHelp.z / lineDist
		)

		lastSoundPoint?.let {
			guessPoint = NobaVec(
				it.x + changes.x * distance!!,
				it.y + changes.y * distance!!,
				it.z + changes.z * distance!!
			)
		}
	}

	private fun solveEquationThing(x: NobaVec, y: NobaVec): NobaVec {
		val a = (-y.x * x.y * x.x - y.y * x.y * x.z + y.y * x.y * x.x + x.y * x.z * y.z + x.x * x.z * y.x - x.x * x.z * y.z) / (x.y * y.x - x.y * y.z + x.x * y.z - y.x * x.z + y.y * x.z - y.y * x.x)
		val b = (y.x - y.y) * (x.x + a) * (x.y + a) / (x.y - x.x)
		val c = y.x - b / (x.x + a)

		return NobaVec(a, b, c)
	}

	private fun reset() {
		hasDinged = false
		lastDingPitch = 0f
		firstPitch = 0f
		lastParticlePoint = null
		lastParticlePoint2 = null
		lastSoundPoint = null
		firstParticlePoint = null
		particlePoint = null
		guessPoint = null
		distance = null
		dingIndex = 0
		dingSlope.clear()
	}
}