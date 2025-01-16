package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.data.InventoryData
import me.nobaboy.nobaaddons.events.impl.client.InventoryEvents
import me.nobaboy.nobaaddons.events.impl.client.PacketEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.client.WorldEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.Timestamp
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket
import net.minecraft.network.packet.s2c.play.CloseScreenS2CPacket
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket
import net.minecraft.network.packet.s2c.play.OpenScreenS2CPacket
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.concurrent.ConcurrentHashMap

object InventoryAPI {
	private val MERCHANT_COUNT = Regex("x\\d+")
	private const val SKYBLOCK_MENU_SLOT = 8

	private var currentInventory: InventoryData? = null
	private var currentWindow: Window? = null

	private var inventorySuppressTime = Timestamp.distantPast()
	private var previousItemCounts: Map<Text, Int>? = null
	val itemLog = ConcurrentHashMap<Text, ItemDiff>()

	private fun shouldSuppressItemLogUpdate(): Boolean = inventorySuppressTime.elapsedSeconds() < 2

	fun init() {
		TickEvents.every(5, this::onQuarterSecond)
		PacketEvents.SEND.register(this::onPacketSend)
		PacketEvents.POST_RECEIVE.register(this::onPacketReceive)
		WorldEvents.POST_LOAD.register { debounceItemLog() }
	}

	private fun onQuarterSecond(event: TickEvents.Tick) {
		val player = event.client.player
		if(!SkyBlockAPI.inSkyBlock || player == null) {
			previousItemCounts = null
			itemLog.clear()
			return
		}

		if(event.client.currentScreen == null) {
			val current = player.inventory.itemNamesToCount()
			previousItemCounts?.takeIf { !shouldSuppressItemLogUpdate() }?.let { updateItemLog(it, current) }
			previousItemCounts = current
		}

		itemLog.entries.removeIf { (_, diff) ->
			diff.timestamp.elapsedSeconds() > NobaConfig.INSTANCE.inventory.itemPickupLog.timeoutSeconds
		}
	}

	private fun onPacketSend(event: PacketEvents.Send) {
		when(val packet = event.packet) {
			is ClickSlotC2SPacket -> onClickSlot(packet)
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

		currentInventory = InventoryData(currentWindow!!.id, currentWindow!!.title, slotCount, items).also(::ready)
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

	private fun debounceItemLog() {
		previousItemCounts = null
		inventorySuppressTime = Timestamp.now()
	}

	private fun close(sameName: Boolean = false) {
		if(MCUtils.client.currentScreen is ChatScreen) return
		InventoryEvents.CLOSE.invoke(InventoryEvents.Close(sameName))
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

	private fun Text.removeMerchantCount(): Text {
		if(siblings.size <= 1) return this
		val last = siblings.last()
		// celeste — Today at 01:19
		// i have reworded this comment like 10 times now
		// i cannot figure out a way to properly express how much i hate having to do this
		if(last.string.matches(MERCHANT_COUNT) && last.style.color?.rgb == Formatting.DARK_GRAY.colorValue!!) {
			val copy = copy()
			copy.siblings.removeLast()
			val name = copy.siblings.removeLast()
			val content = name.string.removeSuffix(" ")
			copy.append(buildLiteral(content) { style = name.style })
			return copy
		}
		return this
	}

	private fun PlayerInventory.itemNamesToCount(): Map<Text, Int> = buildMap {
		for(slot in 0 until main.size) {
			if(slot == SKYBLOCK_MENU_SLOT) {
				continue
			}

			val item = main[slot]
			if(item.isEmpty) continue
			val name = item.name.removeMerchantCount()

			merge(name, item.count, Int::plus)
		}
		offHand.firstOrNull()?.let { merge(name, it.count, Int::plus) }
	}

	data class Window(val id: Int, val title: String)

	data class ItemDiff(
		val name: Text,
		var change: Int = 0,
		var timestamp: Timestamp = Timestamp.now(),
	)
}
