package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import net.minecraft.text.Text

object FishingCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.fishing"))

			// region Bobber Timer
			.buildGroup(Text.translatable("nobaaddons.config.fishing.bobberTimer")) {
				boolean(
					Text.translatable("nobaaddons.config.enabled"),
					default = defaults.fishing.bobberTimer.enabled,
					property = config.fishing.bobberTimer::enabled
				)
				boolean(
					Text.translatable("nobaaddons.config.fishing.bobberTimer.crimsonIsleOnly"),
					default = defaults.fishing.bobberTimer.crimsonIsleOnly,
					property = config.fishing.bobberTimer::crimsonIsleOnly
				)
				boolean(
					Text.translatable("nobaaddons.config.fishing.bobberTimer.lerpColor"),
					Text.translatable("nobaaddons.config.fishing.bobberTimer.lerpColor.tooltip"),
					default = defaults.fishing.bobberTimer.lerpColor,
					property = config.fishing.bobberTimer::lerpColor
				)
			}
			// endregion

			// region Sea Creature Alert
			.buildGroup(Text.translatable("nobaaddons.config.fishing.seaCreatureAlert")) {
				boolean(
					Text.translatable("nobaaddons.config.enabled"),
					default = defaults.fishing.seaCreatureAlert.enabled,
					property = config.fishing.seaCreatureAlert::enabled
				)
				boolean(
					Text.translatable("nobaaddons.config.fishing.seaCreatureAlert.nameInsteadOfRarity"),
					default = defaults.fishing.seaCreatureAlert.nameInsteadOfRarity,
					property = config.fishing.seaCreatureAlert::nameInsteadOfRarity
				)
				cycler(
					Text.translatable("nobaaddons.config.fishing.seaCreatureAlert.minimumRarity"),
					default = defaults.fishing.seaCreatureAlert.minimumRarity,
					property = config.fishing.seaCreatureAlert::minimumRarity
				)
				cycler(
					Text.translatable("nobaaddons.config.notificationSound"),
					default = defaults.fishing.seaCreatureAlert.notificationSound,
					property = config.fishing.seaCreatureAlert::notificationSound
				)
			}
			// endregion

			// region Highlight Thunder Sparks
			.buildGroup(Text.translatable("nobaaddons.config.fishing.highlightThunderSparks")) {
				boolean(
					Text.translatable("nobaaddons.config.enabled"),
					default = defaults.fishing.highlightThunderSparks.enabled,
					property = config.fishing.highlightThunderSparks::enabled
				)
				color(
					Text.translatable("nobaaddons.config.fishing.highlightThunderSparks.highlightColor"),
					default = defaults.fishing.highlightThunderSparks.highlightColor,
					property = config.fishing.highlightThunderSparks::highlightColor
				)
				boolean(
					Text.translatable("nobaaddons.config.fishing.highlightThunderSparks.showText"),
					default = defaults.fishing.highlightThunderSparks.showText,
					property = config.fishing.highlightThunderSparks::showText
				)
			}
			// endregion

			.build()
	}
}