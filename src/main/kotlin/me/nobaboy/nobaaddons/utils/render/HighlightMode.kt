package me.nobaboy.nobaaddons.utils.render

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text

enum class HighlightMode : NameableEnum {
	OUTLINE,
	FILLED,
	FILLED_OUTLINE;

	override fun getDisplayName(): Text = name.replace("_", " ").title().toText()
}