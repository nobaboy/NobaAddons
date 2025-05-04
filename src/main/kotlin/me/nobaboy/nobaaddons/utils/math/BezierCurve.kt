package me.nobaboy.nobaaddons.utils.math

import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NobaVec.Companion.toNobaVec
import me.nobaboy.nobaaddons.utils.math.PolynomialUtils.derivativeValueAt
import me.nobaboy.nobaaddons.utils.math.PolynomialUtils.valueAt

/**
 * This is taken from SkyHanni, which is licensed under the LGPL-2.1.
 *
 * [Original source](https://github.com/hannibal002/SkyHanni/blob/beta/src/main/java/at/hannibal2/skyhanni/utils/PolynomialFitter.kt#L88-L114)
 */
@JvmInline
value class BezierCurve(private val coefficients: List<DoubleArray>) {
	init {
		require(coefficients.size == 3) { "Expected 3D curve coefficients (x, y, z)" }
	}

	fun at(t: Double): NobaVec = coefficients.map { it.valueAt(t) }.toNobaVec()
	fun derivativeAt(t: Double): NobaVec = coefficients.map { it.derivativeValueAt(t) }.toNobaVec()
}