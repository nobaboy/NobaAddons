package me.nobaboy.nobaaddons.config.controllers.infobox

data class InfoBox(
	var text: String,
	val identifier: String,
	val x: Int,
	val y: Int,
	val scale: Double
)