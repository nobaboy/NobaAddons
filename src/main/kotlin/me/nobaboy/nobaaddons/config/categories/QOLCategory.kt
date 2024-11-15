package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import net.minecraft.text.Text

object QOLCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.qol"))

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.qol.soundFilters"))
				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.label.dungeons"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.qol.soundFilters.muteDreadlordAndSouleater"))
					.binding(defaults.qol.soundFilters.muteDreadlordAndSouleater, config.qol.soundFilters::muteDreadlordAndSouleater) { config.qol.soundFilters.muteDreadlordAndSouleater = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}