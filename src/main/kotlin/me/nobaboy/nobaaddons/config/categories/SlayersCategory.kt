package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.config.NobaConfigUtils.requires
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

private fun Text.wip(): Text = buildText {
	append(buildLiteral("[WIP] ") { red() })
	append(this@wip)
}

object SlayersCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.slayers", "Slayers")) {
		buildGroup(tr("nobaaddons.config.slayers.bossAlert", "Boss Alert")) {
			val enabled = boolean(
				CommonText.Config.ENABLED,
				default = defaults.slayers.bossAlert.enabled,
				property = config.slayers.bossAlert::enabled
			)
			color(
				CommonText.Config.ALERT_COLOR,
				default = defaults.slayers.bossAlert.alertColor,
				property = config.slayers.bossAlert::alertColor
			) requires enabled
		}

		buildGroup(tr("nobaaddons.config.slayers.miniBossAlert", "MiniBoss Alert")) {
			val enabled = boolean(
				CommonText.Config.ENABLED,
				default = defaults.slayers.miniBossAlert.enabled,
				property = config.slayers.miniBossAlert::enabled
			)
			color(
				CommonText.Config.ALERT_COLOR,
				default = defaults.slayers.miniBossAlert.alertColor,
				property = config.slayers.miniBossAlert::alertColor
			) requires enabled
		}

		buildGroup(tr("nobaaddons.config.slayers.highlightMiniBosses", "Highlight MiniBosses")) {
			val enabled = boolean(
				CommonText.Config.ENABLED.wip(),
				default = defaults.slayers.highlightMiniBosses.enabled,
				property = config.slayers.highlightMiniBosses::enabled
			)
			color(
				CommonText.Config.HIGHLIGHT_COLOR.wip(),
				default = defaults.slayers.highlightMiniBosses.highlightColor,
				property = config.slayers.highlightMiniBosses::highlightColor
			) requires enabled
		}

		buildGroup(tr("nobaaddons.config.slayers.announceBossKillTime", "Announce Boss Kill Time")) {
			val enabled = boolean(
				CommonText.Config.ENABLED,
				default = defaults.slayers.announceBossKillTime.enabled,
				property = config.slayers.announceBossKillTime::enabled
			)
			cycler(
				tr("nobaaddons.config.slayers.announceBossKillTime.timeSource", "Time Source"),
				tr("nobaaddons.config.slayers.announceBossKillTime.timeSource.tooltip", "Determines the source for the boss kill time\n\nThe boss time remaining does not support decimals, and as such the kill time will only show full seconds while using it, but will account for server lag"),
				default = defaults.slayers.announceBossKillTime.timeSource,
				property = config.slayers.announceBossKillTime::timeSource
			) requires enabled
		}

		buildGroup(tr("nobaaddons.config.slayers.compactMessages", "Compact Quest Messages")) {
			boolean(
				CommonText.Config.ENABLED,
				tr("nobaaddons.config.slayers.compactMessages.enabled.tooltip", "Condenses messages from Auto-Slayer and manually claiming a Slayer quest at Maddox into one message while enabled"),
				default = defaults.slayers.compactMessages.enabled,
				property = config.slayers.compactMessages::enabled,
			)
		}

		buildGroup(tr("nobaaddons.config.slayers.sven", "Sven Packmaster")) {
			boolean(
				tr("nobaaddons.config.slayers.sven.hidePupNametags", "Hide Pup Nametags"),
				default = defaults.slayers.sven.hidePupNametags,
				property = config.slayers.sven::hidePupNametags
			)
		}

		buildGroup(tr("nobaaddons.config.slayers.voidgloom", "Voidgloom Seraph")) {
			val highlightPhases = boolean(
				tr("nobaaddons.config.slayers.voidgloom.highlightPhases", "Highlight Phases").wip(),
				tr("nobaaddons.config.slayers.voidgloom.highlightPhases.tooltip", "Highlights the Voidgloom Seraph based on its current phase\n\nThe priority of the phases are:\n - Beacon Phase\n - Hits Phase\n - Damage Phase"),
				default = defaults.slayers.voidgloom.highlightPhases,
				property = config.slayers.voidgloom::highlightPhases
			)
			color(
				tr("nobaaddons.config.slayers.voidgloom.beaconPhaseColor", "Beacon Phase Color").wip(),
				default = defaults.slayers.voidgloom.beaconPhaseColor,
				property = config.slayers.voidgloom::beaconPhaseColor,
				allowAlpha = true,
			) requires highlightPhases
			color(
				tr("nobaaddons.config.slayers.voidgloom.hitsPhaseColor", "Hits Phase Color").wip(),
				default = defaults.slayers.voidgloom.hitsPhaseColor,
				property = config.slayers.voidgloom::hitsPhaseColor,
				allowAlpha = true,
			) requires highlightPhases
			color(
				tr("nobaaddons.config.slayers.voidgloom.damagePhaseColor", "Damage Phase Color").wip(),
				default = defaults.slayers.voidgloom.damagePhaseColor,
				property = config.slayers.voidgloom::damagePhaseColor,
				allowAlpha = true,
			) requires highlightPhases

			label(tr("nobaaddons.config.slayers.voidgloom.label.yangGlyphs", "Yang Glyphs"))

			val yangGlyphAlert = boolean(
				tr("nobaaddons.config.slayers.voidgloom.yangGlyphAlert", "Yang Glyph Alert"),
				tr("nobaaddons.config.slayers.voidgloom.yangGlyphAlert.tooltip", "Displays an alert when a Yang Glyph (commonly known as a Beacon) is placed by the Voidgloom Seraph."),
				default = defaults.slayers.voidgloom.yangGlyphAlert,
				property = config.slayers.voidgloom::yangGlyphAlert
			)
			color(
				CommonText.Config.ALERT_COLOR,
				default = defaults.slayers.voidgloom.yangGlyphAlertColor,
				property = config.slayers.voidgloom::yangGlyphAlertColor
			) requires yangGlyphAlert
			val highlightYangGlyph = boolean(
				tr("nobaaddons.config.slayers.voidgloom.highlightYangGlyph", "Highlight Yang Glyphs"),
				default = defaults.slayers.voidgloom.highlightYangGlyphs,
				property = config.slayers.voidgloom::highlightYangGlyphs
			)
			color(
				CommonText.Config.HIGHLIGHT_COLOR,
				default = defaults.slayers.voidgloom.yangGlyphHighlightColor,
				property = config.slayers.voidgloom::yangGlyphHighlightColor
			) requires highlightYangGlyph

			label(tr("nobaaddons.config.slayers.voidgloom.label.nukekubiFixations", "Nukekubi Fixations"))

			val highlightNukekubiFixations = boolean(
				tr("nobaaddons.config.slayers.voidgloom.highlightNukekubiFixations", "Highlight Nukekubi Fixations"),
				default = defaults.slayers.voidgloom.highlightNukekubiFixations,
				property = config.slayers.voidgloom::highlightNukekubiFixations
			)
			color(
				CommonText.Config.HIGHLIGHT_COLOR,
				default = defaults.slayers.voidgloom.nukekubiFixationHighlightColor,
				property = config.slayers.voidgloom::nukekubiFixationHighlightColor
			) requires highlightNukekubiFixations

			label(CommonText.Config.LABEL_MISC)

			boolean(
				tr("nobaaddons.config.slayers.voidgloom.brokenHeartRadiationTimer", "Broken Heart Radiation Timer"),
				tr("nobaaddons.config.slayers.voidgloom.brokenHeartRadiationTimer.tooltip", "Displays a timer on the Voidgloom Seraph indicating how much time is left for its Broken Heart Radiation (commonly known as Lazer Phase)"),
				default = defaults.slayers.voidgloom.brokenHeartRadiationTimer,
				property = config.slayers.voidgloom::brokenHeartRadiationTimer
			)
		}

		buildGroup(tr("nobaaddons.config.slayers.inferno", "Inferno Demonlord")) {
			boolean(
				tr("nobaaddons.config.slayers.inferno.highlightHellionShield", "Highlight Hellion Shield").wip(),
				default = defaults.slayers.inferno.highlightHellionShield,
				property = config.slayers.inferno::highlightHellionShield
			)
		}
	}
}