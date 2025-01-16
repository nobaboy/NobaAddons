package me.nobaboy.nobaaddons.events.impl.client

import me.nobaboy.nobaaddons.data.InventoryData
import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.SlotActionType

object InventoryEvents {
	/**
	 * Event invoked after an inventory is opened and fully ready.
	 */
	@JvmField val OPEN = EventDispatcher<Open>()

	/**
	 * Event called when the opened inventory is updated, which could be a singular slot or more.
	 */
	@JvmField val UPDATE = EventDispatcher<Update>()

	/**
	 * Event invoked after the current opened inventory is closed.
	 */
	@JvmField val CLOSE = EventDispatcher<Close>()

	/**
	 * Event invoked when a slot in the current opened inventory is clicked.
	 */
	@JvmField val SLOT_CLICK = EventDispatcher<SlotClick>()

	/**
	 * Event invoked when any individual slot is updated, including in the player's inventory.
	 */
	@JvmField val SLOT_UPDATE = EventDispatcher<SlotUpdate>()

	data class Open(val inventory: InventoryData) : Event()
	data class Update(val inventory: InventoryData) : Event()
	data class Close(val sameName: Boolean) : Event()
	data class SlotClick(val itemStack: ItemStack, val button: Int, val slot: Int, val actionType: SlotActionType) : Event()
	data class SlotUpdate(val itemStack: ItemStack, val slot: Int) : Event()
}
