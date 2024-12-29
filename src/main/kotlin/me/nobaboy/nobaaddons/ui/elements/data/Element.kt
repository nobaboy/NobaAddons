package me.nobaboy.nobaaddons.ui.elements.data

import com.google.gson.annotations.Expose

data class Element(
	@Expose val identifier: String,
	@Expose var x: Int,
	@Expose var y: Int,
	@Expose var scale: Float = 1.0f,
	@Expose var color: Int = 0xFFFFFF
)