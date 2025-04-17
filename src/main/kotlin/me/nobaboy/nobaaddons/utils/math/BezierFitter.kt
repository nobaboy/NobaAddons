package me.nobaboy.nobaaddons.utils.math

import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NobaVec.Companion.toNobaVec
import me.nobaboy.nobaaddons.utils.math.PolynomialUtils.derivativeValueAt
import me.nobaboy.nobaaddons.utils.math.PolynomialUtils.valueAt

open class BezierFitter(private val degree: Int) {
	private val fitters = List(3) { PolynomialFitter(degree) }
	val points = mutableListOf<NobaVec>()

	private var cachedCurve: BezierCurve? = null

	fun addPoint(point: NobaVec) {
		require(point.x.isFinite() && point.y.isFinite() && point.z.isFinite()) {
			"Points must not contain NaN or Infinity"
		}

		val values = point.toDoubleArray()
		val index = points.size.toDouble()

		fitters.forEachIndexed { i, fitter -> fitter.addPoint(index, values[i]) }

		points.add(point)
		cachedCurve = null
	}

	fun fit(): BezierCurve? {
		if(points.size <= degree) return null
		return cachedCurve ?: BezierCurve(fitters.map { it.fit() }).also { cachedCurve = it }
	}

	fun reset() {
		fitters.forEach { it.reset() }
		points.clear()
		cachedCurve = null
	}
}

class BezierCurve(private val coefficients: List<DoubleArray>) {
	init {
		require(coefficients.size == 3) { "Expected 3D curve coefficients (x, y, z)" }
	}

	fun at(t: Double): NobaVec = coefficients.map { it.valueAt(t) }.toNobaVec()

	fun derivativeAt(t: Double): NobaVec = coefficients.map { it.derivativeValueAt(t) }.toNobaVec()
}