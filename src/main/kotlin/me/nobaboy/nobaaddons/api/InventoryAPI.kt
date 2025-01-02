package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.data.InventoryData
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.events.PacketEvents
import me.nobaboy.nobaaddons.events.QuarterSecondPassedEvent
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.ModAPIUtils.listen
import me.nobaboy.nobaaddons.utils.Timestamp
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.inventory.Inventory
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.text.Text
import java.util.concurrent.ConcurrentHashMap

object InventoryAPI {
	private const val SKYBLOCK_MENU_SLOT = 8

	private var currentInventory: InventoryData? = null
	private var currentWindow: Window? = null

	private var inventoryLogDebounce = Timestamp.distantPast()
	private var previousItemCounts: Map<Text, Int>? = null
	val itemLog = ConcurrentHashMap<Text, ItemDiff>()

	private val suppressItemLogUpdate: Boolean
		get() = inventoryLogDebounce.elapsedSeconds() < 2

	fun init() {
		PacketEvents.SEND.register(this::onPacketSend)
		PacketEvents.RECEIVE.register(this::onPacketReceive)
		QuarterSecondPassedEvent.EVENT.register(this::onQuarterSecond)
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register { _, _ -> debounceItemLog() }
		HypixelModAPI.getInstance().listen<ClientboundLocationPacket> { debounceItemLog() }
	}

	private fun debounceItemLog() {
		previousItemCounts = null
		inventoryLogDebounce = Timestamp.now()
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
		inventoryLogDebounce = Timestamp.now()
	}

	private fun onQuarterSecond(event: QuarterSecondPassedEvent) {
		val player = event.client.player
		if(/*!SkyBlockAPI.inSkyBlock || */player == null) {
			previousItemCounts = null
			itemLog.clear()
			return
		}

		if(event.client.currentScreen == null) {
			// note that we're intentionally using the full inventory (which also includes armor items)
			// as it's easier to also account for hypixel using the offhand with it. it does mean you'll get
			// item logs when (un)equipping sets in the wardrobe, but oh well, live with it.
			val current = player.inventory.toCounts()
			previousItemCounts?.takeIf { suppressItemLogUpdate }?.let { updateItemLog(it, current) }
			previousItemCounts = current
		}

		itemLog.entries.removeIf { (_, diff) ->
			diff.timestamp.elapsedSeconds() > NobaConfigManager.config.inventory.pickupLog.timeoutSeconds
		}
	}

	private fun updateItemLog(previous: Map<Text, Int>, current: Map<Text, Int>) {
		// this MUST be annotated, as the kotlin compiler can't figure out the typing automatically from getOrPut,
		// despite the fact that intellij doesn't throw any warnings.
		val diffs = buildMap<Text, ItemDiff> {
			for(item in (previous.keys + current.keys)) {
				val previousCount = previous[item] ?: 0
				val currentCount = current[item] ?: 0
				if(previousCount == currentCount) continue
				getOrPut(item) { ItemDiff(item) }.change += currentCount - previousCount
			}
		}

		for((name, diff) in diffs) {
			if(diff.change == 0) continue
			val logDiff = itemLog.getOrPut(name) { ItemDiff(name) }
			logDiff.change += diff.change
			logDiff.timestamp = Timestamp.now()
		}
	}

	private fun Inventory.toCounts(): Map<Text, Int> = buildMap {
		for(slot in 0 until size()) {
			if(slot == SKYBLOCK_MENU_SLOT) continue

			val item = this@toCounts.getStack(slot)
			if(item.isEmpty) continue
			val name = item.name

			merge(name, item.count) { a, b -> a + b }
		}
	}

	data class Window(val id: Int, val title: String)

	data class ItemDiff(
		val name: Text,
		var change: Int = 0,
		var timestamp: Timestamp = Timestamp.now(),
	)
}
