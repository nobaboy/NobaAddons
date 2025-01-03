package me.nobaboy.nobaaddons.utils.render

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class HighlightMode : NameableEnum {
	OUTLINE,
	FILLED,
	FILLED_OUTLINE;

	override fun getDisplayName(): Text = when(this) {
		OUTLINE -> tr("nobaaddons.label.highlightMode.outline", "Outline")
		FILLED -> tr("nobaaddons.label.highlightMode.filled", "Filled")
		FILLED_OUTLINE -> tr("nobaaddons.label.highlightMode.filledOutline", "Filled Outline")
	}
}