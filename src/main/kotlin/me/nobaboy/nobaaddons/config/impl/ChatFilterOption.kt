package me.nobaboy.nobaaddons.config.impl

import net.minecraft.client.resource.language.I18n

enum class ChatFilterOption {
	SHOWN,
	COMPACT,
	ACTION_BAR,
	HIDDEN;

	override fun toString(): String {
		return I18n.translate("config.chat.filter.chatFilterOption.$name")
	}
}