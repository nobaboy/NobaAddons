package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.impl.ChatFilterOption
import net.minecraft.text.Text

object ChatCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("config.chat"))

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("config.chat.filter"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("config.chat.filter.tipMessage"))
					.description(OptionDescription.of(Text.translatable("config.chat.filter.tipMessage.tooltip")))
					.binding(defaults.chat.filter.hideTipMessages, config.chat.filter::hideTipMessages) { config.chat.filter.hideTipMessages = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<ChatFilterOption>()
					.name(Text.translatable("config.chat.filter.blessingMessage"))
					.binding(defaults.chat.filter.blessingMessage, config.chat.filter::blessingMessage) { config.chat.filter.blessingMessage = it }
					.controller(NobaConfigUtils::createCyclingController)
					.build())

				.option(Option.createBuilder<ChatFilterOption>()
					.name(Text.translatable("config.chat.filter.healerOrbMessage"))
					.binding(defaults.chat.filter.healerOrbMessage, config.chat.filter::healerOrbMessage) { config.chat.filter.healerOrbMessage = it }
					.controller(NobaConfigUtils::createCyclingController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("config.chat.filter.pickupObtainMessage"))
					.binding(defaults.chat.filter.pickupObtainMessage, config.chat.filter::pickupObtainMessage) { config.chat.filter.pickupObtainMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("config.chat.filter.allow5050ItemMessage"))
					.description(OptionDescription.of(Text.translatable("config.chat.filter.allow5050ItemMessage.tooltip")))
					.binding(defaults.chat.filter.allow5050ItemMessage, config.chat.filter::allow5050ItemMessage) { config.chat.filter.allow5050ItemMessage = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.build()
			)

			.build()
	}
}