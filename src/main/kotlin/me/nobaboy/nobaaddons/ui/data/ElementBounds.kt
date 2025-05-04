package me.nobaboy.nobaaddons.ui.data

/**
 * Element bounds, primarily used by the HUD editor screen to restrict the element from being moved off the screen
 */
data class ElementBounds(
	val x: Int,
	val y: Int,
	val width: Int,
	val height: Int,
) {
	constructor(x: Int, y: Int, size: Pair<Int, Int>) : this(x, y, size.first, size.second)
}