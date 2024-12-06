package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesScreen
import me.nobaboy.nobaaddons.utils.MCUtils
import net.minecraft.text.Text
import java.awt.Color

object UIAndVisualsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.uiAndVisuals"))

			.option(ButtonOption.createBuilder()
				.name(Text.translatable("nobaaddons.screen.infoBoxes"))
				.text(Text.translatable("nobaaddons.screen.button.open"))
				.action { screen, option -> MCUtils.client.setScreen(InfoBoxesScreen(screen)) }
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.uiAndVisuals.temporaryWaypoints"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.enabled"))
					.binding(defaults.uiAndVisuals.temporaryWaypoints.enabled, config.uiAndVisuals.temporaryWaypoints::enabled) { config.uiAndVisuals.temporaryWaypoints.enabled = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Color>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.temporaryWaypoints.waypointColor"))
					.binding(defaults.uiAndVisuals.temporaryWaypoints.waypointColor, config.uiAndVisuals.temporaryWaypoints::waypointColor) { config.uiAndVisuals.temporaryWaypoints.waypointColor = it }
					.controller(ColorControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Int>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.temporaryWaypoints.expirationTime"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.temporaryWaypoints.expirationTime.tooltip")))
					.binding(defaults.uiAndVisuals.temporaryWaypoints.expirationTime, config.uiAndVisuals.temporaryWaypoints::expirationTime) { config.uiAndVisuals.temporaryWaypoints.expirationTime = it }
					.controller { IntegerSliderControllerBuilder.create(it).range(1, 120).step(1).formatValue { Text.translatable("nobaaddons.config.seconds", it) } }
					.build())

				.collapsed(true)
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.enabled"))
					.binding(defaults.uiAndVisuals.etherwarpHelper.enabled, config.uiAndVisuals.etherwarpHelper::enabled) { config.uiAndVisuals.etherwarpHelper.enabled = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Color>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.highlightColor"))
					.binding(defaults.uiAndVisuals.etherwarpHelper.highlightColor, config.uiAndVisuals.etherwarpHelper::highlightColor) { config.uiAndVisuals.etherwarpHelper.highlightColor = it }
					.controller(ColorControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.showFailText"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.showFailText.tooltip")))
					.binding(defaults.uiAndVisuals.etherwarpHelper.showFailText, config.uiAndVisuals.etherwarpHelper::showFailText) { config.uiAndVisuals.etherwarpHelper.showFailText = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.allowOverlayOnAir"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.allowOverlayOnAir.tooltip")))
					.binding(defaults.uiAndVisuals.etherwarpHelper.allowOnAir, config.uiAndVisuals.etherwarpHelper::allowOnAir) { config.uiAndVisuals.etherwarpHelper.allowOnAir = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo"))
				.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.tooltip")))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.checkMarkIfMaxed"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.checkMarkIfMaxed.tooltip")))
					.binding(defaults.uiAndVisuals.slotInfo.checkMarkIfMaxed, config.uiAndVisuals.slotInfo::checkMarkIfMaxed) { config.uiAndVisuals.slotInfo.checkMarkIfMaxed = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.label.uiElements"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.bestiaryMilestone"))
					.binding(defaults.uiAndVisuals.slotInfo.bestiaryMilestone, config.uiAndVisuals.slotInfo::bestiaryMilestone) { config.uiAndVisuals.slotInfo.bestiaryMilestone = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.bestiaryFamilyTier"))
					.binding(defaults.uiAndVisuals.slotInfo.bestiaryFamilyTier, config.uiAndVisuals.slotInfo::bestiaryFamilyTier) { config.uiAndVisuals.slotInfo.bestiaryFamilyTier = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.collectionTier"))
					.binding(defaults.uiAndVisuals.slotInfo.collectionTier, config.uiAndVisuals.slotInfo::collectionTier) { config.uiAndVisuals.slotInfo.collectionTier = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.gardenPlotPests"))
					.binding(defaults.uiAndVisuals.slotInfo.gardenPlotPests, config.uiAndVisuals.slotInfo::gardenPlotPests) { config.uiAndVisuals.slotInfo.gardenPlotPests = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.skillLevel"))
					.binding(defaults.uiAndVisuals.slotInfo.skillLevel, config.uiAndVisuals.slotInfo::skillLevel) { config.uiAndVisuals.slotInfo.skillLevel = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.skyBlockLevel"))
					.binding(defaults.uiAndVisuals.slotInfo.skyBlockLevel, config.uiAndVisuals.slotInfo::skyBlockLevel) { config.uiAndVisuals.slotInfo.skyBlockLevel = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.tuningPoints"))
					.binding(defaults.uiAndVisuals.slotInfo.tuningPoints, config.uiAndVisuals.slotInfo::tuningPoints) { config.uiAndVisuals.slotInfo.tuningPoints = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(LabelOption.createBuilder()
					.line(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.label.items"))
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.dungeonHeadTier"))
					.binding(defaults.uiAndVisuals.slotInfo.dungeonHeadTier, config.uiAndVisuals.slotInfo::dungeonHeadTier) { config.uiAndVisuals.slotInfo.dungeonHeadTier = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.enchantedBookLevel"))
					.binding(defaults.uiAndVisuals.slotInfo.enchantedBookLevel, config.uiAndVisuals.slotInfo::enchantedBookLevel) { config.uiAndVisuals.slotInfo.enchantedBookLevel = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.enchantedBookName"))
					.binding(defaults.uiAndVisuals.slotInfo.enchantedBookName, config.uiAndVisuals.slotInfo::enchantedBookName) { config.uiAndVisuals.slotInfo.enchantedBookName = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.kuudraKeyTier"))
					.binding(defaults.uiAndVisuals.slotInfo.kuudraKeyTier, config.uiAndVisuals.slotInfo::kuudraKeyTier) { config.uiAndVisuals.slotInfo.kuudraKeyTier = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.masterSkullTier"))
					.binding(defaults.uiAndVisuals.slotInfo.masterSkullTier, config.uiAndVisuals.slotInfo::masterSkullTier) { config.uiAndVisuals.slotInfo.masterSkullTier = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.masterStarTier"))
					.binding(defaults.uiAndVisuals.slotInfo.masterStarTier, config.uiAndVisuals.slotInfo::masterStarTier) { config.uiAndVisuals.slotInfo.masterStarTier = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.minionTier"))
					.binding(defaults.uiAndVisuals.slotInfo.minionTier, config.uiAndVisuals.slotInfo::minionTier) { config.uiAndVisuals.slotInfo.minionTier = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.newYearCake"))
					.binding(defaults.uiAndVisuals.slotInfo.newYearCake, config.uiAndVisuals.slotInfo::newYearCake) { config.uiAndVisuals.slotInfo.newYearCake = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.petLevel"))
					.binding(defaults.uiAndVisuals.slotInfo.petLevel, config.uiAndVisuals.slotInfo::petLevel) { config.uiAndVisuals.slotInfo.petLevel = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.petItem"))
					.binding(defaults.uiAndVisuals.slotInfo.petItem, config.uiAndVisuals.slotInfo::petItem) { config.uiAndVisuals.slotInfo.petItem = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.petCandy"))
					.binding(defaults.uiAndVisuals.slotInfo.petCandy, config.uiAndVisuals.slotInfo::petCandy) { config.uiAndVisuals.slotInfo.petCandy = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.potionLevel"))
					.binding(defaults.uiAndVisuals.slotInfo.potionLevel, config.uiAndVisuals.slotInfo::potionLevel) { config.uiAndVisuals.slotInfo.potionLevel = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.vacuumPests"))
					.binding(defaults.uiAndVisuals.slotInfo.vacuumPests, config.uiAndVisuals.slotInfo::vacuumPests) { config.uiAndVisuals.slotInfo.vacuumPests = it }
					.controller(TickBoxControllerBuilder::create)
					.build())

				.collapsed(true)
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.hideLightningBolt"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.hideLightningBolt.tooltip")))
					.binding(defaults.uiAndVisuals.renderingTweaks.hideLightningBolt, config.uiAndVisuals.renderingTweaks::hideLightningBolt) { config.uiAndVisuals.renderingTweaks.hideLightningBolt = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing.tooltip")))
					.binding(defaults.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing, config.uiAndVisuals.renderingTweaks::hideOtherPeopleFishing) { config.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson.tooltip")))
					.binding(defaults.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson, config.uiAndVisuals.renderingTweaks::removeFrontFacingThirdPerson) { config.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.uiAndVisuals.swingAnimation"))
				.option(Option.createBuilder<Int>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.swingAnimation.swingDuration"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.swingAnimation.swingDuration.tooltip")))
					.binding(defaults.uiAndVisuals.swingAnimation.swingDuration, config.uiAndVisuals.swingAnimation::swingDuration) { config.uiAndVisuals.swingAnimation.swingDuration = it }
					.controller {
						IntegerSliderControllerBuilder.create(it)
							.step(1)
							.range(1, 60)
							.formatValue { when(it) {
								1 -> Text.translatable("nobaaddons.config.label.unmodified")
								6 -> Text.translatable("nobaaddons.config.label.default")
								else -> Text.literal("$it")
							} }
					}
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.swingAnimation.applyToAllPlayers"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.swingAnimation.applyToAllPlayers.tooltip")))
					.binding(defaults.uiAndVisuals.swingAnimation.applyToAllPlayers, config.uiAndVisuals.swingAnimation::applyToAllPlayers) { config.uiAndVisuals.swingAnimation.applyToAllPlayers = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.collapsed(true)
				.build())

			.group(OptionGroup.createBuilder()
				.name(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering"))
				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelReequip"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelReequip.tooltip")))
					.binding(defaults.uiAndVisuals.itemPosition.cancelEquipAnimation, config.uiAndVisuals.itemPosition::cancelEquipAnimation) { config.uiAndVisuals.itemPosition.cancelEquipAnimation = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelItemUpdate"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelItemUpdate.tooltip")))
					.binding(defaults.uiAndVisuals.itemPosition.cancelItemUpdateAnimation, config.uiAndVisuals.itemPosition::cancelItemUpdateAnimation) { config.uiAndVisuals.itemPosition.cancelItemUpdateAnimation = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Boolean>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelDrinkAnimation"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelDrinkAnimation.tooltip")))
					.binding(defaults.uiAndVisuals.itemPosition.cancelDrinkAnimation, config.uiAndVisuals.itemPosition::cancelDrinkAnimation) { config.uiAndVisuals.itemPosition.cancelDrinkAnimation = it }
					.controller(NobaConfigUtils::createBooleanController)
					.build())

				.option(Option.createBuilder<Int>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.xOffset"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.xOffset.tooltip")))
					.binding(defaults.uiAndVisuals.itemPosition.x, config.uiAndVisuals.itemPosition::x) { config.uiAndVisuals.itemPosition.x = it }
					.controller { IntegerSliderControllerBuilder.create(it).step(1).range(-150, 150) }
					.build())

				.option(Option.createBuilder<Int>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.yOffset"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.yOffset.tooltip")))
					.binding(defaults.uiAndVisuals.itemPosition.y, config.uiAndVisuals.itemPosition::y) { config.uiAndVisuals.itemPosition.y = it }
					.controller { IntegerSliderControllerBuilder.create(it).step(1).range(-150, 150) }
					.build())

				.option(Option.createBuilder<Int>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.zOffset"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.zOffset.tooltip")))
					.binding(defaults.uiAndVisuals.itemPosition.z, config.uiAndVisuals.itemPosition::z) { config.uiAndVisuals.itemPosition.z = it }
					.controller { IntegerSliderControllerBuilder.create(it).step(1).range(-150, 50) }
					.build())

				.option(Option.createBuilder<Float>()
					.name(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.scale"))
					.description(OptionDescription.of(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.scale.tooltip")))
					.binding(defaults.uiAndVisuals.itemPosition.scale, config.uiAndVisuals.itemPosition::scale) { config.uiAndVisuals.itemPosition.scale = it }
					.controller { FloatSliderControllerBuilder.create(it).step(0.1f).range(0.1f, 2f) }
					.build())

				.collapsed(true)
				.build())

			.build()
	}
}