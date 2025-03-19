package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.text.Text

private fun Text.wip(): Text = buildText {
	append(buildLiteral("[WIP] ") { red() })
	append(this@wip)
}

private fun OptionBuilder<*>.incompatibleWithIris() {
	require {
		val iris = !mod("iris")
		requirement?.let { it and iris } ?: iris
	}
	if(FabricLoader.getInstance().isModLoaded("iris")) {
		descriptionText = buildText {
			descriptionText?.let(::append)
			append("\n")
			append(tr("nobaaddons.config.slayers.incompatibleWithIris", "This feature is currently incompatible with Iris").red())
		}
	}
}

object SlayersCategory {
	fun create() = category(tr("nobaaddons.config.slayers", "Slayers")) {
		bossAlert()
		miniBossAlert()
		highlightMiniBosses()
		killTime()
		compactQuestMessages()

		sven()
		voidgloom()
		demonlord()
	}

	private fun ConfigCategory.Builder.bossAlert() {
		group(tr("nobaaddons.config.slayers.bossAlert", "Boss Alert")) {
			val enabled = add({ slayers.bossAlert::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ slayers.bossAlert::alertColor }, BiMapper.NobaAWTColorMapper) {
				name = CommonText.Config.ALERT_COLOR
				require { option(enabled) }
				colorController()
			}
		}
	}

	private fun ConfigCategory.Builder.miniBossAlert() {
		group(tr("nobaaddons.config.slayers.miniBossAlert", "MiniBoss Alert")) {
			val enabled = add({ slayers.miniBossAlert::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ slayers.miniBossAlert::alertColor }, BiMapper.NobaAWTColorMapper) {
				name = CommonText.Config.ALERT_COLOR
				require { option(enabled) }
				colorController()
			}
		}
	}

	private fun ConfigCategory.Builder.highlightMiniBosses() {
		group(tr("nobaaddons.config.slayers.highlightMiniBosses", "Highlight MiniBosses")) {
			val enabled = add({ slayers.highlightMiniBosses::enabled }) {
				name = CommonText.Config.ENABLED.wip()
				incompatibleWithIris()
				booleanController()
			}
			add({ slayers.highlightMiniBosses::highlightColor }, BiMapper.NobaAWTColorMapper) {
				name = CommonText.Config.HIGHLIGHT_COLOR.wip()
				require { option(enabled) }
				incompatibleWithIris()
				colorController()
			}
		}
	}

	private fun ConfigCategory.Builder.killTime() {
		group(tr("nobaaddons.config.slayers.bossKillTime", "Slayer Boss Kill Time")) {
			val enabled = add({ slayers.bossKillTime::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ slayers.bossKillTime::timeSource }) {
				name = tr("nobaaddons.config.slayers.bossKillTime.timeSource", "Time Source")
				descriptionText = tr("nobaaddons.config.slayers.bossKillTime.timeSource.tooltip", "Determines the source for the boss kill time\n\nThe boss time remaining does not support decimals, and as such the kill time will only show full seconds while using it, but will account for server lag")
				require { option(enabled) }
				enumController()
			}
		}
	}

	private fun ConfigCategory.Builder.compactQuestMessages() {
		group(tr("nobaaddons.config.slayers.compactMessages", "Compact Quest Messages")) {
			val enabled = add({ slayers.compactMessages::enabled }) {
				name = CommonText.Config.ENABLED
				descriptionText = tr("nobaaddons.config.slayers.compactMessages.enabled.tooltip", "Condenses messages from Auto-Slayer and manually claiming a Slayer quest at Maddox into one message while enabled")
				booleanController()
			}
			add({ slayers.compactMessages::removeLastMessage }) {
				name = tr("nobaaddons.config.slayers.compactMessages.removeLastMessage", "Remove Previous Message")
				descriptionText = tr("nobaaddons.config.slayers.compactMessages.removeLastMessage.tooltip", "The last compacted message will also be removed upon completing another slayer quest")
				require { option(enabled) }
				booleanController()
			}
		}
	}

	private fun ConfigCategory.Builder.sven() {
		group(tr("nobaaddons.config.slayers.sven", "Sven Packmaster")) {
			add({ slayers.sven::hidePupNametags }) {
				name = tr("nobaaddons.config.slayers.sven.hidePupNametags", "Hide Pup Nametags")
				booleanController()
			}
		}
	}

