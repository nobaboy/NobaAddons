package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.config.NobaConfigUtils.requires
import me.nobaboy.nobaaddons.config.NobaConfigUtils.slider
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.darkAqua
import me.nobaboy.nobaaddons.utils.tr

object QOLCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.qol", "QOL")) {
		// region Sound Filters
		buildGroup(tr("nobaaddons.config.qol.soundFilters", "Sound Filters")) {
			// region Item Abilities
			label(CommonText.Config.LABEL_ITEM_ABILITIES)

			boolean(
				tr("nobaaddons.config.qol.soundFilters.muteWitherSkullAbilities", "Mute Wither Skull Abilities"),
				tr("nobaaddons.config.qol.soundFilters.muteWitherSkullAbilities.tooltip", "This affects Dreadlord Sword and Soulstealer Bow, along with the dungeon mobs that use them."),
				default = defaults.qol.soundFilters.muteWitherSkullAbilities,
				property = config.qol.soundFilters::muteWitherSkullAbilities,
			)
			// endregion

			// region Mobs
			label(CommonText.Config.LABEL_MOBS)

			boolean(
				tr("nobaaddons.config.qol.soundFilters.muteReindrakeSpawn", "Mute Reindrake Spawning"),
				default = defaults.qol.soundFilters.muteReindrakeSpawn,
				property = config.qol.soundFilters::muteReindrakeSpawn,
			)
			boolean(
				tr("nobaaddons.config.qol.soundFilters.muteReindrakeGiftDrop", "Mute Reindrake Gifts"),
				default = defaults.qol.soundFilters.muteReindrakeGiftDrop,
				property = config.qol.soundFilters::muteReindrakeGiftDrop,
			)
			// endregion

			// region Dwarven Mines
			label(tr("nobaaddons.config.qol.label.dwarvenMines", "Dwarven Mines"))

			boolean(
				tr("nobaaddons.config.qol.soundFilters.muteGoneWithTheWind", "Mute Gone With The Wind"),
				default = defaults.qol.soundFilters.muteGoneWithTheWind,
				property = config.qol.soundFilters::muteGoneWithTheWind
			)
			// endregion

			// region Rift
			label(CommonText.Config.LABEL_RIFT)

			boolean(
				tr("nobaaddons.config.qol.soundFilters.muteKillerSpring", "Mute Killer Spring"),
				default = defaults.qol.soundFilters.muteKillerSpring,
				property = config.qol.soundFilters::muteKillerSpring
			)
			// endregion

			// region Misc
			label(CommonText.Config.LABEL_MISC)

			boolean(
				tr("nobaaddons.config.qol.soundFilters.mutePunch", "Mute Punch Sound"),
				tr("nobaaddons.config.qol.soundFilters.mutePunch.tooltip", "Mutes the punch sound Hypixel started playing on all hits recently"),
				default = defaults.qol.soundFilters.mutePunch,
				property = config.qol.soundFilters::mutePunch
			)
			// endregion
		}
		// endregion

		// region Garden
		buildGroup(tr("nobaaddons.config.qol.garden", "Garden")) {
			// region Sensitivity Reducer
			val lockMouseCommand = buildLiteral("/noba lockmouse") { darkAqua() }
			val reduce = boolean(
				tr("nobaaddons.config.qol.garden.reduceMouseSensitivity", "Reduce Mouse Sensitivity"),
				tr("nobaaddons.config.qol.garden.reduceMouseSensitivity.tooltip", "Reduces your mouse sensitivity in the Garden while holding a farming tool and on the ground. Your mouse may also be locked with $lockMouseCommand"),
				default = defaults.qol.garden.reduceMouseSensitivity,
				property = config.qol.garden::reduceMouseSensitivity
			)
			slider(
				tr("nobaaddons.config.qol.garden.reductionMultiplier", "Reduction Multiplier"),
				default = defaults.qol.garden.reductionMultiplier,
				property = config.qol.garden::reductionMultiplier,
				min = 2,
				max = 10,
				step = 1
			) requires reduce
			boolean(
				tr("nobaaddons.config.qol.garden.autoUnlockMouseOnTeleport", "Auto Unlock Mouse on Teleport"),
				tr("nobaaddons.config.qol.garden.autoUnlockMouseOnTeleport.tooltip", "Automatically unlocks your mouse when teleporting more than 5 blocks if locked with $lockMouseCommand"),
				default = defaults.qol.garden.autoUnlockMouseOnTeleport,
				property = config.qol.garden::autoUnlockMouseOnTeleport
			)
			// endregion
		}
		// endregion
	}
}