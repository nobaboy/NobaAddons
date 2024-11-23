package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.data.InventoryData
import me.nobaboy.nobaaddons.events.internal.EventDispatcher
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType

object InventoryEvents {
	@JvmField
	val OPEN = EventDispatcher<Open>()

	@JvmField
	val UPDATE = EventDispatcher<Update>()

	@JvmField
	val CLOSE = EventDispatcher<Close>()

	@JvmField
	val SLOT_CLICK = EventDispatcher<SlotClick>()

	@JvmField
	val SLOT_UPDATE = EventDispatcher<SlotUpdate>()

	data class Open(val inventory: InventoryData)
	data class Update(val inventory: InventoryData)
	data class Close(val sameName: Boolean)
	data class SlotClick(val itemStack: ItemStack, val button: Int, val slot: Int, val actionType: SlotActionType)
	data class SlotUpdate(val itemStack: ItemStack, val slot: Int)
}
