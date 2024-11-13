package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.data.InventoryData
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.events.PacketEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.network.packet.Packet
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket

object InventoryAPI {
	private var currentInventory: InventoryData? = null
	private var currentWindow: Window? = null
	private var acceptItems: Boolean = false

	fun init() {
		PacketEvents.SEND.register(this::handlePacketSend)
		PacketEvents.RECEIVE.register(this::handlePacketReceive)
	}

	private fun handlePacketSend(packet: Packet<*>) {
		when(packet) {
			is ClickSlotC2SPacket -> handleClickSlot(packet)
			is CloseHandledScreenC2SPacket -> close()
		}
	}

	private fun handlePacketReceive(packet: Packet<*>) {
		when(packet) {
			is OpenScreenS2CPacket -> handleOpenScreen(packet)
			is InventoryS2CPacket -> handleInventory(packet)
			is ScreenHandlerSlotUpdateS2CPacket -> handleSlotUpdate(packet)
			is CloseScreenS2CPacket -> close()
		}
	}

	private fun handleClickSlot(packet: ClickSlotC2SPacket) {
		if(packet.syncId != currentWindow?.id) return

		InventoryEvents.SLOT_CLICK.invoker().onInventorySlotClick(packet.stack, packet.button, packet.slot, packet.actionType)
	}

	private fun handleOpenScreen(packet: OpenScreenS2CPacket) {
		currentWindow = Window(packet.syncId, packet.name.string)
	}

	private fun handleInventory(packet: InventoryS2CPacket) {
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

	private fun handleSlotUpdate(packet: ScreenHandlerSlotUpdateS2CPacket) {
		if(packet.syncId != currentWindow?.id) return

		val inventory = currentInventory ?: return
		val slot = packet.slot

		if(slot >= inventory.slotCount) return
		packet.stack?.let { inventory.items[slot] = it }

		InventoryEvents.UPDATE.invoker().onInventoryUpdate(inventory)
	}

	private fun ready(inventory: InventoryData) {
		InventoryEvents.READY.invoker().onInventoryReady(inventory)
		InventoryEvents.UPDATE.invoker().onInventoryUpdate(inventory)
		inventory.ready = true
		acceptItems = false
	}

	private fun close(sameName: Boolean = false) {
		if(MCUtils.client.currentScreen is ChatScreen) return
		InventoryEvents.CLOSE.invoker().onInventoryClose(sameName)
		currentInventory = null
	}

	data class Window(val id: Int, val title: String)
}