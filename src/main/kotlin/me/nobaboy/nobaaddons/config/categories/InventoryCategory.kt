package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.config.NobaConfigUtils.slider
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr

object InventoryCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.inventory", "Inventory")) {
		// region Item Pickup Log
		buildGroup(tr("nobaaddons.config.inventory.pickupLog", "Item Pickup Log")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.inventory.pickupLog.enabled,
				property = config.inventory.pickupLog::enabled
			)
			slider(
				tr("nobaaddons.config.inventory.pickupLog.timeout", "Expire After Seconds"),
				min = 1,
				max = 10,
				step = 1,
				default = defaults.inventory.pickupLog.timeoutSeconds,
				property = defaults.inventory.pickupLog::timeoutSeconds
			)
		}
		// endregion

		// region Enchant Tooltip Parsing
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
		// endregion
	}
}