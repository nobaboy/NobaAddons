package me.nobaboy.nobaaddons.utils.render

import me.nobaboy.nobaaddons.mixins.accessors.WorldRendererAccessor
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.NobaVec.Companion.toBox
import net.minecraft.client.render.Frustum
import net.minecraft.util.math.Box

object FrustumUtils {
	private val frustum: Frustum
		get() = (MCUtils.worldRenderer as WorldRendererAccessor).frustum

	fun isVisible(box: Box): Boolean = frustum.isVisible(box)

	fun isVisible(
		minX: Double, minY: Double, minZ: Double,
		maxX: Double, maxY: Double, maxZ: Double
	): Boolean {
		val box = Box(minX, minY, minZ, maxX, maxY, maxZ)
		return isVisible(box)
	}

	// TODO do I have to round here?
	fun isVisible(vec: NobaVec, toWorldHeight: Boolean = false): Boolean {
		val rounded = vec.roundToBlock()
		val maxY = if(toWorldHeight) 319.0 else vec.y + 1
		val box = Box(
			rounded.x, rounded.y, rounded.z,
			rounded.x + 1.0, maxY, rounded.z + 1.0
		)
		return isVisible(box)
	}

	fun isVisible(bounds: Pair<NobaVec, NobaVec>): Boolean {
		val box = bounds.toBox()
		return isVisible(box)
	}
}