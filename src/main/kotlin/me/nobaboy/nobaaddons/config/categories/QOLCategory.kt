package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.darkAqua
import me.nobaboy.nobaaddons.utils.tr

object QOLCategory {
	fun create() = category(tr("nobaaddons.config.qol", "QOL")) {
		soundFilters()
		garden()
	}

	private fun ConfigCategory.Builder.soundFilters() {
		group(tr("nobaaddons.config.qol.soundFilters", "Sound Filters")) {
			// region Item Abilities
			label { +CommonText.Config.LABEL_ITEM_ABILITIES }

			add({ qol.soundFilters::muteWitherSkullAbilities }) {
				name = tr("nobaaddons.config.qol.soundFilters.muteWitherSkullAbilities", "Mute Wither Skull Abilities")
				descriptionText = tr("nobaaddons.config.qol.soundFilters.muteWitherSkullAbilities.tooltip", "This affects Dreadlord Sword and Soulstealer Bow, along with the dungeon mobs that use them.")
				booleanController()
			}
			// endregion

			// region Mobs
			label { +CommonText.Config.LABEL_MOBS }

			add({ qol.soundFilters::muteReindrakeSpawn }) {
				name = tr("nobaaddons.config.qol.soundFilters.muteReindrakeSpawn", "Mute Reindrake Spawning")
				booleanController()
			}
			add({ qol.soundFilters::muteReindrakeGiftDrop }) {
				name = tr("nobaaddons.config.qol.soundFilters.muteReindrakeGiftDrop", "Mute Reindrake Gifts")
				booleanController()
			}
			// endregion

			// region Dwarven Mines
			label { +tr("nobaaddons.config.qol.label.dwarvenMines", "Dwarven Mines") }

			add({ qol.soundFilters::muteGoneWithTheWind }) {
				name = tr("nobaaddons.config.qol.soundFilters.muteGoneWithTheWind", "Mute Gone With The Wind")
				booleanController()
			}
			// endregion

			// region Rift
			label { +CommonText.Config.LABEL_RIFT }

			add({ qol.soundFilters::muteKillerSpring }) {
				name = tr("nobaaddons.config.qol.soundFilters.muteKillerSpring", "Mute Killer Spring")
				booleanController()
			}
			// endregion

			// region Misc
			label { +CommonText.Config.LABEL_MISC }

			add({ qol.soundFilters::mutePunch }) {
				name = tr("nobaaddons.config.qol.soundFilters.mutePunch", "Mute Punch Sound")
				descriptionText = tr("nobaaddons.config.qol.soundFilters.mutePunch.tooltip", "Mutes the obnoxious punch sound played on every single hit")
				booleanController()
			}
			// endregion
		}
	}

	private fun ConfigCategory.Builder.garden() {
		group(tr("nobaaddons.config.qol.garden", "Garden")) {
			// region Sensitivity Reducer
			val lockMouseCommand = buildLiteral("/noba lockmouse") { darkAqua() }
			val reduce = add({ qol.garden::reduceMouseSensitivity }) {
				name = tr("nobaaddons.config.qol.garden.reduceMouseSensitivity", "Reduce Mouse Sensitivity")
				descriptionText = tr("nobaaddons.config.qol.garden.reduceMouseSensitivity.tooltip", "Reduces your mouse sensitivity in the Garden while holding a farming tool and on the ground. Your mouse may also be locked with $lockMouseCommand")
				booleanController()
			}
			add({ qol.garden::reductionMultiplier }) {
				name = tr("nobaaddons.config.qol.garden.reductionMultiplier", "Reduction Multiplier")
				require { option(reduce) }
				intSliderController(min = 2, max = 10)
			}
			add({ qol.garden::autoUnlockMouseOnTeleport }) {
				name = tr("nobaaddons.config.qol.garden.autoUnlockMouseOnTeleport", "Auto Unlock Mouse on Teleport")
				descriptionText = tr("nobaaddons.config.qol.garden.autoUnlockMouseOnTeleport.tooltip", "Automatically unlocks your mouse when teleporting more than 5 blocks if locked with $lockMouseCommand")
				booleanController()
			}
			// endregion
		}
	}
}