package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.NumberUtils.round
import net.minecraft.entity.Entity
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Box
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

data class NobaVec(
	val x: Double,
	val y: Double,
	val z: Double
) {
	constructor() : this(0.0, 0.0, 0.0)
	constructor(x: Int, y: Int, z: Int) : this(x.toDouble(), y.toDouble(), z.toDouble())
	constructor(x: Float, y: Float, z: Float) : this(x.toDouble(), y.toDouble(), z.toDouble())

	fun toBlockPos(): BlockPos = BlockPos(x.toInt(), y.toInt() ,z.toInt())
	fun toVec3d(): Vec3d = Vec3d(x, y, z)
	fun toBox(): Box {
		val vec = this.roundToBlock()
		return Box(vec.x, vec.y, vec.z, vec.x + 1.0, vec.y + 1.0, vec.z + 1.0)
	}

	fun distance(other: NobaVec, center: Boolean = true): Double = sqrt(distanceSq(other, center))
	fun distance(x: Double, y: Double, z: Double): Double = distance(NobaVec(x, y, z), center = false)

	fun distanceSq(x: Double, y: Double, z: Double): Double = distanceSq(NobaVec(x, y, z), center = false)
	fun distanceSq(other: NobaVec, center: Boolean = true): Double {
		var vec = this
		if(center) vec = center()

		val dx = other.x - vec.x
		val dy = other.y - vec.y
		val dz = other.z - vec.z
		return dx * dx + dy * dy + dz * dz
	}

	fun distanceIgnoreY(other: NobaVec): Double = sqrt(distanceSqIgnoreY(other))
	fun distanceSqIgnoreY(other: NobaVec): Double {
		val dx = other.x - x
		val dz = other.z - z
		return dx * dx + dz * dz
	}

	operator fun plus(other: NobaVec) = NobaVec(x + other.x, y + other.y, z + other.z)
	operator fun minus(other: NobaVec) = NobaVec(x - other.x, y - other.y, z - other.z)

	operator fun times(other: NobaVec) = NobaVec(x * other.x, y * other.y, z * other.z)
	operator fun times(scalar: Double) = NobaVec(x * scalar, y * scalar, z * scalar)
	operator fun times(scalar: Int) = NobaVec(x * scalar, y * scalar, z * scalar)

	operator fun div(other: NobaVec) = NobaVec(x / other.x, y / other.y, z / other.z)
	operator fun div(scalar: Double) = NobaVec(x / scalar, y / scalar, z / scalar)

	fun add(x: Int = 0, y: Int = 0, z: Int = 0): NobaVec = NobaVec(this.x + x, this.y + y, this.z + z)
	fun add(x: Double = 0.0, y: Double = 0.0, z: Double = 0.0): NobaVec =
		NobaVec(this.x + x, this.y + y, this.z + z)

	override fun toString() = "NobaVec{x=$x, y=$y, z=$z}"

	fun dot(other: NobaVec): Double = (x * other.x) + (y * other.y) + (z * other.z)

    fun cosAngle(other: NobaVec) = this.normalize().dot(other.normalize())
    fun radianAngle(other: NobaVec) = acos(cosAngle(other))
    fun degreeAngle(other: NobaVec) = Math.toDegrees(radianAngle(other))

    fun normalize() = length().let { NobaVec(x / it, y / it, z / it) }
    fun inverse() = NobaVec(1.0 / x, 1.0 / y, 1.0 / z)

    fun min() = min(x, min(y, z))
    fun max() = max(x, max(y, z))

    fun minOfEach(other: NobaVec) = NobaVec(min(x, other.x), min(y, other.y), min(z, other.z))
    fun maxOfEach(other: NobaVec) = NobaVec(max(x, other.x), max(y, other.y), max(z, other.z))

    fun formatWithAccuracy(accuracy: Int, splitChar: String = " "): String {
        return if(accuracy == 0) {
            val x = kotlin.math.round(x).toInt()
            val y = kotlin.math.round(y).toInt()
            val z = kotlin.math.round(z).toInt()
            "$x$splitChar$y$splitChar$z"
        } else {
            val x = (kotlin.math.round(x * accuracy) / accuracy)
            val y = (kotlin.math.round(y * accuracy) / accuracy)
            val z = (kotlin.math.round(z * accuracy) / accuracy)
            "$x$splitChar$y$splitChar$z"
        }
    }

    fun toCleanString(): String = "$x $y $z"

    fun lengthSquared(): Double = x * x + y * y + z * z
    fun length(): Double = sqrt(this.lengthSquared())

    fun isZero(): Boolean = x == 0.0 && y == 0.0 && z == 0.0

    fun clone(): NobaVec = NobaVec(x, y, z)

    fun toDoubleArray(): Array<Double> = arrayOf(x, y, z)
    fun toFloatArray(): Array<Float> = arrayOf(x.toFloat(), y.toFloat(), z.toFloat())

    fun equalsIgnoreY(other: NobaVec) = x == other.x && z == other.z

    override fun equals(other: Any?): Boolean {
        if(this === other) return true

        return (other as? NobaVec)?.let {
            x == it.x && y == it.y && z == it.z
        } ?: super.equals(other)
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    fun round(decimals: Int) = NobaVec(x.round(decimals), y.round(decimals), z.round(decimals))

    fun roundToBlock(): NobaVec {
        val x = (x - .499999).round(0)
        val y = (y - .499999).round(0)
        val z = (z - .499999).round(0)
        return NobaVec(x, y, z)
    }

    fun roundLocation(): NobaVec {
        val x = if(x < 0) x.toInt() - 1 else x.toInt()
        val y = y.toInt() - 1
        val z = if(z < 0) z.toInt() - 1 else z.toInt()
        return NobaVec(x, y, z)
    }

    fun interpolate(other: NobaVec, factor: Double): NobaVec {
        require(factor in 0.0..1.0) { "Factor must be between 0 and 1: $factor" }

        val x = (1 - factor) * this.x + factor * other.x
        val y = (1 - factor) * this.y + factor * other.y
        val z = (1 - factor) * this.z + factor * other.z

        return NobaVec(x, y, z)
    }

    fun slope(other: NobaVec, factor: Double) = this + (other - this).scale(factor)
    fun scale(scalar: Double): NobaVec = NobaVec(scalar * x, scalar * y, scalar * z)

	fun toBox(other: NobaVec) = Box(x, y, z, other.x, other.y, other.z)
	fun toBoxWithOffset(offsetX: Double, offsetY: Double, offsetZ: Double) =
		Box(x, y, z, x + offsetX, y + offsetY, z + offsetZ)

	fun raise(offset: Number = 1): NobaVec = copy(y = y + offset.toDouble())
	fun lower(offset: Number = 1): NobaVec = copy(y = y - offset.toDouble())

	fun center(): NobaVec = roundToBlock().add(x = 0.5, y = 0.5, z = 0.5)

	fun middle(other: NobaVec): NobaVec = plus(other.minus(this) / 2)

	fun negate() = NobaVec(-x, -y, -z)

	fun rotateXY(theta: Double) = NobaVec(x * cos(theta) - y * sin(theta), x * sin(theta) + y * cos(theta), z)
	fun rotateXZ(theta: Double) = NobaVec(x * cos(theta) + z * sin(theta), y, -x * sin(theta) + z * cos(theta))
	fun rotateYZ(theta: Double) = NobaVec(x, y * cos(theta) - z * sin(theta), y * sin(theta) + z * cos(theta))

	fun nearestPointOnLine(start: NobaVec, end: NobaVec): NobaVec {
		var direction = end - start
		val toPoint = this - start

		val dotProduct = direction.lengthSquared()
		var t = 0.0
		if(dotProduct != t) t = (toPoint.dot(direction) / dotProduct).coerceIn(0.0, 1.0)

		direction *= t
		direction += start
		return direction
	}

	fun distanceToLine(startPos: NobaVec, endPos: NobaVec): Double {
		return (nearestPointOnLine(startPos, endPos) - this).lengthSquared()
	}

	fun crossProduct(other: NobaVec): NobaVec {
		val crossX = this.y * other.z - this.z * other.y
		val crossY = this.z * other.x - this.x * other.z
		val crossZ = this.x * other.y - this.y * other.x
		return NobaVec(crossX, crossY, crossZ)
	}

	fun offsetBy(other: Vec3d): NobaVec = NobaVec(x + other.x, y + other.y, z + other.z)

	private operator fun div(i: Number): NobaVec = NobaVec(x / i.toDouble(), y / i.toDouble(), z / i.toDouble())

	companion object {
		fun fromYawPitch(yaw: Double, pitch: Double): NobaVec {
			val yawRad = (yaw + 90) * Math.PI / 180
			val pitchRad = (pitch + 90) * Math.PI / 180

			val x = sin(pitchRad) * cos(yawRad)
			val y = sin(pitchRad) * sin(yawRad)
			val z = cos(pitchRad)
			return NobaVec(x, z, y)
		}

		// Format: "x:y:z"
		fun fromString(string: String): NobaVec {
			val (x, y, z) = string.split(":").map { it.toDouble() }
			return NobaVec(x, y, z)
		}

		fun blockBelowPlayer() = LocationUtils.playerLocation().roundToBlock().add(y = -1.0)

		val expandVector = NobaVec(0.0020000000949949026, 0.0020000000949949026, 0.0020000000949949026)
	}
}

fun BlockPos.toNobaVec(): NobaVec = NobaVec(x, y, z)

fun BlockHitResult.toNobaVec(): NobaVec = pos.toNobaVec()

fun Entity.getNobaVec(): NobaVec = NobaVec(x, y, z)
fun Entity.getPrevNobaVec(): NobaVec = NobaVec(prevX, prevY, prevZ)
fun Entity.getMotionNobaVec(): NobaVec = NobaVec(movement.x, movement.y, movement.z)

fun Vec3d.toNobaVec(): NobaVec = NobaVec(x, y, z)
fun Vec3i.toNobaVec(): NobaVec = NobaVec(x, y, z)

fun ParticleS2CPacket.toNobaVec() = NobaVec(x, y, z)

fun Array<Double>.toNobaVec(): NobaVec = NobaVec(this[0], this[1], this[2])

fun Box.expand(vec: NobaVec): Box = this.expand(vec.x, vec.y, vec.z)
fun Box.expand(amount: Double): Box = this.expand(amount, amount, amount)
