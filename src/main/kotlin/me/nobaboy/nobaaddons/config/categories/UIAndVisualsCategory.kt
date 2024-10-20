package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.ListOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.ui.controllers.InfoBox
import me.nobaboy.nobaaddons.config.ui.controllers.InfoBoxController
import me.nobaboy.nobaaddons.features.ui.infobox.InfoBoxHud
import net.minecraft.text.Text
import java.awt.Color

object UIAndVisualsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.uiAndVisuals"))
			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.enabled"))
					.binding(defaults.uiAndVisuals.etherwarpHelper.enabled, config.uiAndVisuals.etherwarpHelper::enabled) { config.uiAndVisuals.etherwarpHelper.enabled = it}
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Color>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.overlayColor"))
					.binding(defaults.uiAndVisuals.etherwarpHelper.overlayColor, config.uiAndVisuals.etherwarpHelper::overlayColor) { config.uiAndVisuals.etherwarpHelper.overlayColor = it}
					.controller(ColorControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.showFailText"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.showFailText.tooltip")))
					.binding(defaults.uiAndVisuals.etherwarpHelper.showFailText, config.uiAndVisuals.etherwarpHelper::showFailText) { config.uiAndVisuals.etherwarpHelper.showFailText = it}
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.allowOverlayOnAir"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.allowOnAir.tooltip")))
					.binding(defaults.uiAndVisuals.etherwarpHelper.allowOnAir, config.uiAndVisuals.etherwarpHelper::allowOnAir) { config.uiAndVisuals.etherwarpHelper.allowOnAir = it}
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())


			.group(ListOption.createBuilder<InfoBox>()
				.name(Text.translatable("nobaaddons.config.uiAndVisuals.infoBoxes"))
				.binding(defaults.uiAndVisuals.infoBoxes, config.uiAndVisuals::infoBoxes) { config.uiAndVisuals.infoBoxes.replaceWith(it) }
				.controller(InfoBoxController.Builder::create)
				.initial(InfoBoxHud::newInfoBox)
				.maximumNumberOfEntries(10)
				.build()
			)

			.build()
	}
}

private fun <T> MutableList<T>.replaceWith(with: List<T>) {
	clear()
	addAll(with)
}