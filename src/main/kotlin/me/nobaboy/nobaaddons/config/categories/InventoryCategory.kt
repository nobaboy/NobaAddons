package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.config.NobaConfigUtils.slider
import me.nobaboy.nobaaddons.config.NobaConfigUtils.tickBox
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.ui.TextShadow
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr

object InventoryCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.inventory", "Inventory")) {
		// region Slot Info
		buildGroup(
			tr("nobaaddons.config.inventory.slotInfo", "Slot Info"),
			tr("nobaaddons.config.inventory.slotInfo.tooltip", "Displays item details such as names and/or tiers on item slots")
		) {
			boolean(
				tr("nobaaddons.config.inventory.slotInfo.checkMarkIfMaxed", "Check Mark if Maxed"),
				tr("nobaaddons.config.inventory.slotInfo.checkMarkIfMaxed.tooltip", "If applicable, display a check mark on the item slot instead of its tier/level when maxed"),
				default = defaults.uiAndVisuals.slotInfo.checkMarkIfMaxed,
				property = config.uiAndVisuals.slotInfo::checkMarkIfMaxed
			)

			label(tr("nobaaddons.config.inventory.slotInfo.label.uiElements", "UI Elements"))

			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.bestiaryMilestone", "Bestiary Milestone"),
				default = defaults.uiAndVisuals.slotInfo.bestiaryMilestone,
				property = config.uiAndVisuals.slotInfo::bestiaryMilestone
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.bestiaryFamilyTier", "Bestiary Family Tier"),
				default = defaults.uiAndVisuals.slotInfo.bestiaryFamilyTier,
				property = config.uiAndVisuals.slotInfo::bestiaryFamilyTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.collectionTier", "Collection Tier"),
				default = defaults.uiAndVisuals.slotInfo.collectionTier,
				property = config.uiAndVisuals.slotInfo::collectionTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.gardenPlotPests", "Garden Plot Pests"),
				default = defaults.uiAndVisuals.slotInfo.gardenPlotPests,
				property = config.uiAndVisuals.slotInfo::gardenPlotPests
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.skillLevel", "Skill Level"),
				default = defaults.uiAndVisuals.slotInfo.skillLevel,
				property = config.uiAndVisuals.slotInfo::skillLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.skyBlockLevel", "SkyBlock Level"),
				default = defaults.uiAndVisuals.slotInfo.skyBlockLevel,
				property = config.uiAndVisuals.slotInfo::skyBlockLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.tuningPoints", "Tuning Points"),
				default = defaults.uiAndVisuals.slotInfo.tuningPoints,
				property = config.uiAndVisuals.slotInfo::tuningPoints
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.trophyFish", "Trophy Fish"),
				tr("nobaaddons.config.inventory.slotInfo.trophyFish.tooltip", "Displays a count of how many of each trophy fish you've caught in Odger's menu"),
				default = defaults.uiAndVisuals.slotInfo.trophyFish,
				property = config.uiAndVisuals.slotInfo::trophyFish
			)

			label(tr("nobaaddons.config.inventory.slotInfo.label.items", "Items"))

			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.attributeShardLevel", "Attribute Shard Level"),
				default = defaults.uiAndVisuals.slotInfo.attributeShardLevel,
				property = config.uiAndVisuals.slotInfo::attributeShardLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.attributeShardName", "Attribute Shard Name"),
				default = defaults.uiAndVisuals.slotInfo.attributeShardName,
				property = config.uiAndVisuals.slotInfo::attributeShardName
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.dungeonHeadTier", "Dungeon Boss Head Tier"),
				default = defaults.uiAndVisuals.slotInfo.dungeonHeadTier,
				property = config.uiAndVisuals.slotInfo::dungeonHeadTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.enchantedBookLevel", "Enchanted Book Level"),
				default = defaults.uiAndVisuals.slotInfo.enchantedBookLevel,
				property = config.uiAndVisuals.slotInfo::enchantedBookLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.enchantedBookName", "Enchanted Book Name"),
				default = defaults.uiAndVisuals.slotInfo.enchantedBookName,
				property = config.uiAndVisuals.slotInfo::enchantedBookName
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.kuudraKeyTier", "Kuudra Key Tier"),
				default = defaults.uiAndVisuals.slotInfo.kuudraKeyTier,
				property = config.uiAndVisuals.slotInfo::kuudraKeyTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.masterSkullTier", "Master Skull Tier"),
				default = defaults.uiAndVisuals.slotInfo.masterSkullTier,
				property = config.uiAndVisuals.slotInfo::masterSkullTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.masterStarTier", "Master Star Tier"),
				default = defaults.uiAndVisuals.slotInfo.masterStarTier,
				property = config.uiAndVisuals.slotInfo::masterStarTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.minionTier", "Minion Tier"),
				default = defaults.uiAndVisuals.slotInfo.minionTier,
				property = config.uiAndVisuals.slotInfo::minionTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.newYearCake", "New Year Cake Year"),
				default = defaults.uiAndVisuals.slotInfo.newYearCake,
				property = config.uiAndVisuals.slotInfo::newYearCake
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.petLevel", "Pet Level"),
				tr("nobaaddons.config.inventory.slotInfo.petLevel.tooltip", "Shows the level of a non-maxed pet"),
				default = defaults.uiAndVisuals.slotInfo.petLevel,
				property = config.uiAndVisuals.slotInfo::petLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.petItem", "Pet Items"),
				tr("nobaaddons.config.inventory.slotInfo.petItem.tooltip", "Shows an icon for certain pet items, like EXP Share or Lucky Clover"),
				default = defaults.uiAndVisuals.slotInfo.petItem,
				property = config.uiAndVisuals.slotInfo::petItem
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.petCandy", "Pet Candy"),
				tr("nobaaddons.config.inventory.slotInfo.petCandy.tooltip", "Displays an icon when a pet has candies applied"),
				default = defaults.uiAndVisuals.slotInfo.petCandy,
				property = config.uiAndVisuals.slotInfo::petCandy
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.potionLevel", "Potion Level"),
				default = defaults.uiAndVisuals.slotInfo.potionLevel,
				property = config.uiAndVisuals.slotInfo::potionLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.ranchersBootsSpeed", "Rancher's Boots Speed"),
				default = defaults.uiAndVisuals.slotInfo.ranchersBootsSpeed,
				property = defaults.uiAndVisuals.slotInfo::ranchersBootsSpeed
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.vacuumPests", "Vacuum Pests"),
				default = defaults.uiAndVisuals.slotInfo.vacuumPests,
				property = config.uiAndVisuals.slotInfo::vacuumPests
			)
		}
		// endregion

		// region Enchantment Tooltips
		buildGroup(tr("nobaaddons.config.inventory.enchantmentTooltips", "Enchantment Tooltips")) {
			boolean(
				tr("nobaaddons.config.inventory.enchantmentTooltips.modifyTooltips", "Modify Enchant Tooltips"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.modifyTooltips.tooltip", "Reformats the enchantment list on items in a style similar to the same feature from Skyblock Addons"),
				default = defaults.uiAndVisuals.enchantmentTooltips.parseItemEnchants,
				property = config.uiAndVisuals.enchantmentTooltips::parseItemEnchants
			)
			boolean(
				tr("nobaaddons.config.inventory.enchantmentTooltips.replaceRomanNumerals", "Replace Roman Numerals"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.replaceRomanNumerals.tooltip", "Enchantment tiers will be replaced with their number representation instead of the original roman numerals used"),
				default = defaults.uiAndVisuals.enchantmentTooltips.replaceRomanNumerals,
				property = config.uiAndVisuals.enchantmentTooltips::replaceRomanNumerals
			)
			cycler(
				tr("nobaaddons.config.inventory.enchantmentTooltips.displayMode", "Display Mode"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.displayMode.tooltip", "Changes how enchantments are displayed on items; Default will follow roughly the same behavior as Hypixel and compact at 6 or more enchants, while Compact will always condense them into as few lines as possible."),
				default = defaults.uiAndVisuals.enchantmentTooltips.displayMode,
				property = config.uiAndVisuals.enchantmentTooltips::displayMode
			)
			boolean(
				tr("nobaaddons.config.inventory.enchantmentTooltips.showDescriptions", "Show Descriptions"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.showDescriptions.tooltip", "Controls whether enchant descriptions will be shown when enchantments aren't compacted (only when Hypixel adds the descriptions); this does not affect enchanted books with a single enchantment, and is not applicable with Compact display mode."),
				default = defaults.uiAndVisuals.enchantmentTooltips.showDescriptions,
				property = config.uiAndVisuals.enchantmentTooltips::showDescriptions
			)
			boolean(
				tr("nobaaddons.config.inventory.enchantmentTooltips.showStacking", "Show Stacking Enchant Progress"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.showStacking.tooltip", "Shows the total value (and progress to next tier if applicable) on stacking enchantments like Champion, Expertise, etc."),
				default = defaults.uiAndVisuals.enchantmentTooltips.showStackingProgress,
				property = config.uiAndVisuals.enchantmentTooltips::showStackingProgress
			)

			color(
				tr("nobaaddons.config.inventory.enchantmentTooltips.maxColor", "Max Enchant Color"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.maxColor.tooltip", "The color used for enchantments at their maximum level"),
				default = defaults.uiAndVisuals.enchantmentTooltips.maxColor,
				property = config.uiAndVisuals.enchantmentTooltips::maxColor
			)
			color(
				tr("nobaaddons.config.inventory.enchantmentTooltips.goodColor", "Good Enchant Color"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.goodColor.tooltip", "The color used for enchantments that are above their max normally obtainable level"),
				default = defaults.uiAndVisuals.enchantmentTooltips.goodColor,
				property = config.uiAndVisuals.enchantmentTooltips::goodColor
			)
			color(
				tr("nobaaddons.config.inventory.enchantmentTooltips.averageColor", "Max Normally Obtainable Enchant Color"),
				// if only mc-auto-translations supported splitting strings onto multiple lines :(
				tr(
					"nobaaddons.config.inventory.enchantmentTooltips.averageColor.tooltip",
					"The color used for enchantments at the max level you can normally obtain them at (either through drops, combining lower tiers, or the enchanting table)\n\nNote that this does not apply to enchants that only have this \"tier 5\" level, and will use Max Enchant Color instead"
				),
				default = defaults.uiAndVisuals.enchantmentTooltips.averageColor,
				property = config.uiAndVisuals.enchantmentTooltips::averageColor
			)
			color(
				tr("nobaaddons.config.inventory.enchantmentTooltips.badColor", "Bad Enchant Color"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.badColor.tooltip", "The color used for enchantments that aren't at any of the above tiers"),
				default = defaults.uiAndVisuals.enchantmentTooltips.badColor,
				property = config.uiAndVisuals.enchantmentTooltips::badColor
			)
		}
		// endregion

		// region Item Pickup Log
		buildGroup(tr("nobaaddons.config.inventory.itemPickupLog", "Item Pickup Log")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.inventory.itemPickupLog.enabled,
				property = config.inventory.itemPickupLog::enabled
			)
			slider(
				tr("nobaaddons.config.inventory.itemPickupLog.timeout", "Expire After"),
				min = 2,
				max = 10,
				step = 1,
				default = defaults.inventory.itemPickupLog.timeoutSeconds,
				property = defaults.inventory.itemPickupLog::timeoutSeconds,
				format = CommonText.Config::seconds
			)
			cycler(
				tr("nobaaddons.config.inventory.itemPickupLog.style", "Text Style"),
				default = TextShadow.SHADOW,
				property = UISettings.itemPickupLog::textShadow
			)
		}
		// endregion
	}
}