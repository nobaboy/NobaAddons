package me.nobaboy.nobaaddons.features.ui.infobox.functions.impl

import me.nobaboy.nobaaddons.features.ui.infobox.functions.InfoBoxFunction
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.roundTo
import kotlin.math.roundToInt
import kotlin.math.sqrt

object PlayerFunctions {
	object XFunction : InfoBoxFunction<Int?> {
		override val name: String = "x"
		override val aliases: List<String> = listOf("pos_x")
		override fun execute(): Int? = MCUtils.player?.x?.roundToInt()
	}

	object YFunction : InfoBoxFunction<Int?> {
		override val name: String = "y"
		override val aliases: List<String> = listOf("pos_y")
		override fun execute(): Int? = MCUtils.player?.x?.roundToInt()
	}

	object ZFunction : InfoBoxFunction<Int?> {
		override val name: String = "z"
		override val aliases: List<String> = listOf("pos_z")
		override fun execute(): Int? = MCUtils.player?.x?.roundToInt()
	}

	object PitchFunction : InfoBoxFunction<Float?> {
		override val name: String = "pitch"
		override fun execute(): Float? = MCUtils.player?.pitch
	}

	object YawFunction : InfoBoxFunction<Float?> {
		override val name: String = "yaw"
		override fun execute(): Float? = MCUtils.player?.yaw
	}

	object BpsFunction : InfoBoxFunction<Double?> {
		override val name: String = "bps"
		override fun execute(): Double? {
			val player = MCUtils.client.player ?: return null

			val dX = player.x - player.prevX
			val dY = player.y - player.prevY
			val dZ = player.z - player.prevZ
			val bps = sqrt((dX * dX) + (dY * dY) + (dZ * dZ)) * 20

			return bps.roundTo(1)
		}
	}
}