	private fun ConfigCategory.Builder.voidgloom() {
		group(tr("nobaaddons.config.slayers.voidgloom", "Voidgloom Seraph")) {
			val highlightPhases = add({ slayers.voidgloom::highlightPhases }) {
				name = tr("nobaaddons.config.slayers.voidgloom.highlightPhases", "Highlight Phases").wip()
				descriptionText = tr("nobaaddons.config.slayers.voidgloom.highlightPhases.tooltip", "Highlights the Voidgloom Seraph based on its current phase\n\nThe priority of the phases are:\n - Beacon Phase\n - Hits Phase\n - Damage Phase")
				incompatibleWithIris()
				booleanController()
			}
			add({ slayers.voidgloom::beaconPhaseColor }) {
				name = tr("nobaaddons.config.slayers.voidgloom.beaconPhaseColor", "Beacon Phase Color").wip()
				require { option(highlightPhases) }
				incompatibleWithIris()
				colorController(allowAlpha = true)
			}
			add({ slayers.voidgloom::hitsPhaseColor }) {
				name = tr("nobaaddons.config.slayers.voidgloom.hitsPhaseColor", "Hits Phase Color").wip()
				require { option(highlightPhases) }
				incompatibleWithIris()
				colorController(allowAlpha = true)
			}
			add({ slayers.voidgloom::damagePhaseColor }) {
				name = tr("nobaaddons.config.slayers.voidgloom.damagePhaseColor", "Damage Phase Color").wip()
				require { option(highlightPhases) }
				incompatibleWithIris()
				colorController(allowAlpha = true)
			}

			label { +tr("nobaaddons.config.slayers.voidgloom.label.yangGlyphs", "Yang Glyphs") }

			val yangGlyphAlert = add({ slayers.voidgloom::yangGlyphAlert }) {
				name = tr("nobaaddons.config.slayers.voidgloom.yangGlyphAlert", "Yang Glyph Alert")
				descriptionText = tr("nobaaddons.config.slayers.voidgloom.yangGlyphAlert.tooltip", "Displays an alert when a Yang Glyph (commonly known as a Beacon) is placed by the Voidgloom Seraph.")
				booleanController()
			}
			add({ slayers.voidgloom::yangGlyphAlertColor }, BiMapper.NobaAWTColorMapper) {
				name = CommonText.Config.ALERT_COLOR
				require { option(yangGlyphAlert) }
				colorController()
			}
			val highlightYangGlyph = add({ slayers.voidgloom::highlightYangGlyphs }) {
				name = tr("nobaaddons.config.slayers.voidgloom.highlightYangGlyph", "Highlight Yang Glyphs")
				booleanController()
			}
			add({ slayers.voidgloom::yangGlyphHighlightColor }, BiMapper.NobaAWTColorMapper) {
				name = CommonText.Config.HIGHLIGHT_COLOR
				require { option(highlightYangGlyph) }
				colorController()
			}

			label { +tr("nobaaddons.config.slayers.voidgloom.label.nukekubiFixations", "Nukekubi Fixations") }

			val highlightNukekubiFixations = add({ slayers.voidgloom::highlightNukekubiFixations }) {
				name = tr("nobaaddons.config.slayers.voidgloom.highlightNukekubiFixations", "Highlight Nukekubi Fixations")
				booleanController()
			}
			add({ slayers.voidgloom::nukekubiFixationHighlightColor }, BiMapper.NobaAWTColorMapper) {
				name = CommonText.Config.HIGHLIGHT_COLOR
				require { option(highlightNukekubiFixations) }
				colorController()
			}

			label { +CommonText.Config.LABEL_MISC }

			add({ slayers.voidgloom::brokenHeartRadiationTimer }) {
				name = tr("nobaaddons.config.slayers.voidgloom.brokenHeartRadiationTimer", "Broken Heart Radiation Timer")
				descriptionText = tr("nobaaddons.config.slayers.voidgloom.brokenHeartRadiationTimer.tooltip", "Displays a timer on the Voidgloom Seraph indicating how much time is left for its Broken Heart Radiation (commonly known as Lazer Phase)")
				booleanController()
			}
		}
	}

	private fun ConfigCategory.Builder.demonlord() {
		group(tr("nobaaddons.config.slayers.inferno", "Inferno Demonlord")) {
			add({ slayers.inferno::highlightHellionShield }) {
				name = tr("nobaaddons.config.slayers.inferno.highlightHellionShield", "Highlight Hellion Shield").wip()
				incompatibleWithIris()
				booleanController()
			}
		}
	}
}