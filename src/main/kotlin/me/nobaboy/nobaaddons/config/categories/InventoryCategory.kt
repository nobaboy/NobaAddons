package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.features.inventory.enchants.EnchantmentDisplayMode
import me.nobaboy.nobaaddons.ui.TextShadow
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr

object InventoryCategory {
	fun create() = category(tr("nobaaddons.config.inventory", "Inventory")) {
		slotInfo()
		enchantmentTooltips()
		itemPickupLog()
	}

	private fun ConfigCategory.Builder.slotInfo() {
		group(
			tr("nobaaddons.config.inventory.slotInfo", "Slot Info"),
			tr("nobaaddons.config.inventory.slotInfo.tooltip", "Displays item details such as names and/or tiers on item slots")
		) {
			add({ inventory.slotInfo::checkMarkIfMaxed }) {
				name = tr("nobaaddons.config.inventory.slotInfo.checkMarkIfMaxed", "Check Mark if Maxed")
				descriptionText = tr("nobaaddons.config.inventory.slotInfo.checkMarkIfMaxed.tooltip", "If applicable, display a check mark on the item slot instead of its tier/level when maxed")
				booleanController()
			}

			label(tr("nobaaddons.config.inventory.slotInfo.label.uiElements", "UI Elements"))

			add({ inventory.slotInfo::bestiaryMilestone }) {
				name = tr("nobaaddons.config.inventory.slotInfo.bestiaryMilestone", "Bestiary Milestone")
				tickBoxController()
			}
			add({ inventory.slotInfo::bestiaryFamilyTier }) {
				name = tr("nobaaddons.config.inventory.slotInfo.bestiaryFamilyTier", "Bestiary Family Tier")
				tickBoxController()
			}
			add({ inventory.slotInfo::collectionTier }) {
				name = tr("nobaaddons.config.inventory.slotInfo.collectionTier", "Collection Tier")
				tickBoxController()
			}
			add({ inventory.slotInfo::gardenPlotPests }) {
				name = tr("nobaaddons.config.inventory.slotInfo.gardenPlotPests", "Garden Plot Pests")
				tickBoxController()
			}
			add({ inventory.slotInfo::skillLevel }) {
				name = tr("nobaaddons.config.inventory.slotInfo.skillLevel", "Skill Level")
				tickBoxController()
			}
			add({ inventory.slotInfo::skyBlockLevel }) {
				name = tr("nobaaddons.config.inventory.slotInfo.skyBlockLevel", "SkyBlock Level")
				tickBoxController()
			}
			add({ inventory.slotInfo::tuningPoints }) {
				name = tr("nobaaddons.config.inventory.slotInfo.tuningPoints", "Tuning Points")
				tickBoxController()
			}
			add({ inventory.slotInfo::trophyFish }) {
				name = tr("nobaaddons.config.inventory.slotInfo.trophyFish", "Trophy Fish")
				descriptionText = tr("nobaaddons.config.inventory.slotInfo.trophyFish.tooltip", "Displays a count of how many of each trophy fish you've caught in Odger's menu")
				tickBoxController()
			}

			label(tr("nobaaddons.config.inventory.slotInfo.label.items", "Items"))

			add({ inventory.slotInfo::attributeShardLevel }) {
				name = tr("nobaaddons.config.inventory.slotInfo.attributeShardLevel", "Attribute Shard Level")
				tickBoxController()
			}
			add({ inventory.slotInfo::attributeShardName }) {
				name = tr("nobaaddons.config.inventory.slotInfo.attributeShardName", "Attribute Shard Name")
				tickBoxController()
			}
			add({ inventory.slotInfo::dungeonHeadTier }) {
				name = tr("nobaaddons.config.inventory.slotInfo.dungeonHeadTier", "Dungeon Boss Head Tier")
				tickBoxController()
			}
			add({ inventory.slotInfo::enchantedBookLevel }) {
				name = tr("nobaaddons.config.inventory.slotInfo.enchantedBookLevel", "Enchanted Book Level")
				tickBoxController()
			}
			add({ inventory.slotInfo::enchantedBookName }) {
				name = tr("nobaaddons.config.inventory.slotInfo.enchantedBookName", "Enchanted Book Name")
				tickBoxController()
			}
			add({ inventory.slotInfo::kuudraKeyTier }) {
				name = tr("nobaaddons.config.inventory.slotInfo.kuudraKeyTier", "Kuudra Key Tier")
				tickBoxController()
			}
			add({ inventory.slotInfo::masterSkullTier }) {
				name = tr("nobaaddons.config.inventory.slotInfo.masterSkullTier", "Master Skull Tier")
				tickBoxController()
			}
			add({ inventory.slotInfo::masterStarTier }) {
				name = tr("nobaaddons.config.inventory.slotInfo.masterStarTier", "Master Star Tier")
				tickBoxController()
			}
			add({ inventory.slotInfo::minionTier }) {
				name = tr("nobaaddons.config.inventory.slotInfo.minionTier", "Minion Tier")
				tickBoxController()
			}
			add({ inventory.slotInfo::newYearCake }) {
				name = tr("nobaaddons.config.inventory.slotInfo.newYearCake", "New Year Cake Year")
				tickBoxController()
			}
			add({ inventory.slotInfo::petLevel }) {
				name = tr("nobaaddons.config.inventory.slotInfo.petLevel", "Pet Level")
				descriptionText = tr("nobaaddons.config.inventory.slotInfo.petLevel.tooltip", "Displays the level of a non-maxed pet")
				tickBoxController()
			}
			add({ inventory.slotInfo::petItem }) {
				name = tr("nobaaddons.config.inventory.slotInfo.petItem", "Pet Items")
				descriptionText = tr("nobaaddons.config.inventory.slotInfo.petItem.tooltip", "Displays an icon for specific pet items, such as EXP Share, Lucky Clover, or Tier Boost")
				tickBoxController()
			}
			add({ inventory.slotInfo::petCandy }) {
				name = tr("nobaaddons.config.inventory.slotInfo.petCandy", "Pet Candy")
				descriptionText = tr("nobaaddons.config.inventory.slotInfo.petCandy.tooltip", "Displays an icon when a pet has candies")
				tickBoxController()
			}
			add({ inventory.slotInfo::potionLevel }) {
				name = tr("nobaaddons.config.inventory.slotInfo.potionLevel", "Potion Level")
				tickBoxController()
			}
			add({ inventory.slotInfo::ranchersBootsSpeed }) {
				name = tr("nobaaddons.config.inventory.slotInfo.ranchersBootsSpeed", "Rancher's Boots Speed")
				tickBoxController()
			}
			add({ inventory.slotInfo::vacuumPests }) {
				name = tr("nobaaddons.config.inventory.slotInfo.vacuumPests", "Vacuum Pests")
				tickBoxController()
			}
		}
	}

