package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.features.inventory.EnchantDisplayMode
import me.nobaboy.nobaaddons.utils.NobaColor
import java.awt.Color

class InventoryConfig {
	@SerialEntry
	val slotInfo: SlotInfo = SlotInfo()

	@SerialEntry
	val enchantmentTooltips = EnchantmentTooltips()

	class SlotInfo {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var checkMarkIfMaxed: Boolean = false

		@SerialEntry
		var attributeShardLevel: Boolean = false

		@SerialEntry
		var attributeShardName: Boolean = false

		@SerialEntry
		var bestiaryMilestone: Boolean = false

		@SerialEntry
		var bestiaryFamilyTier: Boolean = false

		@SerialEntry
		var collectionTier: Boolean = false

		@SerialEntry
		var dungeonHeadTier: Boolean = false

		@SerialEntry
		var enchantedBookLevel: Boolean = false

		@SerialEntry
		var enchantedBookName: Boolean = false

		@SerialEntry
		var gardenPlotPests: Boolean = false

		@SerialEntry
		var kuudraKeyTier: Boolean = false

		@SerialEntry
		var masterSkullTier: Boolean = false

		@SerialEntry
		var masterStarTier: Boolean = false

		@SerialEntry
		var minionTier: Boolean = false

		@SerialEntry
		var newYearCake: Boolean = false

		@SerialEntry
		var petLevel: Boolean = false

		@SerialEntry
		var petItem: Boolean = false

		@SerialEntry
		var petCandy: Boolean = false

		@SerialEntry
		var potionLevel: Boolean = false

		@SerialEntry
		var ranchersBootsSpeed: Boolean = false

		@SerialEntry
		var skillLevel: Boolean = false

		@SerialEntry
		var skyBlockLevel: Boolean = false

		@SerialEntry
		var tuningPoints: Boolean = false

		@SerialEntry
		var trophyFish: Boolean = false

		@SerialEntry
		var vacuumPests: Boolean = false
	}

	class EnchantmentTooltips {
		@SerialEntry
		var parseItemEnchants: Boolean = false

		@SerialEntry
		var replaceRomanNumerals: Boolean = false

		@SerialEntry
		var displayMode: EnchantDisplayMode = EnchantDisplayMode.NORMAL

		@SerialEntry
		var showDescriptions: Boolean = true

		@SerialEntry
		var showStackingProgress: Boolean = true

		@SerialEntry
		var maxColor: Color = NobaColor.GOLD.toColor()

		@SerialEntry
		var goodColor: Color = NobaColor.GOLD.toColor()

		@SerialEntry
		var averageColor: Color = NobaColor.BLUE.toColor()

		@SerialEntry
		var badColor: Color = NobaColor.GRAY.toColor()
	}
}