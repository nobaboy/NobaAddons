package me.nobaboy.nobaaddons.utils.math

import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NobaVec.Companion.toNobaVec
import me.nobaboy.nobaaddons.utils.math.PolynomialUtils.derivativeValueAt
import me.nobaboy.nobaaddons.utils.math.PolynomialUtils.valueAt

class BezierCurve(private val coefficients: List<DoubleArray>) {
	init {
		require(coefficients.size == 3) { "Expected 3D curve coefficients (x, y, z)" }
	}

	fun at(t: Double): NobaVec = coefficients.map { it.valueAt(t) }.toNobaVec()
	fun derivativeAt(t: Double): NobaVec = coefficients.map { it.derivativeValueAt(t) }.toNobaVec()
}