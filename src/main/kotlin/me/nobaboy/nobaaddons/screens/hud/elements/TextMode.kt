package me.nobaboy.nobaaddons.screens.hud.elements

import me.nobaboy.nobaaddons.utils.StringUtils.title
import net.minecraft.util.function.ValueLists

enum class TextMode {
	PURE,
	SHADOW,
	OUTLINE;

	val next by lazy { BY_ID.apply(ordinal + 1) }

	override fun toString(): String = name.title()

	companion object {
		val BY_ID = ValueLists.createIdToValueFunction(TextMode::ordinal, TextMode.entries.toTypedArray(), ValueLists.OutOfBoundsHandling.WRAP)
	}
}