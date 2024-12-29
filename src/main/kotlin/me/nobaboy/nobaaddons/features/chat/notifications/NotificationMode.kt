package me.nobaboy.nobaaddons.features.chat.notifications

import me.nobaboy.nobaaddons.utils.EnumUtils
import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class NotificationMode {
	CONTAINS,
	STARTS_WITH,
	REGEX;

	val next: NotificationMode by lazy { BY_ID.apply(ordinal + 1) }

	override fun toString(): String = name.replace("_", " ").title()

	companion object {
		val BY_ID = EnumUtils.ordinalMapper<NotificationMode>()
	}
}