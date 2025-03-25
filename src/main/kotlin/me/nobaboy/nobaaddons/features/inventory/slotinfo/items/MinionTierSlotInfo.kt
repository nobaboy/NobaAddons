package me.nobaboy.nobaaddons.features.inventory.slotinfo.items

object MinionTierSlotInfo : AbstractItemIdTierSlotInfo() {
	override val pattern = Regex("^[A-Z_]+_GENERATOR_(?<tier>\\d+)")
	override val enabled: Boolean get() = config.minionTier
}