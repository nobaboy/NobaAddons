package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.data.InventoryData
import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType

object InventoryEvents {
	/**
	 * Event invoked when an inventory is opened and fully ready, excluding the player's inventory
	 */
	@JvmField val OPEN = EventDispatcher<Open>()

	/**
	 * Event invoked when the current inventory is closed, including the player's inventory.
	 */
	@JvmField val CLOSE = EventDispatcher<Close>()

	/**
	 * Event invoked when the current inventory is updated.
	 */
	@JvmField val UPDATE = EventDispatcher<Update>()

	/**
	 * Event invoked when a slot is updated, including the player's inventory.
	 */
	@JvmField val SLOT_UPDATE = EventDispatcher<SlotUpdate>()

	/**
	 * Event invoked when a slot is clicked in an inventory.
	 */
	@JvmField val SLOT_CLICK = EventDispatcher<SlotClick>()

	data class Open(val inventory: InventoryData) : Event()
	data class Close(val sameName: Boolean) : Event()
	data class Update(val inventory: InventoryData) : Event()
	data class SlotUpdate(val itemStack: ItemStack, val slot: Int) : Event()
	data class SlotClick(val itemStack: ItemStack, val button: Int, val slot: Int, val actionType: SlotActionType) : Event()
}
