package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.core.MobRarity
import me.nobaboy.nobaaddons.utils.sound.NotificationSound
import net.minecraft.text.Text

object FishingCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.fishing"))

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.fishing.seaCreatureAlert"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.enabled"))
					.binding(defaults.fishing.seaCreatureAlert.enabled, config.fishing.seaCreatureAlert::enabled) { config.fishing.seaCreatureAlert.enabled = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.fishing.seaCreatureAlert.nameInsteadOfRarity"))
					.binding(defaults.fishing.seaCreatureAlert.nameInsteadOfRarity, config.fishing.seaCreatureAlert::nameInsteadOfRarity) { config.fishing.seaCreatureAlert.nameInsteadOfRarity = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<MobRarity>()
					.name(Text.translatable("nobaaddons.config.fishing.seaCreatureAlert.minimumRarity"))
					.binding(defaults.fishing.seaCreatureAlert.minimumRarity, config.fishing.seaCreatureAlert::minimumRarity) { config.fishing.seaCreatureAlert.minimumRarity = it }
					.controller(NobaConfigUtils::createCyclingController)
					.build())

				.option(Option.createBuilder<NotificationSound>()
					.name(Text.translatable("nobaaddons.config.notificationSound"))
					.binding(defaults.fishing.seaCreatureAlert.notificationSound, config.fishing.seaCreatureAlert::notificationSound) { config.fishing.seaCreatureAlert.notificationSound = it }
					.controller(NobaConfigUtils::createCyclingController)
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}