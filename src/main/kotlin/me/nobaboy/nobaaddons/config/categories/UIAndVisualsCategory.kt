package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.button
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.config.NobaConfigUtils.slider
import me.nobaboy.nobaaddons.config.NobaConfigUtils.tickBox
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesScreen
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.strikethrough
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text
import net.minecraft.text.Texts
import java.util.Calendar

private val year = Calendar.getInstance().get(Calendar.YEAR)

private fun societyIsCrumbling(@Suppress("SameParameterValue") fromYear: Int): Text {
	return Texts.join(buildList {
		addAll((fromYear until year).map { it.toText().strikethrough() })
		add(year.toText())
	}, " ".toText())
}

object UIAndVisualsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.uiAndVisuals", "UI & Visuals")) {
		button(tr("nobaaddons.screen.infoBoxes", "Info Boxes"), text = CommonText.SCREEN_OPEN) {
			MCUtils.client.setScreen(InfoBoxesScreen(it))
		}

		buildGroup(tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints", "Temporary Waypoints")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.uiAndVisuals.temporaryWaypoints.enabled,
				property = config.uiAndVisuals.temporaryWaypoints::enabled
			)
			color(
				tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints.waypointColor", "Waypoint Color"),
				default = defaults.uiAndVisuals.temporaryWaypoints.waypointColor,
				property = config.uiAndVisuals.temporaryWaypoints::waypointColor
			)
			slider(
				tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints.expirationTime", "Expiration Time"),
				tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints.expirationTime.tooltip", "Sets the duration after which a temporary waypoint disappears"),
				default = defaults.uiAndVisuals.temporaryWaypoints.expirationTime,
				property = config.uiAndVisuals.temporaryWaypoints::expirationTime,
				min = 1,
				max = 120,
				step = 1
			)
		}

		buildGroup(tr("nobaaddons.config.uiAndVisuals.etherwarpHelper", "Etherwarp Overlay")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.uiAndVisuals.etherwarpHelper.enabled,
				property = config.uiAndVisuals.etherwarpHelper::enabled
			)
			color(
				tr("nobaaddons.config.uiAndVisuals.etherwarpHelper.highlightColor", "Highlight Color"),
				default = defaults.uiAndVisuals.etherwarpHelper.highlightColor,
				property = config.uiAndVisuals.etherwarpHelper::highlightColor
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.etherwarpHelper.showFailText", "Show Fail Text"),
				tr("nobaaddons.config.uiAndVisuals.etherwarpHelper.showFailText.tooltip", "Displays the reason for an Etherwarp failure below the crosshair"),
				default = defaults.uiAndVisuals.etherwarpHelper.showFailText,
				property = config.uiAndVisuals.etherwarpHelper::showFailText
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.etherwarpHelper.allowOverlayOnAir", "Allow Overlay on Air"),
				tr("nobaaddons.config.uiAndVisuals.etherwarpHelper.allowOverlayOnAir.tooltip", "Allows the overlay to render on air blocks displaying how far you're allowed to teleport"),
				default = defaults.uiAndVisuals.etherwarpHelper.allowOverlayOnAir,
				property = config.uiAndVisuals.etherwarpHelper::allowOverlayOnAir
			)
		}

		buildGroup(
			tr("nobaaddons.config.uiAndVisuals.slotInfo", "Slot Info"),
			tr("nobaaddons.config.uiAndVisuals.slotInfo.tooltip", "Displays item details such as names and/or tiers on item slots")
		) {
			boolean(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.checkMarkIfMaxed", "Check Mark if Maxed"),
				tr("nobaaddons.config.uiAndVisuals.slotInfo.checkMarkIfMaxed.tooltip", "If applicable, display a check mark on the item slot instead of its tier/level when maxed"),
				default = defaults.uiAndVisuals.slotInfo.checkMarkIfMaxed,
				property = config.uiAndVisuals.slotInfo::checkMarkIfMaxed
			)

			label(tr("nobaaddons.config.uiAndVisuals.slotInfo.label.uiElements", "UI Elements"))

			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.bestiaryMilestone", "Bestiary Milestone"),
				default = defaults.uiAndVisuals.slotInfo.bestiaryMilestone,
				property = config.uiAndVisuals.slotInfo::bestiaryMilestone
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.bestiaryFamilyTier", "Bestiary Family Tier"),
				default = defaults.uiAndVisuals.slotInfo.bestiaryFamilyTier,
				property = config.uiAndVisuals.slotInfo::bestiaryFamilyTier
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.collectionTier", "Collection Tier"),
				default = defaults.uiAndVisuals.slotInfo.collectionTier,
				property = config.uiAndVisuals.slotInfo::collectionTier
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.gardenPlotPests", "Garden Plot Pests"),
				default = defaults.uiAndVisuals.slotInfo.gardenPlotPests,
				property = config.uiAndVisuals.slotInfo::gardenPlotPests
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.skillLevel", "Skill Level"),
				default = defaults.uiAndVisuals.slotInfo.skillLevel,
				property = config.uiAndVisuals.slotInfo::skillLevel
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.skyBlockLevel", "SkyBlock Level"),
				default = defaults.uiAndVisuals.slotInfo.skyBlockLevel,
				property = config.uiAndVisuals.slotInfo::skyBlockLevel
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.tuningPoints", "Tuning Points"),
				default = defaults.uiAndVisuals.slotInfo.tuningPoints,
				property = config.uiAndVisuals.slotInfo::tuningPoints
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.trophyFish", "Trophy Fish"),
				tr("nobaaddons.config.uiAndVisuals.slotInfo.trophyFish.tooltip", "Displays a count of how many of each trophy fish you've caught in Odger's menu"),
				default = defaults.uiAndVisuals.slotInfo.trophyFish,
				property = config.uiAndVisuals.slotInfo::trophyFish
			)

			label(tr("nobaaddons.config.uiAndVisuals.slotInfo.label.items", "Items"))

			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.attributeShardLevel", "Attribute Shard Level"),
				default = defaults.uiAndVisuals.slotInfo.attributeShardLevel,
				property = config.uiAndVisuals.slotInfo::attributeShardLevel
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.attributeShardName", "Attribute Shard Name"),
				default = defaults.uiAndVisuals.slotInfo.attributeShardName,
				property = config.uiAndVisuals.slotInfo::attributeShardName
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.dungeonHeadTier", "Dungeon Boss Head Tier"),
				default = defaults.uiAndVisuals.slotInfo.dungeonHeadTier,
				property = config.uiAndVisuals.slotInfo::dungeonHeadTier
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.enchantedBookLevel", "Enchanted Book Level"),
				default = defaults.uiAndVisuals.slotInfo.enchantedBookLevel,
				property = config.uiAndVisuals.slotInfo::enchantedBookLevel
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.enchantedBookName", "Enchanted Book Name"),
				default = defaults.uiAndVisuals.slotInfo.enchantedBookName,
				property = config.uiAndVisuals.slotInfo::enchantedBookName
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.kuudraKeyTier", "Kuudra Key Tier"),
				default = defaults.uiAndVisuals.slotInfo.kuudraKeyTier,
				property = config.uiAndVisuals.slotInfo::kuudraKeyTier
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.masterSkullTier", "Master Skull Tier"),
				default = defaults.uiAndVisuals.slotInfo.masterSkullTier,
				property = config.uiAndVisuals.slotInfo::masterSkullTier
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.masterStarTier", "Master Star Tier"),
				default = defaults.uiAndVisuals.slotInfo.masterStarTier,
				property = config.uiAndVisuals.slotInfo::masterStarTier
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.minionTier", "Minion Tier"),
				default = defaults.uiAndVisuals.slotInfo.minionTier,
				property = config.uiAndVisuals.slotInfo::minionTier
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.newYearCake", "New Year Cake Year"),
				default = defaults.uiAndVisuals.slotInfo.newYearCake,
				property = config.uiAndVisuals.slotInfo::newYearCake
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.petLevel", "Pet Level"),
				tr("nobaaddons.config.uiAndVisuals.slotInfo.petLevel.tooltip", "Shows the level of a non-maxed pet"),
				default = defaults.uiAndVisuals.slotInfo.petLevel,
				property = config.uiAndVisuals.slotInfo::petLevel
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.petItem", "Pet Items"),
				tr("nobaaddons.config.uiAndVisuals.slotInfo.petItem.tooltip", "Shows an icon for certain pet items, like EXP Share or Lucky Clover"),
				default = defaults.uiAndVisuals.slotInfo.petItem,
				property = config.uiAndVisuals.slotInfo::petItem
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.petCandy", "Pet Candy"),
				tr("nobaaddons.config.uiAndVisuals.slotInfo.petCandy.tooltip", "Displays an icon when a pet has candies applied"),
				default = defaults.uiAndVisuals.slotInfo.petCandy,
				property = config.uiAndVisuals.slotInfo::petCandy
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.potionLevel", "Potion Level"),
				default = defaults.uiAndVisuals.slotInfo.potionLevel,
				property = config.uiAndVisuals.slotInfo::potionLevel
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.ranchersBootsSpeed", "Rancher's Boots Speed"),
				default = defaults.uiAndVisuals.slotInfo.ranchersBootsSpeed,
				property = defaults.uiAndVisuals.slotInfo::ranchersBootsSpeed
			)
			tickBox(
				tr("nobaaddons.config.uiAndVisuals.slotInfo.vacuumPests", "Vacuum Pests"),
				default = defaults.uiAndVisuals.slotInfo.vacuumPests,
				property = config.uiAndVisuals.slotInfo::vacuumPests
			)
		}

		buildGroup(tr("nobaaddons.config.uiAndVisuals.renderingTweaks", "Rendering Tweaks")) {
			boolean(
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideLightningBolt", "Hide Lightning Bolt"),
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideLightningBolt.tooltip", "Prevents lightning bolts from rendering"),
				default = defaults.uiAndVisuals.renderingTweaks.hideLightningBolt,
				property = config.uiAndVisuals.renderingTweaks::hideLightningBolt
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing", "Hide Other People Fishing"),
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing.tooltip", "Hides the fishing bobber of other players"),
				default = defaults.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing,
				property = config.uiAndVisuals.renderingTweaks::hideOtherPeopleFishing
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson", "Remove Front-Facing Third Person"),
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson.tooltip", "Removes the front-facing perspective from F5"),
				default = defaults.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson,
				property = config.uiAndVisuals.renderingTweaks::removeFrontFacingThirdPerson
			)
		}

		buildGroup(tr("nobaaddons.config.uiAndVisuals.swingAnimation", "Arm Swing Animation Tweaks")) {
			slider(
				tr("nobaaddons.config.uiAndVisuals.swingAnimation.swingDuration", "Swing Duration"),
				tr("nobaaddons.config.uiAndVisuals.swingAnimation.swingDuration.tooltip", "Controls how long your arm swing animation duration is, ignoring all effects like Haste"),
				default = defaults.uiAndVisuals.swingAnimation.swingDuration,
				property = config.uiAndVisuals.swingAnimation::swingDuration,
				min = 1,
				max = 60,
				step = 1,
			) {
				when(it) {
					1 -> tr("nobaaddons.config.label.unmodified", "Unmodified")
					6 -> tr("nobaaddons.config.label.default", "Default")
					else -> it.toString().toText()
				}
			}

			boolean(
				tr("nobaaddons.config.uiAndVisuals.swingAnimation.applyToAllPlayers", "Apply to All Players"),
				tr("nobaaddons.config.uiAndVisuals.swingAnimation.applyToAllPlayers.tooltip", "If enabled, the above swing duration will also apply to all players, insteada of only yourself"),
				default = defaults.uiAndVisuals.swingAnimation.applyToAllPlayers,
				property = config.uiAndVisuals.swingAnimation::applyToAllPlayers,
			)
		}

		buildGroup(tr("nobaaddons.config.uiAndVisuals.itemRendering", "First Person Item Rendering")) {
			boolean(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelReequip", "Cancel Re-equip Animation"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelReequip.tooltip", "Prevents the item update animation from playing entirely, including when switching items"),
				default = defaults.uiAndVisuals.itemPosition.cancelEquipAnimation,
				property = config.uiAndVisuals.itemPosition::cancelEquipAnimation
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelItemUpdate", "Cancel Item Update Animation"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelItemUpdate.tooltip", "Prevents the item update animation from playing when your held item is updated"),
				default = defaults.uiAndVisuals.itemPosition.cancelItemUpdateAnimation,
				property = config.uiAndVisuals.itemPosition::cancelItemUpdateAnimation
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelDrinkAnimation", "Cancel Item Consume Animation"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelDrinkAnimation.tooltip", "Prevents the item consume animation (such as from drinking potions) from playing"),
				default = defaults.uiAndVisuals.itemPosition.cancelDrinkAnimation,
				property = config.uiAndVisuals.itemPosition::cancelDrinkAnimation
			)
			slider(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.xOffset", "Held Item X Offset"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.xOffset.tooltip", "Moves the held item model in first person left or right across the screen; note that this slider is inverted if your main hand is set to left handed"),
				default = defaults.uiAndVisuals.itemPosition.x,
				property = config.uiAndVisuals.itemPosition::x,
				min = -150,
				max = 150,
				step = 1
			)
			slider(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.yOffset", "Held Item Y Offset"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.yOffset.tooltip", "Moves the held item up and down across the screen"),
				default = defaults.uiAndVisuals.itemPosition.y,
				property = config.uiAndVisuals.itemPosition::y,
				min = -150,
				max = 150,
				step = 1
			)
			slider(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.zOffset", "Held Item Z Offset"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.zOffset.tooltip", "Moves the held item towards or away from the camera"),
				default = defaults.uiAndVisuals.itemPosition.z,
				property = config.uiAndVisuals.itemPosition::z,
				min = -150,
				max = 50,
				step = 1
			)
			slider(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.scale", "Held Item Scale"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.scale.tooltip", "Controls how large the held item is rendered in first person"),
				default = defaults.uiAndVisuals.itemPosition.scale,
				property = config.uiAndVisuals.itemPosition::scale,
				min = 0.1f,
				max = 2.0f,
				step = 0.1f
			)
		}

		buildGroup(tr("nobaaddons.config.uiAndVisuals.armorGlints", "Armor Glint Tweaks")) {
			boolean(
				tr("nobaaddons.config.uiAndVisuals.armorGlints.fixGlints", "Fix Armor Enchant Glints"),
				tr("nobaaddons.config.uiAndVisuals.armorGlints.fixGlints.tooltip", "It's ${societyIsCrumbling(2019)}. Society has progressed little in the past 20 years. The world is falling apart. Hypixel still has yet to figure out how to consistently add an enchantment glint to armor."),
				default = defaults.uiAndVisuals.renderingTweaks.fixEnchantedArmorGlint,
				property = config.uiAndVisuals.renderingTweaks::fixEnchantedArmorGlint
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.armorGlints.removeGlints", "Remove All Armor Enchant Glints"),
				tr("nobaaddons.config.uiAndVisuals.armorGlints.removeGlints.tooltip", "Removes enchantment glints from all armor pieces"),
				default = defaults.uiAndVisuals.renderingTweaks.removeArmorGlints,
				property = config.uiAndVisuals.renderingTweaks::removeArmorGlints
			)
		}

		buildGroup(tr("nobaaddons.config.uiAndVisuals.enchants", "Enchantment Tooltips")) {
			boolean(
				tr("nobaaddons.config.uiAndVisuals.enchants.parseEnchants", "Modify Enchant Tooltips"),
				tr("nobaaddons.config.uiAndVisuals.enchants.parseEnchants.tooltip", "Reformats the enchantment list on items in a style similar to the same feature from Skyblock Addons"),
				default = defaults.uiAndVisuals.enchantments.parseItemEnchants,
				property = config.uiAndVisuals.enchantments::parseItemEnchants
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.enchants.replaceRomanNumerals", "Replace Roman Numerals"),
				tr("nobaaddons.config.uiAndVisuals.enchants.replaceRomanNumerals.tooltip", "Enchantment tiers will be replaced with their number representation instead of the original roman numerals used"),
				default = defaults.uiAndVisuals.enchantments.replaceRomanNumerals,
				property = config.uiAndVisuals.enchantments::replaceRomanNumerals
			)
			cycler(
				tr("nobaaddons.config.uiAndVisuals.enchants.displayMode", "Display Mode"),
				tr("nobaaddons.config.uiAndVisuals.enchants.displayMode.tooltip", "Changes how enchantments are displayed on items; Default will follow roughly the same behavior as Hypixel and compact at 6 or more enchants, while Compact will always condense them into as few lines as possible."),
				default = defaults.uiAndVisuals.enchantments.displayMode,
				property = config.uiAndVisuals.enchantments::displayMode
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.enchants.showDescriptions", "Show Descriptions"),
				tr("nobaaddons.config.uiAndVisuals.enchants.showDescriptions.tooltip", "Controls whether enchant descriptions will be shown when enchantments aren't compacted (only when Hypixel adds the descriptions); this does not affect enchanted books with a single enchantment, and is not applicable with Compact display mode."),
				default = defaults.uiAndVisuals.enchantments.showDescriptions,
				property = config.uiAndVisuals.enchantments::showDescriptions
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.enchants.showStacking", "Show Stacking Enchant Progress"),
				tr("nobaaddons.config.uiAndVisuals.enchants.showStacking.tooltip", "Shows the total value (and progress to next tier if applicable) on stacking enchantments like Champion, Expertise, etc."),
				default = defaults.uiAndVisuals.enchantments.showStackingProgress,
				property = config.uiAndVisuals.enchantments::showStackingProgress
			)

			color(
				tr("nobaaddons.config.uiAndVisuals.enchants.maxColor", "Max Enchant Color"),
				tr("nobaaddons.config.uiAndVisuals.enchants.maxColor.tooltip", "The color used for enchantments at their maximum level"),
				default = defaults.uiAndVisuals.enchantments.maxColor,
				property = config.uiAndVisuals.enchantments::maxColor
			)
			color(
				tr("nobaaddons.config.uiAndVisuals.enchants.goodColor", "Good Enchant Color"),
				tr("nobaaddons.config.uiAndVisuals.enchants.goodColor.tooltip", "The color used for enchantments that are above their max normally obtainable level"),
				default = defaults.uiAndVisuals.enchantments.goodColor,
				property = config.uiAndVisuals.enchantments::goodColor
			)
			color(
				tr("nobaaddons.config.uiAndVisuals.enchants.averageColor", "Max Normally Obtainable Enchant Color"),
				// if only mc-auto-translations supported splitting strings onto multiple lines :(
				tr(
					"nobaaddons.config.uiAndVisuals.enchants.averageColor.tooltip",
					"The color used for enchantments at the max level you can normally obtain them at (either through drops, combining lower tiers, or the enchanting table)\n\nNote that this does not apply to enchants that only have this \"tier 5\" level, and will use Max Enchant Color instead"
				),
				default = defaults.uiAndVisuals.enchantments.averageColor,
				property = config.uiAndVisuals.enchantments::averageColor
			)
			color(
				tr("nobaaddons.config.uiAndVisuals.enchants.badColor", "Bad Enchant Color"),
				tr("nobaaddons.config.uiAndVisuals.enchants.badColor.tooltip", "The color used for enchantments that aren't at any of the above tiers"),
				default = defaults.uiAndVisuals.enchantments.badColor,
				property = config.uiAndVisuals.enchantments::badColor
			)
		}
	}
}