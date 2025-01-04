package me.nobaboy.nobaaddons.features.chat.notifications

import me.nobaboy.nobaaddons.utils.EnumUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class NotificationMode(val displayName: Text) {
	CONTAINS(tr("nobaaddons.config.chat.notifications.notificationMode.contains", "Contains")),
	STARTS_WITH(tr("nobaaddons.config.chat.notifications.notificationMode.startsWith", "Starts With")),
	REGEX(tr("nobaaddons.config.chat.notifications.notificationMode.regex", "Regex"));

	val next: NotificationMode by lazy { BY_ID.apply(ordinal + 1) }

	companion object {
		val BY_ID = EnumUtils.ordinalMapper<NotificationMode>()
	}
}