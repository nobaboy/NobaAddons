package me.nobaboy.nobaaddons.utils.math

import me.nobaboy.nobaaddons.utils.NobaVec
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object PolynomialUtils {
	fun DoubleArray.valueAt(t: Double): Double {
		return reversed().fold(0.0) { accumulator, coefficient -> accumulator * t + coefficient }
	}

	fun DoubleArray.derivativeValueAt(t: Double): Double {
		val reversed = reversedArray().dropLast(1)
		return reversed.foldIndexed(0.0) { i, accumulator, coefficient ->
			accumulator * t + coefficient * (reversed.size - i)
		}
	}

	fun calculatePitchWeight(derivative: NobaVec): Double {
		val adjustedPitch = invertPitchMapping(derivative)
		return sqrt(24 * sin(adjustedPitch - Math.PI) + 25)
	}

	fun invertPitchMapping(derivative: NobaVec): Double {
		val xzMagnitude = sqrt(derivative.x * derivative.x + derivative.z * derivative.z)
		val targetPitch = -atan2(derivative.y, xzMagnitude)

		var lowerBound = -Math.PI / 2
		var upperBound = Math.PI / 2
		var midpoint: Double

		repeat(100) {
			midpoint = (lowerBound + upperBound) / 2
			val testPitch = atan2(sin(midpoint) - 0.75, cos(midpoint))

			when {
				testPitch < targetPitch -> lowerBound = midpoint
				testPitch > targetPitch -> upperBound = midpoint
				else -> return midpoint
			}
		}

		return (lowerBound + upperBound) / 2
	}
}