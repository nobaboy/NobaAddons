package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.string
import net.minecraft.text.Text

object SlayersCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.slayers"))

			.buildGroup(Text.translatable("nobaaddons.config.slayers.miniBossAlert")) {
				boolean(
					Text.translatable("nobaaddons.config.slayers.miniBoss.alert"),
					default = defaults.slayers.miniBoss.alert,
					property = config.slayers.miniBoss::alert
				)
				string(
					Text.translatable("nobaaddons.config.slayers.miniBoss.alertText"),
					default = defaults.slayers.miniBoss.alertText,
					property = config.slayers.miniBoss::alertText
				)
				color(
					Text.translatable("nobaaddons.config.slayers.miniBoss.alertColor"),
					default = defaults.slayers.miniBoss.alertColor,
					property = config.slayers.miniBoss::alertColor
				)
				cycler(
					Text.translatable("nobaaddons.config.notificationSound"),
					default = defaults.slayers.miniBoss.notificationSound,
					property = config.slayers.miniBoss::notificationSound
				)
			}

			.buildGroup(Text.translatable("nobaaddons.config.slayers.miniBoss")) {
				boolean(
					Text.translatable("nobaaddons.config.slayers.miniBoss.highlight"),
					default = defaults.slayers.miniBoss.highlight,
					property = config.slayers.miniBoss::highlight
				)
				color(
					Text.translatable("nobaaddons.config.slayers.miniBoss.highlightColor"),
					default = defaults.slayers.miniBoss.highlightColor,
					property = config.slayers.miniBoss::highlightColor
				)
			}

			.build()
	}
}