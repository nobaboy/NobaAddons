package me.nobaboy.nobaaddons.utils.mc

import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.render.FrustumUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.world.RaycastContext

object LocationUtils {
	val playerLocation: NobaVec get() = MCUtils.player?.getNobaVec() ?: NobaVec()

	fun playerCoords(): String {
		MCUtils.player!!.let {
			val (x, y, z) = listOf(it.x, it.y, it.z).map { it.toInt() }
			return "x: $x, y: $y, z: $z"
		}
	}

	fun blockBelowPlayer(): NobaVec = playerLocation.roundToBlock().lower()

	fun Entity.distanceToPlayer(): Double = getNobaVec().distanceToPlayer()
	fun NobaVec.distanceToPlayer(): Double = distance(playerLocation)
	fun NobaVec.distanceSqToPlayer(): Double = distanceSq(playerLocation)

	fun NobaVec.distanceToPlayerIgnoreY(): Double = distanceIgnoreY(playerLocation)
	fun NobaVec.distanceToPlayerSqIgnoreY(): Double = distanceSqIgnoreY(playerLocation)

	fun Entity.distanceTo(vec: NobaVec): Double = getNobaVec().distance(vec)
	fun Entity.distanceToIgnoreY(vec: NobaVec): Double = getNobaVec().distanceIgnoreY(vec)

	fun PlayerEntity.eyeLocation(oldVersion: Boolean = true): NobaVec {
		val eyePos = if(isSneaking) {
			if(oldVersion) 1.54 else 1.27
		} else {
			1.62
		}

		return getNobaVec().add(y = eyePos)
	}

	// TODO Figure out a way to treat non-full blocks as full blocks
	fun PlayerEntity.rayCast(maxDistance: Double, tickDelta: Float, includeFluids: Boolean): HitResult? {
		val player = MCUtils.player ?: return null

		val startPos = player.eyeLocation().toVec3d()
		val direction = player.getRotationVec(tickDelta)
		val endPos = startPos.add(direction.x * maxDistance, direction.y * maxDistance, direction.z * maxDistance)

		val fluidHandling = if(includeFluids) RaycastContext.FluidHandling.ANY else RaycastContext.FluidHandling.NONE
		val raycastContext = RaycastContext(startPos, endPos, RaycastContext.ShapeType.OUTLINE, fluidHandling, this)
		return MCUtils.world?.raycast(raycastContext)
	}

	fun NobaVec.canBeSeen(radius: Double = 150.0): Boolean {
		val a = MCUtils.player?.eyeLocation() ?: return false
		val b = this
		val notTooFar = a.distance(b) < radius
		val inFov = FrustumUtils.isVisible(b)
		return notTooFar && inFov
	}

	fun NobaVec.canBeSeen(yOffsetRange: IntRange, radius: Double = 150.0): Boolean =
		yOffsetRange.any { offset -> raise(offset).canBeSeen(radius) }
}