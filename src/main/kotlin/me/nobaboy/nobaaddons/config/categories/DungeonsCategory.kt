package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.requires
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr

object DungeonsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.dungeons", "Dungeons")) {
		// region Highlight Starred Mobs
		buildGroup(tr("nobaaddons.config.dungeons.highlightStarredMobs", "Highlight Starred Mobs")) {
			val enabled = boolean(
				CommonText.Config.ENABLED,
				default = defaults.dungeons.highlightStarredMobs.enabled,
				property = config.dungeons.highlightStarredMobs::enabled
			)
			color(
				CommonText.Config.HIGHLIGHT_COLOR,
				default = defaults.dungeons.highlightStarredMobs.highlightColor,
				property = config.dungeons.highlightStarredMobs::highlightColor
			) requires enabled
			cycler(
				tr("nobaaddons.config.dungeons.highlightStarredMobs.highlightMode", "Highlight Mode"),
				default = defaults.dungeons.highlightStarredMobs.highlightMode,
				property = config.dungeons.highlightStarredMobs::highlightMode
			) requires enabled
		}
		// endregion

		// region Simon Says Timer
		buildGroup(tr("nobaaddons.config.dungeons.simonSaysTimer", "Simon Says Timer")) {
			val enabled = boolean(
				CommonText.Config.ENABLED,
				default = defaults.dungeons.simonSaysTimer.enabled,
				property = config.dungeons.simonSaysTimer::enabled
			)
			boolean(
				tr("nobaaddons.config.dungeons.simonSaysTimer.timeInPartyChat", "Send Time in Party Chat"),
				tr("nobaaddons.config.dungeons.simonSaysTimer.timeInPartyChat.tooltip", "Sends your Simon Says device completion time in party chat"),
				default = defaults.dungeons.simonSaysTimer.timeInPartyChat,
				property = config.dungeons.simonSaysTimer::timeInPartyChat
			) requires enabled
		}
		// endregion
	}
}