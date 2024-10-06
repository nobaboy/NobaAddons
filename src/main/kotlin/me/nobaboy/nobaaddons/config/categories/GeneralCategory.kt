package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.NobaConfig
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

			.build()
	}
}