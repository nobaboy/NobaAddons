package me.nobaboy.nobaaddons.config.ui.elements

data class Element(
	val identifier: String,
	var x: Int,
	var y: Int,
	var scale: Double = 1.0,
	var color: Int = 0xFFFFFF
)