package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.features.chat.filters.ChatFilterOption
import net.minecraft.text.Text

object ChatCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.chat"))

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.chat.alerts"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.alerts.mythicSeaCreatureSpawn"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.alerts.mythicSeaCreatureSpawn.tooltip")))
					.binding(defaults.chat.alerts.mythicSeaCreatureSpawn, config.chat.alerts::mythicSeaCreatureSpawn) { config.chat.alerts.mythicSeaCreatureSpawn = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.alerts.vanquisherSpawn"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.alerts.vanquisherSpawn.tooltip")))
					.binding(defaults.chat.alerts.vanquisherSpawn, config.chat.alerts::vanquisherSpawn) { config.chat.alerts.vanquisherSpawn = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.build()
			)

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.chat.filter"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filter.hideAbilityDamageMessage"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filter.hideAbilityDamageMessage.tooltip")))
					.binding(defaults.chat.filters.hideAbilityDamageMessage, config.chat.filters::hideAbilityDamageMessage) { config.chat.filters.hideAbilityDamageMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filter.hideTipMessages"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filter.hideTipMessages.tooltip")))
					.binding(defaults.chat.filters.hideTipMessages, config.chat.filters::hideTipMessages) { config.chat.filters.hideTipMessages = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filter.hideProfileInfo"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filter.hideProfileInfo.tooltip")))
					.binding(defaults.chat.filters.hideProfileInfo, config.chat.filters::hideProfileInfo) { config.chat.filters.hideProfileInfo = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<ChatFilterOption>()
					.name(Text.translatable("nobaaddons.config.chat.filter.blessingMessage"))
					.binding(defaults.chat.filters.blessingMessage, config.chat.filters::blessingMessage) { config.chat.filters.blessingMessage = it }
					.controller(NobaConfigUtils::createCyclingController)
					.build())

				.option(Option.createBuilder<ChatFilterOption>()
					.name(Text.translatable("nobaaddons.config.chat.filter.healerOrbMessage"))
					.binding(defaults.chat.filters.healerOrbMessage, config.chat.filters::healerOrbMessage) { config.chat.filters.healerOrbMessage = it }
					.controller(NobaConfigUtils::createCyclingController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filter.pickupObtainMessage"))
					.binding(defaults.chat.filters.pickupObtainMessage, config.chat.filters::pickupObtainMessage) { config.chat.filters.pickupObtainMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.chat.filter.allow5050ItemMessage"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.chat.filter.allow5050ItemMessage.tooltip")))
					.binding(defaults.chat.filters.allow5050ItemMessage, config.chat.filters::allow5050ItemMessage) { config.chat.filters.allow5050ItemMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.build()
			)

			.build()
	}
}