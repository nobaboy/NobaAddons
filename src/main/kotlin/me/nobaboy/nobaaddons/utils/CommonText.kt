package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.TextUtils.red

object CommonText {
	val NOBAADDONS get() = tr("nobaaddons.name", "NobaAddons")

	val SCREEN_OPEN get() = tr("nobaaddons.screen.button.open", "Open")
	val SCREEN_DELETE get() = tr("nobaaddons.screen.button.delete", "Delete")

	object Config {
		val ENABLED get() = tr("nobaaddons.config.enabled", "Enabled")

		val ENTITY_OVERLAY_IRIS_CONFLICT get() = tr(
			"nobaaddons.config.highlightIrisConflict",
			"This feature currently conflicts with Iris, and may not render correctly"
		).red()

		val ALERT_COLOR get() = tr("nobaaddons.config.alertColor", "Alert Color")
		val HIGHLIGHT_COLOR get() = tr("nobaaddons.config.highlightColor", "Highlight Color")

		val NOTIFICATION_SOUND get() = tr("nobaaddons.config.notificationSound", "Notification Sound")

		val LABEL_MISC get() = tr("nobaaddons.config.label.miscellaneous", "Miscellaneous")
		val LABEL_CRIMSON_ISLE get() = tr("nobaaddons.config.label.crimsonIsle", "Crimson Isle")
		val LABEL_MOBS get() = tr("nobaaddons.config.label.mobs", "Mobs")
		val LABEL_ITEM_ABILITIES get() = tr("nobaaddons.config.label.itemAbilities", "Item Abilities")
		val LABEL_RIFT get() = tr("nobaaddons.config.label.rift", "Rift")
		val LABEL_DUNGEONS get() = tr("nobaaddons.config.label.dungeons", "Dungeons")

		fun seconds(value: Int) = tr("nobaaddons.config.label.seconds", "$value seconds")
	}
}