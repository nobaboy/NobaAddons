package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.utils.CommonText
import net.minecraft.text.Text

object DungeonsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.dungeons"))

			// region Highlight Starred Mobs
			.buildGroup(Text.translatable("nobaaddons.config.dungeons.highlightStarredMobs")) {
				boolean(
					CommonText.Config.ENABLED,
					default = defaults.dungeons.highlightStarredMobs.enabled,
					property = config.dungeons.highlightStarredMobs::enabled
				)
				color(
					Text.translatable("nobaaddons.config.dungeons.highlightStarredMobs.highlightColor"),
					default = defaults.dungeons.highlightStarredMobs.highlightColor,
					property = config.dungeons.highlightStarredMobs::highlightColor
				)
				cycler(
					Text.translatable("nobaaddons.config.dungeons.highlightStarredMobs.highlightMode"),
					default = defaults.dungeons.highlightStarredMobs.highlightMode,
					property = config.dungeons.highlightStarredMobs::highlightMode
				)
			}
			// endregion

			// region Simon Says Timer
			.buildGroup(Text.translatable("nobaaddons.config.dungeons.simonSaysTimer")) {
				boolean(
					CommonText.Config.ENABLED,
					default = defaults.dungeons.simonSaysTimer.enabled,
					property = config.dungeons.simonSaysTimer::enabled
				)
				boolean(
					Text.translatable("nobaaddons.config.dungeons.simonSaysTimer.timeInPartyChat"),
					Text.translatable("nobaaddons.config.dungeons.simonSaysTimer.timeInPartyChat.tooltip"),
					default = defaults.dungeons.simonSaysTimer.timeInPartyChat,
					property = config.dungeons.simonSaysTimer::timeInPartyChat
				)
			}
			// endregion

			.build()
	}
}