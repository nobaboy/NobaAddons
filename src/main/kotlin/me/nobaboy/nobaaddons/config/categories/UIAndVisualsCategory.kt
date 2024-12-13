package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.config.NobaConfigUtils.slider
import me.nobaboy.nobaaddons.config.NobaConfigUtils.tickBox
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesScreen
import me.nobaboy.nobaaddons.utils.MCUtils
import net.minecraft.text.Text

object UIAndVisualsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig): ConfigCategory {
		return ConfigCategory.createBuilder()
			.name(Text.translatable("nobaaddons.config.uiAndVisuals"))

			.option(ButtonOption.createBuilder()
				.name(Text.translatable("nobaaddons.screen.infoBoxes"))
				.text(Text.translatable("nobaaddons.screen.button.open"))
				.action { screen, option -> MCUtils.client.setScreen(InfoBoxesScreen(screen)) }
				.build())

			.buildGroup(Text.translatable("nobaaddons.config.uiAndVisuals.temporaryWaypoints")) {
				boolean(
					Text.translatable("nobaaddons.config.enabled"),
					default = defaults.uiAndVisuals.temporaryWaypoints.enabled,
					property = config.uiAndVisuals.temporaryWaypoints::enabled
				)
				color(
					Text.translatable("nobaaddons.config.uiAndVisuals.temporaryWaypoints.waypointColor"),
					default = defaults.uiAndVisuals.temporaryWaypoints.waypointColor,
					property = config.uiAndVisuals.temporaryWaypoints::waypointColor
				)
				slider(
					Text.translatable("nobaaddons.config.uiAndVisuals.temporaryWaypoints.expirationTime"),
					Text.translatable("nobaaddons.config.uiAndVisuals.temporaryWaypoints.expirationTime.tooltip"),
					default = defaults.uiAndVisuals.temporaryWaypoints.expirationTime,
					property = config.uiAndVisuals.temporaryWaypoints::expirationTime,
					min = 1,
					max = 120,
					step = 1
				)
			}

			.buildGroup(Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper")) {
				boolean(
					Text.translatable("nobaaddons.config.enabled"),
					default = defaults.uiAndVisuals.etherwarpHelper.enabled,
					property = config.uiAndVisuals.etherwarpHelper::enabled
				)
				color(
					Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.highlightColor"),
					default = defaults.uiAndVisuals.etherwarpHelper.highlightColor,
					property = config.uiAndVisuals.etherwarpHelper::highlightColor
				)
				boolean(
					Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.showFailText"),
					Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.showFailText.tooltip"),
					default = defaults.uiAndVisuals.etherwarpHelper.showFailText,
					property = config.uiAndVisuals.etherwarpHelper::showFailText
				)
				boolean(
					Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.allowOverlayOnAir"),
					Text.translatable("nobaaddons.config.uiAndVisuals.etherwarpHelper.allowOverlayOnAir.tooltip"),
					default = defaults.uiAndVisuals.etherwarpHelper.allowOverlayOnAir,
					property = config.uiAndVisuals.etherwarpHelper::allowOverlayOnAir
				)
			}

