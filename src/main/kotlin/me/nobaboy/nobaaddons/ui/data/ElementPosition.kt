package me.nobaboy.nobaaddons.ui.data

import kotlinx.serialization.Serializable

/**
 * Basic HUD element positioning data used by [me.nobaboy.nobaaddons.ui.HudElement]
 */
@Serializable
data class ElementPosition(
	var x: Double = 0.0,
	var y: Double = 0.0,
	var scale: Float = 1f,
)