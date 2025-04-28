package me.nobaboy.nobaaddons.utils.math

import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.properties.Holding

open class BezierFitter(private val degree: Int) {
	private val fitters = Array(3) { PolynomialFitter(degree) }
	private val points = mutableListOf<NobaVec>()

	private val cachedCurve = Holding<BezierCurve>()

	val lastPoint: NobaVec? get() = points.lastOrNull()
	val isEmpty: Boolean get() = points.isEmpty()

	fun addPoint(point: NobaVec) {
		require(point.x.isFinite() && point.y.isFinite() && point.z.isFinite()) {
			"Points must not contain NaN or Infinity"
		}

		val values = point.toDoubleArray()
		val index = points.size.toDouble()

		fitters.forEachIndexed { i, fitter -> fitter.addPoint(index, values[i]) }

		points.add(point)
		cachedCurve.clear()
	}

	fun fit(): BezierCurve? {
		if(points.size <= degree) return null
		return cachedCurve.getOrSet { BezierCurve(fitters.map { it.fit() }) }
	}

	fun reset() {
		fitters.forEach { it.reset() }
		points.clear()
		cachedCurve.clear()
	}
}