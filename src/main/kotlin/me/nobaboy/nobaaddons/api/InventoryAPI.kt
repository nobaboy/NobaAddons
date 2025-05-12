package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.data.InventoryData
import me.nobaboy.nobaaddons.events.impl.client.InventoryEvents
import me.nobaboy.nobaaddons.events.impl.client.PacketEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket

object InventoryAPI {
	private var currentScreen: Screen? = null
	private var currentInventory: InventoryData? = null

	init {
		PacketEvents.SEND.register(this::onPacketSend)
		PacketEvents.POST_RECEIVE.register(this::onPacketReceive)
	}

	private fun onPacketSend(event: PacketEvents.Send) {
		when(event.packet) {
			is CloseHandledScreenC2SPacket -> close()
		}
	}

	private fun onPacketReceive(event: PacketEvents.Receive) {
		when(val packet = event.packet) {
			is OpenScreenS2CPacket -> onScreenOpen(packet)
			is InventoryS2CPacket -> onInventory(packet)
			is ScreenHandlerSlotUpdateS2CPacket -> onSlotUpdate(packet)
			is CloseScreenS2CPacket -> close()
		}
	}

	private fun onScreenOpen(packet: OpenScreenS2CPacket) {
		currentScreen = Screen(packet.syncId, packet.name.string)
	}

	private fun onInventory(packet: InventoryS2CPacket) {
		if(packet.syncId != currentScreen?.id) return

		val slotCount = packet.contents.size - 36
		val items = packet.contents
			.take(slotCount)
			.withIndex()
			.filter { (_, item) -> !item.isEmpty }
			.associate { it.index to it.value }
			.toMutableMap()

		currentInventory = InventoryData(currentScreen!!.id, currentScreen!!.title, slotCount, items).also(::ready)
	}

	private fun onSlotUpdate(packet: ScreenHandlerSlotUpdateS2CPacket) {
		if(packet.syncId != currentScreen?.id) return

		val currentInventory = currentInventory ?: return
		val slot = packet.slot

		if(slot >= currentInventory.slotCount) return
		currentInventory.items[slot] = packet.stack

		InventoryEvents.UPDATE.dispatch(InventoryEvents.Update(currentInventory))
	}

	private fun ready(inventory: InventoryData) {
		InventoryEvents.OPEN.dispatch(InventoryEvents.Open(inventory))
		InventoryEvents.UPDATE.dispatch(InventoryEvents.Update(inventory))
	}

	private fun close(sameName: Boolean = false) {
		if(MCUtils.client.currentScreen is ChatScreen) return
		InventoryEvents.CLOSE.dispatch(InventoryEvents.Close(sameName))
	}

	private data class Screen(val id: Int, val title: String)
}