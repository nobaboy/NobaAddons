package me.nobaboy.nobaaddons.ui.data

data class ElementBounds(
	val x: Int,
	val y: Int,
	val width: Int,
	val height: Int
) {
	constructor(x: Int, y: Int, size: Pair<Int, Int>) : this(x, y, size.first, size.second)
}