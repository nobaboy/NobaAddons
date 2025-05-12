package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.features.inventory.enchants.EnchantmentDisplayMode
import me.nobaboy.nobaaddons.utils.NobaColor

class InventoryConfig {
	@Object val slotInfo = SlotInfo()
	@Object val enchantmentTooltips = EnchantmentTooltips()
	@Object val itemPickupLog = ItemPickupLog()

	class SlotInfo {
		var checkMarkIfMaxed = false

		var bestiaryMilestone = false
		var bestiaryFamilyTier = false
		var collectionTier = false
		var gardenPlotPests = false
		var skillLevel = false
		var skyBlockLevel = false
		var tuningPoints = false
		var trophyFish = false

		var attributeShardLevel = false
		var attributeShardName = false
		var dungeonHeadTier = false
		var enchantedBookLevel = false
		var enchantedBookName = false
		var kuudraKeyTier = false
		var masterSkullTier = false
		var masterStarTier = false
		var minionTier = false
		var newYearCake = false
		var petLevel = false
		var petItem = false
		var petCandy = false
		var potionLevel = false
		var ranchersBootsSpeed = false
		var vacuumPests = false
	}

	class EnchantmentTooltips {
		var modifyTooltips = false
		var replaceRomanNumerals = false
		var displayMode = EnchantmentDisplayMode.NORMAL
		var showDescriptions = false
		var showStackingProgress = false
		var maxColor = NobaColor.GOLD
		var goodColor = NobaColor.GOLD
		var averageColor = NobaColor.BLUE
		var badColor = NobaColor.GRAY
	}

	class ItemPickupLog {
		var enabled = false
		var compactLines = false
		var timeoutSeconds = 5
	}
}