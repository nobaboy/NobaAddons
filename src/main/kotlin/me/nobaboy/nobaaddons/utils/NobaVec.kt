package me.nobaboy.nobaaddons.utils

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import me.nobaboy.nobaaddons.utils.serializers.NobaVecKSerializer
import net.minecraft.entity.Entity
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.math.acos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Serializable(with = NobaVecKSerializer::class)
data class NobaVec(
	val x: Double,
	val y: Double,
	val z: Double,
) {
	constructor() : this(0.0, 0.0, 0.0)
	constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())
	constructor(x: Float, y: Float, z: Float) : this(x.toDouble(), y.toDouble(), z.toDouble())

	fun toBlockPos(): BlockPos = BlockPos(x.toInt(), y.toInt(), z.toInt())
	fun toVec3d(): Vec3d = Vec3d(x, y, z)
	fun toBox(): Box = roundToBlock().let { Box(it.x, it.y, it.z, it.x + 1, it.y + 1, it.z + 1) }

	fun distance(other: NobaVec, center: Boolean = false): Double = sqrt(distanceSq(other, center))
	fun distanceSq(other: NobaVec, center: Boolean = false): Double {
		val vec = if(center) center() else this

		val dx = other.x - vec.x
		val dy = other.y - vec.y
		val dz = other.z - vec.z

		return dx * dx + dy * dy + dz * dz
	}

	fun distanceIgnoreY(other: NobaVec, center: Boolean = false): Double = sqrt(distanceSqIgnoreY(other, center = center))
	fun distanceSqIgnoreY(other: NobaVec, center: Boolean = false): Double {
		val vec = if(center) center() else this

		val dx = other.x - vec.x
		val dz = other.z - vec.z

		return dx * dx + dz * dz
	}

	fun distance(x: Double, y: Double, z: Double): Double = distance(NobaVec(x, y, z), center = false)
	fun distanceSq(x: Double, y: Double, z: Double): Double = distanceSq(NobaVec(x, y, z), center = false)
	fun distanceIgnoreY(x: Double, z: Double): Double = distanceIgnoreY(NobaVec(x, 0.0, z), center = false)
	fun distanceSqIgnoreY(x: Double, z: Double): Double = distanceSqIgnoreY(NobaVec(x, 0.0, z), center = false)

	operator fun plus(other: NobaVec) = NobaVec(x + other.x, y + other.y, z + other.z)
	operator fun minus(other: NobaVec) = NobaVec(x - other.x, y - other.y, z - other.z)

	operator fun times(other: NobaVec) = NobaVec(x * other.x, y * other.y, z * other.z)
	operator fun times(scalar: Number) = NobaVec(x * scalar.toDouble(), y * scalar.toDouble(), z * scalar.toDouble())

	operator fun div(other: NobaVec) = NobaVec(x / other.x, y / other.y, z / other.z)
	operator fun div(scalar: Number) = NobaVec(x / scalar.toDouble(), y / scalar.toDouble(), z / scalar.toDouble())

	fun dot(other: NobaVec): Double = (x * other.x) + (y * other.y) + (z * other.z)
	fun crossProduct(other: NobaVec) = NobaVec(
		y * other.z - z * other.y,
		z * other.x - x * other.z,
		x * other.y - y * other.x
	)

	fun normalize() = length().let { if(it == 0.0) this else this / it }

	fun length(): Double = sqrt(lengthSquared())
	fun lengthSquared(): Double = x * x + y * y + z * z
	fun isZero(): Boolean = x == 0.0 && y == 0.0 && z == 0.0

	fun cosAngle(other: NobaVec) = normalize().dot(other.normalize())
	fun radianAngle(other: NobaVec) = acos(cosAngle(other))
	fun degreeAngle(other: NobaVec) = Math.toDegrees(radianAngle(other))

	fun inverse() = NobaVec(1.0 / x, 1.0 / y, 1.0 / z)

	fun min() = min(x, min(y, z))
	fun max() = max(x, max(y, z))

	fun minOfEach(other: NobaVec) = NobaVec(min(x, other.x), min(y, other.y), min(z, other.z))
	fun maxOfEach(other: NobaVec) = NobaVec(max(x, other.x), max(y, other.y), max(z, other.z))

	fun roundTo(decimals: Int) = NobaVec(x.roundTo(decimals), y.roundTo(decimals), z.roundTo(decimals))
	fun roundToBlock(): NobaVec {
		val x = (x - .499999).roundTo(0)
		val y = (y - .499999).roundTo(0)
		val z = (z - .499999).roundTo(0)
		return NobaVec(x, y, z)
	}

	fun center(): NobaVec = add(x = 0.5, y = 0.5, z = 0.5)
	fun add(x: Number = 0, y: Number = 0, z: Number = 0): NobaVec =
		NobaVec(this.x + x.toDouble(), this.y + y.toDouble(), this.z + z.toDouble())

	fun interpolate(other: NobaVec, factor: Double): NobaVec {
		require(factor in 0.0..1.0) { "Factor must be between 0 and 1: $factor" }

		val x = (1 - factor) * x + factor * other.x
		val y = (1 - factor) * y + factor * other.y
		val z = (1 - factor) * z + factor * other.z

		return NobaVec(x, y, z)
	}

	fun distanceToLine(start: NobaVec, end: NobaVec): Double = (nearestPointOnLine(start, end) - this).length()
	fun nearestPointOnLine(start: NobaVec, end: NobaVec): NobaVec {
		val direction = end - start
		val t = ((this - start).dot(direction) / direction.lengthSquared()).coerceIn(0.0, 1.0)
		return start + direction * t
	}

	fun raise(offset: Number = 1): NobaVec = copy(y = y + offset.toDouble())
	fun lower(offset: Number = 1): NobaVec = copy(y = y - offset.toDouble())
