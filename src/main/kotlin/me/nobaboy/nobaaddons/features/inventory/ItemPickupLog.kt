package me.nobaboy.nobaaddons.features.inventory

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.UISettings
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import java.util.concurrent.ConcurrentHashMap

object ItemPickupLog {
	private var worldLoadDebounce: Timestamp = Timestamp.distantPast()
	private val config get() = NobaConfigManager.config.inventory.pickupLog
	private val itemLog = ConcurrentHashMap<Text, Pair<Int, Timestamp>>()

	fun init() {
		UIManager.add(PickupLogHudElement)
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register { _, _ ->
			worldLoadDebounce = Timestamp.now()
		}
	}

	private fun onInventoryUpdate(event: InventoryEvents.SlotUpdate) {
		// TODO
	}

	private object PickupLogHudElement : TextHudElement(UISettings.pickupLog) {
		override val name: Text = tr("nobaaddons.ui.pickupLog", "Item Pickup Log")
		override val size: Pair<Int, Int> = 120 to 225
		override val enabled: Boolean get() = config.enabled
		override val allowScaling: Boolean = false

		override fun renderText(context: DrawContext) {
			renderLines(context, listOf("- 1x Trans Rights").map { it.toText() })
		}
	}
}