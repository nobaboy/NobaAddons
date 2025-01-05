package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
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
			color(
				CommonText.Config.ALERT_COLOR,
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
			color(
				CommonText.Config.ALERT_COLOR,
				default = defaults.slayers.miniBossAlert.alertColor,
				property = config.slayers.miniBossAlert::alertColor
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

			label(tr("nobaaddons.config.slayers.voidgloom.label.yangGlyphs", "Yang Glyphs"))

			boolean(
				tr("nobaaddons.config.slayers.voidgloom.yangGlyphAlert", "Yang Glyph Alert"),
				default = defaults.slayers.voidgloom.yangGlyphAlert,
				property = config.slayers.voidgloom::yangGlyphAlert
			)
			color(
				CommonText.Config.ALERT_COLOR,
				default = defaults.slayers.voidgloom.yangGlyphAlertColor,
				property = config.slayers.voidgloom::yangGlyphAlertColor
			)
			boolean(
				tr("nobaaddons.config.slayers.voidgloom.highlightYangGlyph", "Highlight Yang Glyphs"),
				default = defaults.slayers.voidgloom.highlightYangGlyphs,
				property = config.slayers.voidgloom::highlightYangGlyphs
			)
			color(
				CommonText.Config.HIGHLIGHT_COLOR,
				default = defaults.slayers.voidgloom.yangGlyphHighlightColor,
				property = config.slayers.voidgloom::yangGlyphHighlightColor
			)

			label(tr("nobaaddons.config.slayers.voidgloom.label.nukekubiFixations", "Nukekubi Fixations"))

			boolean(
				tr("nobaaddons.config.slayers.voidgloom.highlightNukekubi", "Highlight Nukekubi Fixations"),
				default = defaults.slayers.voidgloom.highlightNukekubiFixations,
				property = config.slayers.voidgloom::highlightNukekubiFixations
			)
			color(
				CommonText.Config.HIGHLIGHT_COLOR,
				default = defaults.slayers.voidgloom.nukekubiFixationHighlightColor,
				property = config.slayers.voidgloom::nukekubiFixationHighlightColor
			)
		}
	}
}