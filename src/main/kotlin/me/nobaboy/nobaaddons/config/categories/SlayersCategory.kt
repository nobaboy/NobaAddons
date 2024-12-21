package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.string
import net.minecraft.text.Text

object SlayersCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.slayers"))

			.buildGroup(Text.translatable("nobaaddons.config.slayers.announceBossKillTime")) {
				boolean(
					Text.translatable("nobaaddons.config.enabled"),
					default = defaults.slayers.announceBossKillTime.enabled,
					property = config.slayers.announceBossKillTime::enabled
				)
				cycler(
					Text.translatable("nobaaddons.config.slayers.announceBossKillTime.timeSource"),
					Text.translatable("nobaaddons.config.slayers.announceBossKillTime.timeSource.tooltip"),
					default = defaults.slayers.announceBossKillTime.timeSource,
					property = config.slayers.announceBossKillTime::timeSource
				)
			}

			.buildGroup(Text.translatable("nobaaddons.config.slayers.miniBossAlert")) {
				boolean(
					Text.translatable("nobaaddons.config.enabled"),
					default = defaults.slayers.miniBossAlert.enabled,
					property = config.slayers.miniBossAlert::enabled
				)
				string(
					Text.translatable("nobaaddons.config.slayers.miniBossAlert.alertText"),
					default = defaults.slayers.miniBossAlert.alertText,
					property = config.slayers.miniBossAlert::alertText
				)
				color(
					Text.translatable("nobaaddons.config.slayers.miniBossAlert.alertColor"),
					default = defaults.slayers.miniBossAlert.alertColor,
					property = config.slayers.miniBossAlert::alertColor
				)
			}

			.buildGroup(Text.translatable("nobaaddons.config.slayers.highlightMiniBosses")) {
				boolean(
					Text.translatable("nobaaddons.config.enabled"),
					default = defaults.slayers.highlightMiniBosses.enabled,
					property = config.slayers.highlightMiniBosses::enabled
				)
				color(
					Text.translatable("nobaaddons.config.slayers.highlightMiniBosses.highlightColor"),
					default = defaults.slayers.highlightMiniBosses.highlightColor,
					property = config.slayers.highlightMiniBosses::highlightColor
				)
			}

			.build()
	}
}