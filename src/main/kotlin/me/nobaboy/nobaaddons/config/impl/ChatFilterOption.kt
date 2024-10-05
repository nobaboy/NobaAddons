package me.nobaboy.nobaaddons.config.impl

import net.minecraft.client.resource.language.I18n

enum class ChatFilterOption {
    SHOWN,
    ACTION_BAR,
    COMPACT,
    HIDDEN;

    override fun toString(): String {
        return I18n.translate("config.chat.filter.chatFilterOption.$name")
    }
}