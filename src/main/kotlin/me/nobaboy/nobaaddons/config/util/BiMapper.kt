package me.nobaboy.nobaaddons.config.util

import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaColor.Companion.toNobaColor
import java.awt.Color

/**
 * Interface providing a translation layer between [A] and [B], where [A] is the value stored in a
 * [kotlin.reflect.KMutableProperty], and [B] is the target value for use in YACL.
 */
interface BiMapper<A, B> {
	fun to(a: A): B
	fun from(b: B): A

	object NobaAWTColorMapper : BiMapper<NobaColor, Color> {
		override fun to(a: NobaColor): Color = a.toColor()
		override fun from(b: Color): NobaColor = b.toNobaColor()
	}
}