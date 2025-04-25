package me.nobaboy.nobaaddons.utils

import kotlinx.serialization.Serializable
import net.minecraft.util.Formatting
import net.minecraft.util.math.ColorHelper
import java.awt.Color

@JvmInline
@Serializable
value class NobaColor(val rgb: Int) {
	constructor(formatting: Formatting) : this(formatting.colorValue!!)

	val red: Int get() = (rgb shr 16) and 0xFF
	val green: Int get() = (rgb shr 8) and 0xFF
	val blue: Int get() = rgb and 0xFF

	val formatting: Formatting? get() = Formatting.entries.firstOrNull { it.colorValue == rgb }
	val colorCode: Char? get() = formatting?.code

	val next: NobaColor
		get() {
			val index = allColors.indexOfFirst { it == this }
			return if(index != -1) allColors[(index + 1) % allColors.size] else this
		}

	fun toJavaColor(): Color = Color(rgb)

	fun withAlpha(alpha: Int): Int = ColorHelper./*? if <1.21.2 {*//*Argb.*//*?}*/withAlpha(alpha, rgb)

	companion object {
		private val allColors: List<NobaColor> = buildList {
			Formatting.entries.forEach { add(NobaColor(it.colorValue ?: return@forEach)) }
		}

		val BLACK = NobaColor(Formatting.BLACK)
		val DARK_BLUE = NobaColor(Formatting.DARK_BLUE)
		val DARK_GREEN = NobaColor(Formatting.DARK_GREEN)
		val DARK_AQUA = NobaColor(Formatting.DARK_AQUA)
		val DARK_RED = NobaColor(Formatting.DARK_RED)
		val DARK_PURPLE = NobaColor(Formatting.DARK_PURPLE)
		val GOLD = NobaColor(Formatting.GOLD)
		val GRAY = NobaColor(Formatting.GRAY)
		val DARK_GRAY = NobaColor(Formatting.DARK_GRAY)
		val BLUE = NobaColor(Formatting.BLUE)
		val GREEN = NobaColor(Formatting.GREEN)
		val AQUA = NobaColor(Formatting.AQUA)
		val RED = NobaColor(Formatting.RED)
		val LIGHT_PURPLE = NobaColor(Formatting.LIGHT_PURPLE)
		val YELLOW = NobaColor(Formatting.YELLOW)
		val WHITE = NobaColor(Formatting.WHITE)

		fun Formatting.toNobaColor(): NobaColor = NobaColor(this)
		fun Color.toNobaColor(): NobaColor = NobaColor(rgb)
	}
}