			.buildGroup(
				Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo"),
				Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.tooltip")
			) {
				boolean(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.checkMarkIfMaxed"),
					default = defaults.uiAndVisuals.slotInfo.checkMarkIfMaxed,
					property = config.uiAndVisuals.slotInfo::checkMarkIfMaxed
				)

				label(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.label.uiElements"))

				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.bestiaryMilestone"),
					default = defaults.uiAndVisuals.slotInfo.bestiaryMilestone,
					property = config.uiAndVisuals.slotInfo::bestiaryMilestone
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.bestiaryFamilyTier"),
					default = defaults.uiAndVisuals.slotInfo.bestiaryFamilyTier,
					property = config.uiAndVisuals.slotInfo::bestiaryFamilyTier
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.collectionTier"),
					default = defaults.uiAndVisuals.slotInfo.collectionTier,
					property = config.uiAndVisuals.slotInfo::collectionTier
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.gardenPlotPests"),
					default = defaults.uiAndVisuals.slotInfo.gardenPlotPests,
					property = config.uiAndVisuals.slotInfo::gardenPlotPests
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.skillLevel"),
					default = defaults.uiAndVisuals.slotInfo.skillLevel,
					property = config.uiAndVisuals.slotInfo::skillLevel
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.skyBlockLevel"),
					default = defaults.uiAndVisuals.slotInfo.skyBlockLevel,
					property = config.uiAndVisuals.slotInfo::skyBlockLevel
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.tuningPoints"),
					default = defaults.uiAndVisuals.slotInfo.tuningPoints,
					property = config.uiAndVisuals.slotInfo::tuningPoints
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.trophyFish"),
					default = defaults.uiAndVisuals.slotInfo.trophyFish,
					property = config.uiAndVisuals.slotInfo::trophyFish
				)

				label(Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.label.items"))

				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.dungeonHeadTier"),
					default = defaults.uiAndVisuals.slotInfo.dungeonHeadTier,
					property = config.uiAndVisuals.slotInfo::dungeonHeadTier
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.enchantedBookLevel"),
					default = defaults.uiAndVisuals.slotInfo.enchantedBookLevel,
					property = config.uiAndVisuals.slotInfo::enchantedBookLevel
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.enchantedBookName"),
					default = defaults.uiAndVisuals.slotInfo.enchantedBookName,
					property = config.uiAndVisuals.slotInfo::enchantedBookName
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.kuudraKeyTier"),
					default = defaults.uiAndVisuals.slotInfo.kuudraKeyTier,
					property = config.uiAndVisuals.slotInfo::kuudraKeyTier
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.masterSkullTier"),
					default = defaults.uiAndVisuals.slotInfo.masterSkullTier,
					property = config.uiAndVisuals.slotInfo::masterSkullTier
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.masterStarTier"),
					default = defaults.uiAndVisuals.slotInfo.masterStarTier,
					property = config.uiAndVisuals.slotInfo::masterStarTier
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.minionTier"),
					default = defaults.uiAndVisuals.slotInfo.minionTier,
					property = config.uiAndVisuals.slotInfo::minionTier
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.newYearCake"),
					default = defaults.uiAndVisuals.slotInfo.newYearCake,
					property = config.uiAndVisuals.slotInfo::newYearCake
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.petLevel"),
					default = defaults.uiAndVisuals.slotInfo.petLevel,
					property = config.uiAndVisuals.slotInfo::petLevel
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.petItem"),
					default = defaults.uiAndVisuals.slotInfo.petItem,
					property = config.uiAndVisuals.slotInfo::petItem
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.petCandy"),
					default = defaults.uiAndVisuals.slotInfo.petCandy,
					property = config.uiAndVisuals.slotInfo::petCandy
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.potionLevel"),
					default = defaults.uiAndVisuals.slotInfo.potionLevel,
					property = config.uiAndVisuals.slotInfo::potionLevel
				)
				tickBox(
					Text.translatable("nobaaddons.config.uiAndVisuals.slotInfo.vacuumPests"),
					default = defaults.uiAndVisuals.slotInfo.vacuumPests,
					property = config.uiAndVisuals.slotInfo::vacuumPests
				)
			}

			.buildGroup(Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks")) {
				boolean(
					Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.hideLightningBolt"),
					Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.hideLightningBolt.tooltip"),
					default = defaults.uiAndVisuals.renderingTweaks.hideLightningBolt,
					property = config.uiAndVisuals.renderingTweaks::hideLightningBolt
				)
				boolean(
					Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing"),
					Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing.tooltip"),
					default = defaults.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing,
					property = config.uiAndVisuals.renderingTweaks::hideOtherPeopleFishing
				)
				boolean(
					Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson"),
					Text.translatable("nobaaddons.config.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson.tooltip"),
					default = defaults.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson,
					property = config.uiAndVisuals.renderingTweaks::removeFrontFacingThirdPerson
				)
			}

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

			.buildGroup(Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering")) {
				boolean(
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelReequip"),
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelReequip.tooltip"),
					default = defaults.uiAndVisuals.itemPosition.cancelEquipAnimation,
					property = config.uiAndVisuals.itemPosition::cancelEquipAnimation
				)
				boolean(
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelItemUpdate"),
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelItemUpdate.tooltip"),
					default = defaults.uiAndVisuals.itemPosition.cancelItemUpdateAnimation,
					property = config.uiAndVisuals.itemPosition::cancelItemUpdateAnimation
				)
				boolean(
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelDrinkAnimation"),
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.cancelDrinkAnimation.tooltip"),
					default = defaults.uiAndVisuals.itemPosition.cancelDrinkAnimation,
					property = config.uiAndVisuals.itemPosition::cancelDrinkAnimation
				)
				slider(
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.xOffset"),
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.xOffset.tooltip"),
					default = defaults.uiAndVisuals.itemPosition.x,
					property = config.uiAndVisuals.itemPosition::x,
					min = -150,
					max = 150,
					step = 1
				)
				slider(
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.yOffset"),
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.yOffset.tooltip"),
					default = defaults.uiAndVisuals.itemPosition.y,
					property = config.uiAndVisuals.itemPosition::y,
					min = -150,
					max = 150,
					step = 1
				)
				slider(
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.zOffset"),
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.zOffset.tooltip"),
					default = defaults.uiAndVisuals.itemPosition.z,
					property = config.uiAndVisuals.itemPosition::z,
					min = -150,
					max = 50,
					step = 1
				)
				slider(
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.scale"),
					Text.translatable("nobaaddons.config.uiAndVisuals.itemRendering.scale.tooltip"),
					default = defaults.uiAndVisuals.itemPosition.scale,
					property = config.uiAndVisuals.itemPosition::scale,
					min = 0.1f,
					max = 2.0f,
					step = 0.1f
				)
			}

			.build()
	}
}