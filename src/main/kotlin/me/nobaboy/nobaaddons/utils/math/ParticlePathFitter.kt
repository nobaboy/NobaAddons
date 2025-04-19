package me.nobaboy.nobaaddons.utils.math

import me.nobaboy.nobaaddons.utils.NobaVec

class ParticlePathFitter(degree: Int) : BezierFitter(degree) {
	fun solve(): NobaVec? {
		val curve = fit() ?: return null
		val derivative = curve.derivativeAt(0.0)

		val distance = PolynomialUtils.calculatePitchWeight(derivative)
		val t = 3 * distance / derivative.length()

		return curve.at(t)
	}
}