package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.data.InventoryData
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.events.PacketEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket

object InventoryAPI {
	private var currentInventory: InventoryData? = null
	private var currentWindow: Window? = null

	fun init() {
		PacketEvents.SEND.register(this::onPacketSend)
		PacketEvents.RECEIVE.register(this::onPacketReceive)
	}

	private fun onPacketSend(event: PacketEvents.Send) {
		val packet = event.packet
		when(packet) {
			is ClickSlotC2SPacket -> onClickSlot(packet)
			is CloseHandledScreenC2SPacket -> close()
		}
	}

	private fun onPacketReceive(event: PacketEvents.Receive) {
		val packet = event.packet
		when(packet) {
			is OpenScreenS2CPacket -> onScreenOpen(packet)
			is InventoryS2CPacket -> onInventory(packet)
			is ScreenHandlerSlotUpdateS2CPacket -> onSlotUpdate(packet)
			is CloseScreenS2CPacket -> close()
		}
	}

	private fun onClickSlot(packet: ClickSlotC2SPacket) {
		if(packet.syncId != currentWindow?.id) return

		InventoryEvents.SLOT_CLICK.invoke(InventoryEvents.SlotClick(packet.stack, packet.button, packet.slot, packet.actionType))
	}

	private fun onScreenOpen(packet: OpenScreenS2CPacket) {
		currentWindow = Window(packet.syncId, packet.name.string)
	}

	private fun onInventory(packet: InventoryS2CPacket) {
		if(packet.syncId != currentWindow?.id) return

		val slotCount = packet.contents.size - 36
		val items = packet.contents
			.take(slotCount)
			.withIndex()
			.filter { (_, item) -> !item.isEmpty }
			.associate { it.index to it.value }
			.toMutableMap()

		currentInventory = InventoryData(currentWindow!!.id, currentWindow!!.title, slotCount, items).also {
			ready(it)
		}
	}

	private fun onSlotUpdate(packet: ScreenHandlerSlotUpdateS2CPacket) {
		if(packet.syncId != currentWindow?.id) {
			InventoryEvents.SLOT_UPDATE.invoke(InventoryEvents.SlotUpdate(packet.stack, packet.slot))
			return
		}

		val inventory = currentInventory ?: return
		val slot = packet.slot

		if(slot >= inventory.slotCount) return
		packet.stack?.let { inventory.items[slot] = it }

		InventoryEvents.UPDATE.invoke(InventoryEvents.Update(inventory))
	}

	private fun ready(inventory: InventoryData) {
		InventoryEvents.OPEN.invoke(InventoryEvents.Open(inventory))
		InventoryEvents.UPDATE.invoke(InventoryEvents.Update(inventory))
	}

	private fun close(sameName: Boolean = false) {
		if(MCUtils.client.currentScreen is ChatScreen) return
		InventoryEvents.CLOSE.invoke(InventoryEvents.Close(sameName))
		currentInventory = null
	}

	data class Window(val id: Int, val title: String)
}