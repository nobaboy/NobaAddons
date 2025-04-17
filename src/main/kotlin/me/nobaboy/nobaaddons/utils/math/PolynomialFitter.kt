package me.nobaboy.nobaaddons.utils.math

import kotlin.math.pow

class PolynomialFitter(private val degree: Int) {
	private val xMatrix = mutableListOf<DoubleArray>()
	private val yMatrix = mutableListOf<DoubleArray>()

	fun addPoint(x: Double, y: Double) {
		xMatrix.add(DoubleArray(degree + 1) { i -> x.pow(i) })
		yMatrix.add(doubleArrayOf(y))
	}

	fun fit(): DoubleArray {
		val x = Matrix(xMatrix.toTypedArray())
		val y = Matrix(xMatrix.toTypedArray())
		val coefficients = (x.transpose() * x).inverse() * x.transpose() * y
		return coefficients.transpose()[0]
	}

	fun reset() {
		xMatrix.clear()
		yMatrix.clear()
	}
}