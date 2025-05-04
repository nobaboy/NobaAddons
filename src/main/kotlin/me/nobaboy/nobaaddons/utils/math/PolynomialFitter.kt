package me.nobaboy.nobaaddons.utils.math

import kotlin.math.pow

/**
 * This is taken from SkyHanni, which is licensed under the LGPL-2.1.
 *
 * [Original source](https://github.com/hannibal002/SkyHanni/blob/beta/src/main/java/at/hannibal2/skyhanni/utils/PolynomialFitter.kt#L6-L30)
 */
class PolynomialFitter(private val degree: Int) {
	private val xMatrix = ArrayList<DoubleArray>()
	private val yMatrix = ArrayList<DoubleArray>()

	fun addPoint(x: Double, y: Double) {
		xMatrix.add(DoubleArray(degree + 1) { i -> x.pow(i) })
		yMatrix.add(doubleArrayOf(y))
	}

	fun fit(): DoubleArray {
		val x = Matrix(xMatrix.toTypedArray())
		val y = Matrix(yMatrix.toTypedArray())
		val coefficients = (x.transpose() * x).inverse() * x.transpose() * y
		return coefficients.transpose()[0]
	}

	fun reset() {
		xMatrix.clear()
		yMatrix.clear()
	}
}