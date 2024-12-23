package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.utils.CommonText
import net.minecraft.text.Text

object EventsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.events"))

			// region Mythological Ritual
			.buildGroup(Text.translatable("nobaaddons.config.events.mythological")) {
				boolean(
					Text.translatable("nobaaddons.config.events.mythological.burrowGuess"),
					Text.translatable("nobaaddons.config.events.mythological.burrowGuess.tooltip"),
					default = defaults.events.mythological.burrowGuess,
					property = config.events.mythological::burrowGuess
				)
				boolean(
					Text.translatable("nobaaddons.config.events.mythological.findNearbyBurrows"),
					Text.translatable("nobaaddons.config.events.mythological.findNearbyBurrows.tooltip"),
					default = defaults.events.mythological.findNearbyBurrows,
					property = config.events.mythological::findNearbyBurrows
				)
				boolean(
					Text.translatable("nobaaddons.config.events.mythological.pingOnBurrowFind"),
					Text.translatable("nobaaddons.config.events.mythological.pingOnBurrowFind.tooltip"),
					default = defaults.events.mythological.dingOnBurrowFind,
					property = config.events.mythological::dingOnBurrowFind
				)
				boolean(
					Text.translatable("nobaaddons.config.events.mythological.removeGuessOnBurrowFind"),
					default = defaults.events.mythological.removeGuessOnBurrowFind,
					property = config.events.mythological::removeGuessOnBurrowFind
				)
				boolean(
					Text.translatable("nobaaddons.config.events.mythological.findNearestWarp"),
					default = defaults.events.mythological.findNearestWarp,
					property = config.events.mythological::findNearestWarp
				)

				label(Text.translatable("nobaaddons.config.events.mythological.label.inquisitorSharing"))

				boolean(Text.translatable("nobaaddons.config.events.mythological.alertInquisitor"),
					Text.translatable("nobaaddons.config.events.mythological.alertInquisitor.tooltip"),
					default = defaults.events.mythological.alertInquisitor, property = config.events.mythological::alertInquisitor
				)
				boolean(
					Text.translatable("nobaaddons.config.events.mythological.alertOnlyInParty"),
					Text.translatable("nobaaddons.config.events.mythological.alertOnlyInParty.tooltip"),
					default = defaults.events.mythological.alertOnlyInParty,
					property = config.events.mythological::alertOnlyInParty
				)
				cycler(
					CommonText.Config.NOTIFICATION_SOUND,
					default = defaults.events.mythological.notificationSound,
					property = config.events.mythological::notificationSound
				)
				boolean(
					Text.translatable("nobaaddons.config.events.mythological.showInquisitorDespawnTime"),
					Text.translatable("nobaaddons.config.events.mythological.showInquisitorDespawnTime.tooltip"),
					default = defaults.events.mythological.showInquisitorDespawnTime,
					property = config.events.mythological::showInquisitorDespawnTime
				)
				boolean(
					Text.translatable("nobaaddons.config.events.mythological.inquisitorFocusMode"),
					Text.translatable("nobaaddons.config.events.mythological.inquisitorFocusMode.tooltip"),
					default = defaults.events.mythological.inquisitorFocusMode,
					property = config.events.mythological::inquisitorFocusMode
				)

				label(CommonText.Config.LABEL_MISC)

				boolean(
					Text.translatable("nobaaddons.config.events.mythological.announceRareDrops"),
					Text.translatable("nobaaddons.config.events.mythological.announceRareDrops.tooltip"),
					default = defaults.events.mythological.announceRareDrops,
					property = config.events.mythological::announceRareDrops
				)

				label(Text.translatable("nobaaddons.config.events.mythological.label.warpLocations"))

				boolean(
					Text.translatable("nobaaddons.config.events.mythological.ignoreCrypt"),
					default = defaults.events.mythological.ignoreCrypt,
					property = config.events.mythological::ignoreCrypt
				)
				boolean(
					Text.translatable("nobaaddons.config.events.mythological.ignoreWizard"),
					default = defaults.events.mythological.ignoreWizard,
					property = config.events.mythological::ignoreWizard
				)
				boolean(
					Text.translatable("nobaaddons.config.events.mythological.ignoreStonks"),
					default = defaults.events.mythological.ignoreStonks,
					property = config.events.mythological::ignoreStonks
				)
			}
			// endregion

			.build()
	}
}