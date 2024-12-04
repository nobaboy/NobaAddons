package me.nobaboy.nobaaddons.screens.hud.elements

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text
import net.minecraft.util.function.ValueLists

enum class TextMode : NameableEnum {
	PURE,
	SHADOW,
	OUTLINE;

	val next by lazy { BY_ID.apply(ordinal + 1) }

	override fun getDisplayName(): Text = name.title().toText()

	companion object {
		val BY_ID = ValueLists.createIdToValueFunction(TextMode::ordinal, TextMode.entries.toTypedArray(), ValueLists.OutOfBoundsHandling.WRAP)
	}
}