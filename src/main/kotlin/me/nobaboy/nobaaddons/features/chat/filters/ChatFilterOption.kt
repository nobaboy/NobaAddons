package me.nobaboy.nobaaddons.features.chat.filters

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class ChatFilterOption : NameableEnum {
	SHOWN,
	COMPACT,
	HIDDEN;

	val enabled: Boolean get() = this != SHOWN

	override fun getDisplayName(): Text = when(this) {
		SHOWN -> tr("nobaaddons.label.chatFilterOption.shown", "Shown")
		COMPACT -> tr("nobaaddons.label.chatFilterOption.compact", "Compact")
		HIDDEN -> tr("nobaaddons.label.chatFilterOption.hidden", "Hidden")
	}
}