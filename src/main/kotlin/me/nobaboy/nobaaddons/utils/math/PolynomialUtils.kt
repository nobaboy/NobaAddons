package me.nobaboy.nobaaddons.utils.math

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
}