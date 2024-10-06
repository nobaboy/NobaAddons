package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.dsl.binding
import dev.isxander.yacl3.dsl.controller
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaMainScreen
import net.minecraft.client.MinecraftClient
import net.minecraft.text.Text

object GeneralCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("config.general"))
			.option(ButtonOption.createBuilder()
				.name(Text.translatable("config.general.main"))
				.text(Text.translatable("config.general.open"))
				.action { screen, option ->
					MinecraftClient.getInstance().setScreen(NobaMainScreen())
				}
				.build())

			.option(Option.createBuilder<Boolean>()
				.name(Text.translatable("config.general.wikiCommandAutoOpen"))
				.description(OptionDescription.of(Text.translatable("config.general.wikiCommandAutoOpen.tooltip")))
				.binding(defaults.general.wikiCommandAutoOpen, config.general::wikiCommandAutoOpen) { config.general.wikiCommandAutoOpen = it }
				.controller(NobaConfigUtils::createBooleanController)
				.build())

			.build()
	}
}