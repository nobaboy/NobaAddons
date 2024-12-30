package me.nobaboy.nobaaddons.utils

import net.minecraft.util.Formatting
import java.awt.Color

data class NobaColor(val rgb: Int, val formatting: Formatting? = null) {
	constructor(formatting: Formatting) : this(formatting.colorValue!!, formatting)

	val red: Int get() = (rgb shr 16) and 0xFF
	val green: Int get() = (rgb shr 8) and 0xFF
	val blue: Int get() = rgb and 0xFF

	val colorCode: Char? get() = formatting?.code

	val next: NobaColor
		get() {
			val index = allColors.indexOfFirst { it == this }
			return if(index != -1) allColors[(index + 1) % allColors.size] else this
		}

	fun toColor(): Color = Color(rgb)

	companion object {
		private val allColors = mutableListOf<NobaColor>()

		val BLACK = register(NobaColor(Formatting.BLACK))
		val DARK_BLUE = register(NobaColor(Formatting.DARK_BLUE))
		val DARK_GREEN = register(NobaColor(Formatting.DARK_GREEN))
		val DARK_AQUA = register(NobaColor(Formatting.DARK_AQUA))
		val DARK_RED = register(NobaColor(Formatting.DARK_RED))
		val DARK_PURPLE = register(NobaColor(Formatting.DARK_PURPLE))
		val GOLD = register(NobaColor(Formatting.GOLD))
		val GRAY = register(NobaColor(Formatting.GRAY))
		val DARK_GRAY = register(NobaColor(Formatting.DARK_GRAY))
		val BLUE = register(NobaColor(Formatting.BLUE))
		val GREEN = register(NobaColor(Formatting.GREEN))
		val AQUA = register(NobaColor(Formatting.AQUA))
		val RED = register(NobaColor(Formatting.RED))
		val LIGHT_PURPLE = register(NobaColor(Formatting.LIGHT_PURPLE))
		val YELLOW = register(NobaColor(Formatting.YELLOW))
		val WHITE = register(NobaColor(Formatting.WHITE))

		private fun register(color: NobaColor): NobaColor {
			allColors.add(color)
			return color
		}

		fun Color.toNobaColor(): NobaColor = NobaColor(rgb)
	}
}