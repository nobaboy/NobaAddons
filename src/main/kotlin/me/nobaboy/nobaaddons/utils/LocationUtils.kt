package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.render.FrustumUtils
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.hit.HitResult
import net.minecraft.util.math.Box
import net.minecraft.world.RaycastContext
import kotlin.math.max
import kotlin.math.min

object LocationUtils {
	fun playerLocation() = MCUtils.player?.getNobaVec() ?: NobaVec()
	fun playerCoords(): String {
		MCUtils.player!!.let {
			val (x, y, z) = listOf(it.x, it.y, it.z).map { it.toInt() }
			return "x: $x, y: $y, z: $z"
		}
	}

	fun blockBelowPlayer() = playerLocation().roundToBlock().lower()

	fun NobaVec.distanceToPlayer() = distance(playerLocation())
	fun NobaVec.distanceSqToPlayer() = distanceSq(playerLocation())

	fun NobaVec.distanceToPlayerIgnoreY() = distanceIgnoreY(playerLocation())
	fun NobaVec.distanceToPlayerSqIgnoreY() = distanceSqIgnoreY(playerLocation())

	fun Entity.distanceToPlayer() = getNobaVec().distanceToPlayer()

	fun Entity.distanceTo(vec: NobaVec) = getNobaVec().distance(vec)
	fun Entity.distanceToIgnoreY(vec: NobaVec) = getNobaVec().distanceIgnoreY(vec)

	fun PlayerEntity.eyeLocation(oldVersion: Boolean = true): NobaVec {
		val eyePos = if(isSneaking) {
			if(oldVersion) 1.54 else 1.27
		} else {
			1.62
		}

		return getNobaVec().add(y = eyePos)
	}

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

	fun Box.minBox() = NobaVec(minX, minY, minZ)
	fun Box.maxBox() = NobaVec(maxX, maxY, maxZ)

	fun Box.rayIntersects(origin: NobaVec, direction: NobaVec): Boolean {
		val rayDirectionInverse = direction.inverse()
		val t1 = (minBox() - origin) * rayDirectionInverse
		val t2 = (maxBox() - origin) * rayDirectionInverse

		val tMin = max(t1.minOfEach(t2).max(), Double.NEGATIVE_INFINITY)
		val tMax = min(t1.maxOfEach(t2).min(), Double.NEGATIVE_INFINITY)
		return tMax >= tMin && tMax >= 0.0
	}

	fun Box.union(boxes: List<Box>?): Box? {
		if(boxes.isNullOrEmpty()) {
			return null
		}

		var minX = this.minX
		var minY = this.minY
		var minZ = this.minZ
		var maxX = this.maxX
		var maxY = this.maxY
		var maxZ = this.maxZ

		boxes.forEach { box ->
			if(box.minX < minX) minX = box.minX
			if(box.minY < minY) minY = box.minY
			if(box.minZ < minZ) minZ = box.minZ
			if(box.maxX > maxX) maxX = box.maxX
			if(box.maxY > maxY) maxY = box.maxY
			if(box.maxZ > maxZ) maxZ = box.maxZ
		}

		return Box(minX, minY, minZ, maxX, maxY, maxZ)
	}

	fun Box.dimensions() = maxBox() - minBox()

	fun Box.center() = dimensions() * 0.5 + minBox()

	fun Box.topCenter() = center().raise((maxY - minY) / 2)

	fun Box.clampTo(other: Box): Box {
		val minX = max(this.minX, other.minX)
		val minY = max(this.minY, other.minY)
		val minZ = max(this.minZ, other.minZ)
		val maxX = min(this.maxX, other.maxX)
		val maxY = min(this.maxY, other.maxY)
		val maxZ = min(this.maxZ, other.maxZ)
		return Box(minX, minY, minZ, maxX, maxY, maxZ)
	}
}