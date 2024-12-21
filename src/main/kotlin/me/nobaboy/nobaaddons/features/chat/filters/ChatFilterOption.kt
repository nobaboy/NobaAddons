package me.nobaboy.nobaaddons.features.chat.filters

import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class ChatFilterOption {
	SHOWN,
	COMPACT,
	HIDDEN;

	val enabled: Boolean get() = this != SHOWN

	override fun toString(): String = name.title()
}