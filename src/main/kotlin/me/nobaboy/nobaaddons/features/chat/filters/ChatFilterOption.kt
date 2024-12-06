package me.nobaboy.nobaaddons.features.chat.filters

import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class ChatFilterOption {
	SHOWN,
	COMPACT,
	HIDDEN;

	override fun toString(): String = name.title()
}