package me.nobaboy.nobaaddons.features.chat.filters

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text

enum class ChatFilterOption : NameableEnum {
	SHOWN,
	COMPACT,
	HIDDEN;

	override fun getDisplayName(): Text = name.title().toText()
}