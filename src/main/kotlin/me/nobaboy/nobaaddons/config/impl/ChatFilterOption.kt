package me.nobaboy.nobaaddons.config.impl

import net.minecraft.client.resource.language.I18n

enum class ChatFilterOption {
	SHOWN,
	COMPACT,
	HIDDEN;

	override fun toString(): String {
		return I18n.translate("nobaaddons.config.chat.filter.chatFilterOption.$name")
	}
}