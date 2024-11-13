package me.nobaboy.nobaaddons.screens.hud.elements

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text

enum class TextMode : NameableEnum {
	PURE,
	SHADOW,
	OUTLINE;

	override fun getDisplayName(): Text = name.title().toText()
}