//	fun offsetBy(vec: Vec3d): NobaVec = NobaVec(x + vec.x, y + vec.y, z + vec.z)

	fun formatWithAccuracy(accuracy: Int, splitChar: String = " "): String =
		if(accuracy == 0) listOf(x, y, z).joinToString(splitChar) { it.roundToInt().toString() }
		else listOf(x, y, z).joinToString(splitChar) { ((it * accuracy).roundToInt() / accuracy.toDouble()).toString() }

	fun toDoubleArray(): Array<Double> = arrayOf(x, y, z)
	fun toFloatArray(): Array<Float> = arrayOf(x.toFloat(), y.toFloat(), z.toFloat())

	override fun toString(): String = "NobaVec{x=$x, y=$y, z=$z}"

	companion object {
		fun List<Double>.toNobaVec(): NobaVec {
			require(size == 3) { "Expected list of 3 elements to convert to NobaVec" }
			return NobaVec(this[0], this[1], this[2])
		}

		val expandVector = NobaVec(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026)
	}
}

fun BlockPos.toNobaVec(): NobaVec = NobaVec(x, y, z)
fun BlockHitResult.toNobaVec(): NobaVec = pos.toNobaVec()

fun Entity.getNobaVec(): NobaVec = NobaVec(x, y, z)
// unused; don't feel like stonecutter-ing this as such
//fun Entity.getPrevNobaVec(): NobaVec = NobaVec(prevX, prevY, prevZ)
//fun Entity.getMotionNobaVec(): NobaVec = NobaVec(movement.x, movement.y, movement.z)

fun Vec3d.toNobaVec(): NobaVec = NobaVec(x, y, z)
fun Vec3i.toNobaVec(): NobaVec = NobaVec(x, y, z)

fun ParticleS2CPacket.toNobaVec(): NobaVec = NobaVec(x, y, z)

fun Array<Double>.toNobaVec(): NobaVec = NobaVec(this[0], this[1], this[2])

fun Box.expand(vec: NobaVec): Box = this.expand(vec.x, vec.y, vec.z)