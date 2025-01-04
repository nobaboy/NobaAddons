package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.features.inventory.enchants.EnchantmentDisplayMode
import me.nobaboy.nobaaddons.utils.NobaColor

class InventoryConfig : ObjectProperty<InventoryConfig>("inventory") {
	val slotInfo by SlotInfo()
	val enchantmentTooltips by EnchantmentTooltips()
	val itemPickupLog by ItemPickupLog()

	class SlotInfo : ObjectProperty<SlotInfo>("slotInfo") {
		var checkMarkIfMaxed by Property.of<Boolean>("checkMarkIfMaxed", false)

		var bestiaryMilestone by Property.of<Boolean>("bestiaryMilestone", false)
		var bestiaryFamilyTier by Property.of<Boolean>("bestiaryFamilyTier", false)
		var collectionTier by Property.of<Boolean>("collectionTier", false)
		var gardenPlotPests by Property.of<Boolean>("gardenPlotPests", false)
		var skillLevel by Property.of<Boolean>("skillLevel", false)
		var skyBlockLevel by Property.of<Boolean>("skyBlockLevel", false)
		var tuningPoints by Property.of<Boolean>("tuningPoints", false)
		var trophyFish by Property.of<Boolean>("trophyFish", false)

		var attributeShardLevel by Property.of<Boolean>("attributeShardLevel", false)
		var attributeShardName by Property.of<Boolean>("attributeShardName", false)
		var dungeonHeadTier by Property.of<Boolean>("dungeonHeadTier", false)
		var enchantedBookLevel by Property.of<Boolean>("enchantedBookLevel", false)
		var enchantedBookName by Property.of<Boolean>("enchantedBookName", false)
		var kuudraKeyTier by Property.of<Boolean>("kuudraKeyTier", false)
		var masterSkullTier by Property.of<Boolean>("masterSkullTier", false)
		var masterStarTier by Property.of<Boolean>("masterStarTier", false)
		var minionTier by Property.of<Boolean>("minionTier", false)
		var newYearCake by Property.of<Boolean>("newYearCake", false)
		var petLevel by Property.of<Boolean>("petLevel", false)
		var petItem by Property.of<Boolean>("petItem", false)
		var petCandy by Property.of<Boolean>("petCandy", false)
		var potionLevel by Property.of<Boolean>("potionLevel", false)
		var ranchersBootsSpeed by Property.of<Boolean>("ranchersBootsSpeed", false)
		var vacuumPests by Property.of<Boolean>("vacuumPests", false)
	}

	class EnchantmentTooltips : ObjectProperty<EnchantmentTooltips>("enchantmentTooltips") {
		var modifyTooltips by Property.of<Boolean>("modifyTooltips", false)
		var replaceRomanNumerals by Property.of<Boolean>("replaceRomanNumerals", false)
		var displayMode by Property.of("displayMode", Serializer.enum(), EnchantmentDisplayMode.NORMAL)
		var showDescriptions by Property.of<Boolean>("showDescriptions", false)
		var showStackingProgress by Property.of<Boolean>("showStackingProgress", false)
		var maxColor by Property.of("maxColor", Serializer.color, NobaColor.GOLD.toColor())
		var goodColor by Property.of("goodColor", Serializer.color, NobaColor.GOLD.toColor())
		var averageColor by Property.of("averageColor", Serializer.color, NobaColor.BLUE.toColor())
		var badColor by Property.of("badColor", Serializer.color, NobaColor.GRAY.toColor())
	}

	class ItemPickupLog : ObjectProperty<ItemPickupLog>("itemPickupLog") {
		var enabled by Property.of<Boolean>("enabled", false)
		var timeoutSeconds by Property.of<Int>("timeoutSeconds", 5)
	}
}