package me.nobaboy.nobaaddons.events

import net.fabricmc.fabric.api.event.EventFactory

object InventoryEvents {
//	val OPEN = EventFactory.createArrayBacked(InventoryOpenEvent::class.java) { listeners ->
//		InventoryOpenEvent { inventory ->
//			listeners.forEach { it.onInventoryOpen(inventory) }
//		}
//	}

//	val READY = EventFactory.createArrayBacked(InventoryReadyEvent::class.java) { listeners ->
//
//	}

//	val UPDATE = EventFactory.createArrayBacked(InventoryUpdateEvent::class.java) { listeners ->
//
//	}

	val CLOSE = EventFactory.createArrayBacked(InventoryCloseEvent::class.java) { listeners ->
		InventoryCloseEvent { name ->
			listeners.forEach { it.onInventoryClose(name) }
		}
	}

//	fun interface InventoryOpenEvent {
//		fun onInventoryOpen(inventory: InventoryData)
//	}

//	fun interface InventoryReadyEvent {
//		fun onInventoryReady(inventory: InventoryData)
//	}

//	fun interface InventoryUpdateEvent {
//		fun onInventoryUpdate(inventory: InventoryData)
//	}

	fun interface InventoryCloseEvent {
		fun onInventoryClose(name: String)
	}
}