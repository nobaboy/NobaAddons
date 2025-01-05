package me.nobaboy.nobaaddons.ui

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.EnumUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class TextShadow : NameableEnum {
	NONE,
	SHADOW,
	OUTLINE;

	val next: TextShadow by lazy { BY_ID.apply(ordinal + 1) }

	override fun getDisplayName(): Text = when(this) {
		NONE -> tr("nobaaddons.label.textShadow.none", "None")
		SHADOW -> tr("nobaaddons.label.textShadow.shadow", "Shadow")
		OUTLINE -> tr("nobaaddons.label.textShadow.outline", "Outline")
	}

	companion object {
		val BY_ID = EnumUtils.ordinalMapper<TextShadow>()
	}
}