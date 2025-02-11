package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.utils.*
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr

object QOLCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = buildCategory(tr("nobaaddons.config.qol", "QOL")) {
		// region Sound Filters
		group(tr("nobaaddons.config.qol.soundFilters", "Sound Filters")) {
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
	}
}