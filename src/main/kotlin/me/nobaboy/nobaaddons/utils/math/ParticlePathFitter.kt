package me.nobaboy.nobaaddons.utils.math

import me.nobaboy.nobaaddons.utils.NobaVec

/**
 * This is taken from SkyHanni, which is licensed under the LGPL-2.1.
 *
 * [Original source](https://github.com/hannibal002/SkyHanni/blob/beta/src/main/java/at/hannibal2/skyhanni/utils/PolynomialFitter.kt#L73-L86)
 */
class ParticlePathFitter(degree: Int) : BezierFitter(degree) {
	fun solve(): NobaVec? {
		val curve = fit() ?: return null
		val derivative = curve.derivativeAt(0.0)

		val distance = PolynomialUtils.calculatePitchWeight(derivative)
		val t = 3 * distance / derivative.length()

		return curve.at(t)
	}
}