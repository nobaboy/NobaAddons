package me.nobaboy.nobaaddons.config.ui.elements

data class Element(
	val identifier: String,
	var x: Int,
	var y: Int,
	var scale: Float = 1.0f,
	var color: Int = 0xFFFFFF
)