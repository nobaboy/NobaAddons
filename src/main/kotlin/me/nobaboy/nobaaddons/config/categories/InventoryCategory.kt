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
				default = defaults.inventory.slotInfo.checkMarkIfMaxed,
				property = config.inventory.slotInfo::checkMarkIfMaxed
			)

			label(tr("nobaaddons.config.inventory.slotInfo.label.uiElements", "UI Elements"))

			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.bestiaryMilestone", "Bestiary Milestone"),
				default = defaults.inventory.slotInfo.bestiaryMilestone,
				property = config.inventory.slotInfo::bestiaryMilestone
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.bestiaryFamilyTier", "Bestiary Family Tier"),
				default = defaults.inventory.slotInfo.bestiaryFamilyTier,
				property = config.inventory.slotInfo::bestiaryFamilyTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.collectionTier", "Collection Tier"),
				default = defaults.inventory.slotInfo.collectionTier,
				property = config.inventory.slotInfo::collectionTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.gardenPlotPests", "Garden Plot Pests"),
				default = defaults.inventory.slotInfo.gardenPlotPests,
				property = config.inventory.slotInfo::gardenPlotPests
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.skillLevel", "Skill Level"),
				default = defaults.inventory.slotInfo.skillLevel,
				property = config.inventory.slotInfo::skillLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.skyBlockLevel", "SkyBlock Level"),
				default = defaults.inventory.slotInfo.skyBlockLevel,
				property = config.inventory.slotInfo::skyBlockLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.tuningPoints", "Tuning Points"),
				default = defaults.inventory.slotInfo.tuningPoints,
				property = config.inventory.slotInfo::tuningPoints
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.trophyFish", "Trophy Fish"),
				tr("nobaaddons.config.inventory.slotInfo.trophyFish.tooltip", "Displays a count of how many of each trophy fish you've caught in Odger's menu"),
				default = defaults.inventory.slotInfo.trophyFish,
				property = config.inventory.slotInfo::trophyFish
			)

			label(tr("nobaaddons.config.inventory.slotInfo.label.items", "Items"))

			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.attributeShardLevel", "Attribute Shard Level"),
				default = defaults.inventory.slotInfo.attributeShardLevel,
				property = config.inventory.slotInfo::attributeShardLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.attributeShardName", "Attribute Shard Name"),
				default = defaults.inventory.slotInfo.attributeShardName,
				property = config.inventory.slotInfo::attributeShardName
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.dungeonHeadTier", "Dungeon Boss Head Tier"),
				default = defaults.inventory.slotInfo.dungeonHeadTier,
				property = config.inventory.slotInfo::dungeonHeadTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.enchantedBookLevel", "Enchanted Book Level"),
				default = defaults.inventory.slotInfo.enchantedBookLevel,
				property = config.inventory.slotInfo::enchantedBookLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.enchantedBookName", "Enchanted Book Name"),
				default = defaults.inventory.slotInfo.enchantedBookName,
				property = config.inventory.slotInfo::enchantedBookName
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.kuudraKeyTier", "Kuudra Key Tier"),
				default = defaults.inventory.slotInfo.kuudraKeyTier,
				property = config.inventory.slotInfo::kuudraKeyTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.masterSkullTier", "Master Skull Tier"),
				default = defaults.inventory.slotInfo.masterSkullTier,
				property = config.inventory.slotInfo::masterSkullTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.masterStarTier", "Master Star Tier"),
				default = defaults.inventory.slotInfo.masterStarTier,
				property = config.inventory.slotInfo::masterStarTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.minionTier", "Minion Tier"),
				default = defaults.inventory.slotInfo.minionTier,
				property = config.inventory.slotInfo::minionTier
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.newYearCake", "New Year Cake Year"),
				default = defaults.inventory.slotInfo.newYearCake,
				property = config.inventory.slotInfo::newYearCake
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.petLevel", "Pet Level"),
				tr("nobaaddons.config.inventory.slotInfo.petLevel.tooltip", "Shows the level of a non-maxed pet"),
				default = defaults.inventory.slotInfo.petLevel,
				property = config.inventory.slotInfo::petLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.petItem", "Pet Items"),
				tr("nobaaddons.config.inventory.slotInfo.petItem.tooltip", "Shows an icon for certain pet items, like EXP Share or Lucky Clover"),
				default = defaults.inventory.slotInfo.petItem,
				property = config.inventory.slotInfo::petItem
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.petCandy", "Pet Candy"),
				tr("nobaaddons.config.inventory.slotInfo.petCandy.tooltip", "Displays an icon when a pet has candies applied"),
				default = defaults.inventory.slotInfo.petCandy,
				property = config.inventory.slotInfo::petCandy
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.potionLevel", "Potion Level"),
				default = defaults.inventory.slotInfo.potionLevel,
				property = config.inventory.slotInfo::potionLevel
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.ranchersBootsSpeed", "Rancher's Boots Speed"),
				default = defaults.inventory.slotInfo.ranchersBootsSpeed,
				property = defaults.inventory.slotInfo::ranchersBootsSpeed
			)
			tickBox(
				tr("nobaaddons.config.inventory.slotInfo.vacuumPests", "Vacuum Pests"),
				default = defaults.inventory.slotInfo.vacuumPests,
				property = config.inventory.slotInfo::vacuumPests
			)
		}
		// endregion

		// region Enchantment Tooltips
		buildGroup(tr("nobaaddons.config.inventory.enchantmentTooltips", "Enchantment Tooltips")) {
			boolean(
				tr("nobaaddons.config.inventory.enchantmentTooltips.modifyTooltips", "Modify Enchant Tooltips"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.modifyTooltips.tooltip", "Reformats the enchantment list on items in a style similar to the same feature from Skyblock Addons"),
				default = defaults.inventory.enchantmentTooltips.modifyTooltips,
				property = config.inventory.enchantmentTooltips::modifyTooltips
			)
			boolean(
				tr("nobaaddons.config.inventory.enchantmentTooltips.replaceRomanNumerals", "Replace Roman Numerals"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.replaceRomanNumerals.tooltip", "Enchantment tiers will be replaced with their number representation instead of the original roman numerals used"),
				default = defaults.inventory.enchantmentTooltips.replaceRomanNumerals,
				property = config.inventory.enchantmentTooltips::replaceRomanNumerals
			)
			cycler(
				tr("nobaaddons.config.inventory.enchantmentTooltips.displayMode", "Display Mode"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.displayMode.tooltip", "Changes how enchantments are displayed on items; Default will follow roughly the same behavior as Hypixel and compact at 6 or more enchants, while Compact will always condense them into as few lines as possible."),
				default = defaults.inventory.enchantmentTooltips.displayMode,
				property = config.inventory.enchantmentTooltips::displayMode
			)
			boolean(
				tr("nobaaddons.config.inventory.enchantmentTooltips.showDescriptions", "Show Descriptions"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.showDescriptions.tooltip", "Controls whether enchant descriptions will be shown when enchantments aren't compacted (only when Hypixel adds the descriptions); this does not affect enchanted books with a single enchantment, and is not applicable with Compact display mode."),
				default = defaults.inventory.enchantmentTooltips.showDescriptions,
				property = config.inventory.enchantmentTooltips::showDescriptions
			)
			boolean(
				tr("nobaaddons.config.inventory.enchantmentTooltips.showStacking", "Show Stacking Enchant Progress"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.showStacking.tooltip", "Shows the total value (and progress to next tier if applicable) on stacking enchantments like Champion, Expertise, etc."),
				default = defaults.inventory.enchantmentTooltips.showStackingProgress,
				property = config.inventory.enchantmentTooltips::showStackingProgress
			)

			color(
				tr("nobaaddons.config.inventory.enchantmentTooltips.maxColor", "Max Enchant Color"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.maxColor.tooltip", "The color used for enchantments at their maximum level"),
				default = defaults.inventory.enchantmentTooltips.maxColor,
				property = config.inventory.enchantmentTooltips::maxColor
			)
			color(
				tr("nobaaddons.config.inventory.enchantmentTooltips.goodColor", "Good Enchant Color"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.goodColor.tooltip", "The color used for enchantments that are above their max normally obtainable level"),
				default = defaults.inventory.enchantmentTooltips.goodColor,
				property = config.inventory.enchantmentTooltips::goodColor
			)
			color(
				tr("nobaaddons.config.inventory.enchantmentTooltips.averageColor", "Max Normally Obtainable Enchant Color"),
				// if only mc-auto-translations supported splitting strings onto multiple lines :(
				tr(
					"nobaaddons.config.inventory.enchantmentTooltips.averageColor.tooltip",
					"The color used for enchantments at the max level you can normally obtain them at (either through drops, combining lower tiers, or the enchanting table)\n\nNote that this does not apply to enchants that only have this \"tier 5\" level, and will use Max Enchant Color instead"
				),
				default = defaults.inventory.enchantmentTooltips.averageColor,
				property = config.inventory.enchantmentTooltips::averageColor
			)
			color(
				tr("nobaaddons.config.inventory.enchantmentTooltips.badColor", "Bad Enchant Color"),
				tr("nobaaddons.config.inventory.enchantmentTooltips.badColor.tooltip", "The color used for enchantments that aren't at any of the above tiers"),
				default = defaults.inventory.enchantmentTooltips.badColor,
				property = config.inventory.enchantmentTooltips::badColor
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