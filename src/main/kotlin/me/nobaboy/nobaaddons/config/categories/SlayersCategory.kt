package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.string
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr

object SlayersCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.slayers", "Slayers")) {
		buildGroup(tr("nobaaddons.config.slayers.bossAlert", "Boss Alert")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.slayers.bossAlert.enabled,
				property = config.slayers.bossAlert::enabled
			)
			string(
				tr("nobaaddons.config.slayers.bossAlert.alertText", "Alert Text"),
				default = defaults.slayers.bossAlert.alertText,
				property = config.slayers.bossAlert::alertText
			)
			color(
				tr("nobaaddons.config.slayers.bossAlert.alertColor", "Alert Color"),
				default = defaults.slayers.bossAlert.alertColor,
				property = config.slayers.bossAlert::alertColor
			)
		}

		buildGroup(tr("nobaaddons.config.slayers.miniBossAlert", "MiniBoss Alert")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.slayers.miniBossAlert.enabled,
				property = config.slayers.miniBossAlert::enabled
			)
			string(
				tr("nobaaddons.config.slayers.miniBossAlert.alertText", "Alert Text"),
				default = defaults.slayers.miniBossAlert.alertText,
				property = config.slayers.miniBossAlert::alertText
			)
			color(
				tr("nobaaddons.config.slayers.miniBossAlert.alertColor", "Alert Color"),
				default = defaults.slayers.miniBossAlert.alertColor,
				property = config.slayers.miniBossAlert::alertColor
			)
		}

		buildGroup(tr("nobaaddons.config.slayers.highlightMiniBosses", "Highlight MiniBosses")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.slayers.highlightMiniBosses.enabled,
				property = config.slayers.highlightMiniBosses::enabled
			)
			color(
				tr("nobaaddons.config.slayers.highlightMiniBosses.highlightColor", "Highlight Color"),
				default = defaults.slayers.highlightMiniBosses.highlightColor,
				property = config.slayers.highlightMiniBosses::highlightColor
			)
		}

		buildGroup(tr("nobaaddons.config.slayers.announceBossKillTime", "Announce Boss Kill Time")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.slayers.announceBossKillTime.enabled,
				property = config.slayers.announceBossKillTime::enabled
			)
			cycler(
				tr("nobaaddons.config.slayers.announceBossKillTime.timeSource", "Time Source"),
				tr("nobaaddons.config.slayers.announceBossKillTime.timeSource.tooltip", "Determines the source for the boss kill time\n\nThe boss time remaining does not support decimals, and as such the kill time will only show full seconds while using it, but will account for server lag"),
				default = defaults.slayers.announceBossKillTime.timeSource,
				property = config.slayers.announceBossKillTime::timeSource
			)
		}

		buildGroup(tr("nobaaddons.config.slayers.voidgloom", "Voidgloom Seraph")) {
			boolean(
				tr("nobaaddons.config.slayers.voidgloom.brokenHeartRadiationTimer", "Broken Heart Radiation Timer"),
				default = defaults.slayers.voidgloom.brokenHeartRadiationTimer,
				property = config.slayers.voidgloom::brokenHeartRadiationTimer
			)
			boolean(
				tr("nobaaddons.config.slayers.voidgloom.yangGlyphAlert", "Yang Glyph Alert"),
				default = defaults.slayers.voidgloom.yangGlyphAlert,
				property = config.slayers.voidgloom::yangGlyphAlert
			)
			color(
				tr("nobaaddons.config.slayers.voidgloom.alertColor", "Alert Color"),
				default = defaults.slayers.voidgloom.alertColor,
				property = config.slayers.voidgloom::alertColor
			)
			boolean(
				tr("nobaaddons.config.slayers.voidgloom.highlightYangGlyph", "Highlight Yang Glyph"),
				default = defaults.slayers.voidgloom.highlightYangGlyph,
				property = config.slayers.voidgloom::highlightYangGlyph
			)
			boolean(
				tr("nobaaddons.config.slayers.voidgloom.highlightNukekubi", "Highlight Nukekubi"),
				default = defaults.slayers.voidgloom.highlightNukekubi,
				property = config.slayers.voidgloom::highlightNukekubi
			)
			color(
				tr("nobaaddons.config.slayers.voidgloom.highlightColor", "Highlight Color"),
				default = defaults.slayers.voidgloom.highlightColor,
				property = config.slayers.voidgloom::highlightColor
			)
		}
	}
}