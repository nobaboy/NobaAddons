package me.nobaboy.nobaaddons.utils.render

import me.nobaboy.nobaaddons.mixins.accessors.WorldRendererAccessor
import me.nobaboy.nobaaddons.mixins.invokers.FrustumInvoker
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.client.render.Frustum
import net.minecraft.util.math.Box

object FrustumUtils {
	fun getFrustum(): Frustum = (MCUtils.worldRenderer as WorldRendererAccessor).frustum

	fun isVisible(box: Box): Boolean {
		return getFrustum().isVisible(box)
	}
	fun isVisible(minX: Double, minY: Double, minZ: Double, maxX: Double, maxY: Double, maxZ: Double,): Boolean =
		(getFrustum() as FrustumInvoker).invokeIsVisible(minX, minY, minZ, maxX, maxY, maxZ)

	fun isVisible(vec0: NobaVec, toWorldHeight: Boolean = false): Boolean {
		val vec = vec0.roundToBlock()
		val maxY = if(toWorldHeight) 319.0 else vec.y + 1
		return isVisible(vec.x, vec.y, vec.z, vec.x + 1.0, maxY, vec.z + 1.0)
	}
}