	private fun ConfigCategory.Builder.enchantmentTooltips() {
		group(tr("nobaaddons.config.inventory.enchantmentTooltips", "Enchantment Tooltips")) {
			val enabled = add({ inventory.enchantmentTooltips::modifyTooltips }) {
				name = tr("nobaaddons.config.inventory.enchantmentTooltips.modifyTooltips", "Modify Enchant Tooltips")
				descriptionText = tr("nobaaddons.config.inventory.enchantmentTooltips.modifyTooltips.tooltip", "Reformats the enchantment list on items in a style similar to the same feature from Skyblock Addons")
				booleanController()
			}
			add({ inventory.enchantmentTooltips::replaceRomanNumerals }) {
				name = tr("nobaaddons.config.inventory.enchantmentTooltips.replaceRomanNumerals", "Replace Roman Numerals")
				descriptionText = tr("nobaaddons.config.inventory.enchantmentTooltips.replaceRomanNumerals.tooltip", "Enchantment tiers will be replaced with their number representation instead of the original roman numerals used")
				require { option(enabled) }
				booleanController()
			}
			val display = add({ inventory.enchantmentTooltips::displayMode }) {
				name = tr("nobaaddons.config.inventory.enchantmentTooltips.displayMode", "Display Mode")
				descriptionText = tr("nobaaddons.config.inventory.enchantmentTooltips.displayMode.tooltip", "Changes how enchantments are displayed on items; Default will follow roughly the same behavior as Hypixel and compact at 6 or more enchants, while Compact will always condense them into as few lines as possible.")
				require { option(enabled) }
				enumController()
			}
			add({ inventory.enchantmentTooltips::showDescriptions }) {
				name = tr("nobaaddons.config.inventory.enchantmentTooltips.showDescriptions", "Show Descriptions")
				descriptionText = tr("nobaaddons.config.inventory.enchantmentTooltips.showDescriptions.tooltip", "Controls whether enchant descriptions will be shown when enchantments aren't compacted (only when Hypixel adds the descriptions); this does not affect enchanted books with a single enchantment, and is not applicable with Compact display mode.")
				require { option(enabled) and option(display) { it != EnchantmentDisplayMode.COMPACT } }
				booleanController()
			}
			add({ inventory.enchantmentTooltips::showStackingProgress }) {
				name = tr("nobaaddons.config.inventory.enchantmentTooltips.showStacking", "Show Stacking Enchant Progress")
				descriptionText = tr("nobaaddons.config.inventory.enchantmentTooltips.showStacking.tooltip", "Shows the total value (and progress to next tier if applicable) on stacking enchantments like Champion, Expertise, etc.")
				require { option(enabled) }
				booleanController()
			}

			add({ inventory.enchantmentTooltips::maxColor }, BiMapper.NobaAWTColorMapper) {
				name = tr("nobaaddons.config.inventory.enchantmentTooltips.maxColor", "Max Enchant Color")
				descriptionText = tr("nobaaddons.config.inventory.enchantmentTooltips.maxColor.tooltip", "The color used for enchantments at their maximum level")
				require { option(enabled) }
				colorController()
			}
			add({ inventory.enchantmentTooltips::goodColor }, BiMapper.NobaAWTColorMapper) {
				name = tr("nobaaddons.config.inventory.enchantmentTooltips.goodColor", "Good Enchant Color")
				descriptionText = tr("nobaaddons.config.inventory.enchantmentTooltips.goodColor.tooltip", "The color used for enchantments that are above their max normally obtainable level")
				require { option(enabled) }
				colorController()
			}
			add({ inventory.enchantmentTooltips::averageColor }, BiMapper.NobaAWTColorMapper) {
				name = tr("nobaaddons.config.inventory.enchantmentTooltips.averageColor", "Max Normally Obtainable Enchant Color")
				// if only mc-auto-translations supported splitting strings onto multiple lines :(
				descriptionText = tr(
					"nobaaddons.config.inventory.enchantmentTooltips.averageColor.tooltip",
					"The color used for enchantments at the max level you can normally obtain them at (either through drops, combining lower tiers, or the enchanting table)\n\nNote that this does not apply to enchants that only have this \"tier 5\" level, and will use Max Enchant Color instead"
				)
				require { option(enabled) }
				colorController()
			}
			add({ inventory.enchantmentTooltips::badColor }, BiMapper.NobaAWTColorMapper) {
				name = tr("nobaaddons.config.inventory.enchantmentTooltips.badColor", "Bad Enchant Color")
				descriptionText = tr("nobaaddons.config.inventory.enchantmentTooltips.badColor.tooltip", "The color used for enchantments that aren't at any of the above tiers")
				require { option(enabled) }
				colorController()
			}
		}
	}

	private fun ConfigCategory.Builder.itemPickupLog() {
		group(tr("nobaaddons.config.inventory.itemPickupLog", "Item Pickup Log")) {
			val enabled = add({ inventory.itemPickupLog::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ inventory.itemPickupLog::timeoutSeconds }) {
				name = tr("nobaaddons.config.inventory.itemPickupLog.timeout", "Expire After")
				require { option(enabled) }
				intSliderController(min = 2, max = 10, format = CommonText.Config::seconds)
			}
			add(Binding.generic(TextShadow.SHADOW, UISettings.itemPickupLog::textShadow, UISettings.itemPickupLog::textShadow.setter)) {
				name = CommonText.Config.TEXT_STYLE
				require { option(enabled) }
				enumController()
			}
		}
	}
}