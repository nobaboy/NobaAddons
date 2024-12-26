package me.nobaboy.nobaaddons.features.visuals.slotinfo.items

object MasterSkullTierSlotInfo : AbstractItemIdTierSlotInfo() {
	override val pattern = Regex("MASTER_SKULL_TIER_(?<tier>\\d)")
	override val enabled: Boolean get() = config.masterSkullTier
}