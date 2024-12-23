package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.config.NobaConfigUtils.slider
import me.nobaboy.nobaaddons.utils.CommonText
import net.minecraft.text.Text

object QOLCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.qol"))

			// region Sound Filters
			.buildGroup(Text.translatable("nobaaddons.config.qol.soundFilters")) {
				// region Item Abilities
				label(CommonText.Config.LABEL_ITEM_ABILITIES)

				boolean(
					Text.translatable("nobaaddons.config.qol.soundFilters.muteWitherSkullAbilities"),
					Text.translatable("nobaaddons.config.qol.soundFilters.muteWitherSkullAbilities.tooltip"),
					default = defaults.qol.soundFilters.muteWitherSkullAbilities,
					property = config.qol.soundFilters::muteWitherSkullAbilities,
				)
				// endregion

				// region Mobs
				label(CommonText.Config.LABEL_MOBS)

				boolean(
					Text.translatable("nobaaddons.config.qol.soundFilters.muteReindrakeSpawn"),
					default = defaults.qol.soundFilters.muteReindrakeSpawn,
					property = config.qol.soundFilters::muteReindrakeSpawn,
				)
				boolean(
					Text.translatable("nobaaddons.config.qol.soundFilters.muteReindrakeGiftDrop"),
					default = defaults.qol.soundFilters.muteReindrakeGiftDrop,
					property = config.qol.soundFilters::muteReindrakeGiftDrop,
				)
				// endregion

				// region Dwarven Mines
				label(Text.translatable("nobaaddons.config.qol.label.dwarvenMines"))

				boolean(
					Text.translatable("nobaaddons.config.qol.soundFilters.muteGoneWithTheWind"),
					default = defaults.qol.soundFilters.muteGoneWithTheWind,
					property = config.qol.soundFilters::muteGoneWithTheWind
				)
				// endregion

				// region Rift
				label(CommonText.Config.LABEL_RIFT)

				boolean(
					Text.translatable("nobaaddons.config.qol.soundFilters.muteKillerSpring"),
					default = defaults.qol.soundFilters.muteKillerSpring,
					property = config.qol.soundFilters::muteKillerSpring
				)
				// endregion

				// region Misc
				label(CommonText.Config.LABEL_MISC)

				boolean(
					Text.translatable("nobaaddons.config.qol.soundFilters.mutePunch"),
					Text.translatable("nobaaddons.config.qol.soundFilters.mutePunch.tooltip"),
					default = defaults.qol.soundFilters.mutePunch,
					property = config.qol.soundFilters::mutePunch
				)
				// endregion
			}
			// endregion

			// region Garden
			.buildGroup(Text.translatable("nobaaddons.config.qol.garden")) {
				// region Sensitivity Reducer
				boolean(
					Text.translatable("nobaaddons.config.qol.garden.reduceMouseSensitivity"),
					Text.translatable("nobaaddons.config.qol.garden.reduceMouseSensitivity.tooltip"),
					default = defaults.qol.garden.reduceMouseSensitivity,
					property = config.qol.garden::reduceMouseSensitivity
				)
				slider(
					Text.translatable("nobaaddons.config.qol.garden.reductionMultiplier"),
					default = defaults.qol.garden.reductionMultiplier,
					property = config.qol.garden::reductionMultiplier,
					min = 2,
					max = 10,
					step = 1
				)
				boolean(
					Text.translatable("nobaaddons.config.qol.garden.isDaedalusFarmingTool"),
					Text.translatable("nobaaddons.config.qol.garden.isDaedalusFarmingTool.tooltip"),
					default = defaults.qol.garden.isDaedalusFarmingTool,
					property = config.qol.garden::isDaedalusFarmingTool
				)
				boolean(
					Text.translatable("nobaaddons.config.qol.garden.autoUnlockMouseOnTeleport"),
					Text.translatable("nobaaddons.config.qol.garden.autoUnlockMouseOnTeleport.tooltip"),
					default = defaults.qol.garden.autoUnlockMouseOnTeleport,
					property = config.qol.garden::autoUnlockMouseOnTeleport
				)
				// endregion
			}
			// endregion

			.build()
	}
}