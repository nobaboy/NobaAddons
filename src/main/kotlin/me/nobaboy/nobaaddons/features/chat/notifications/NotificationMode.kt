package me.nobaboy.nobaaddons.features.chat.notifications

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.EnumUtils
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class NotificationMode : NameableEnum {
	CONTAINS,
	STARTS_WITH,
	REGEX;

	val next: NotificationMode by lazy { BY_ID.apply(ordinal + 1) }

	override fun getDisplayName(): Text = when(this) {
		CONTAINS -> tr("nobaaddons.config.chat.notifications.notificationMode.contains", "Contains")
		STARTS_WITH -> tr("nobaaddons.config.chat.notifications.notificationMode.startsWith", "Starts With")
		REGEX -> tr("nobaaddons.config.chat.notifications.notificationMode.regex", "Regex")
	}

	companion object {
		val BY_ID = EnumUtils.ordinalMapper<NotificationMode>()
	}
}