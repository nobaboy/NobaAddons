package me.nobaboy.nobaaddons.events

import me.nobaboy.nobaaddons.data.InventoryData
import net.fabricmc.fabric.api.event.EventFactory

object InventoryEvents {
	val OPEN = EventFactory.createArrayBacked(InventoryOpenEvent::class.java) { listeners ->
		InventoryOpenEvent { inventory ->
			listeners.forEach { it.onInventoryOpen(inventory) }
		}
	}

	val READY = EventFactory.createArrayBacked(InventoryReadyEvent::class.java) { listeners ->
		InventoryReadyEvent { inventory ->
			listeners.forEach { it.onInventoryReady(inventory) }
		}
	}

	val UPDATE = EventFactory.createArrayBacked(InventoryUpdateEvent::class.java) { listeners ->
		InventoryUpdateEvent { inventory ->
			listeners.forEach { it.onInventoryUpdate(inventory) }
		}
	}

	val CLOSE = EventFactory.createArrayBacked(InventoryCloseEvent::class.java) { listeners ->
		InventoryCloseEvent { sameName ->
			listeners.forEach { it.onInventoryClose(sameName) }
		}
	}

	fun interface InventoryOpenEvent {
		fun onInventoryOpen(inventory: InventoryData)
	}

	fun interface InventoryReadyEvent {
		fun onInventoryReady(inventory: InventoryData)
	}

	fun interface InventoryUpdateEvent {
		fun onInventoryUpdate(inventory: InventoryData)
	}

	fun interface InventoryCloseEvent {
		fun onInventoryClose(sameName: Boolean)
	}
}