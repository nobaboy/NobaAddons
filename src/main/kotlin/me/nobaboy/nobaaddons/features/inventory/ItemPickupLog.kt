package me.nobaboy.nobaaddons.features.inventory

import me.nobaboy.nobaaddons.api.InventoryAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.TextUtils.literal
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientWorldEvents
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.math.abs

object ItemPickupLog {
	private var worldLoadDebounce: Timestamp = Timestamp.distantPast()
	private val config get() = NobaConfigManager.config.inventory.pickupLog

	fun init() {
		UIManager.add(PickupLogHudElement)
		ClientWorldEvents.AFTER_CLIENT_WORLD_CHANGE.register { _, _ ->
			worldLoadDebounce = Timestamp.now()
		}
	}

	private object PickupLogHudElement : TextHudElement(UISettings.pickupLog) {
		override val name: Text = tr("nobaaddons.ui.pickupLog", "Item Pickup Log")
		override val size: Pair<Int, Int> = 120 to 225
		override val enabled: Boolean get() = config.enabled
		override val allowScaling: Boolean = false

		override fun renderText(context: DrawContext) {
			renderLines(context, InventoryAPI.itemLog.filter { (_, diff) -> diff.change != 0 }.map { (name, diff) ->
				buildText {
					if(diff.change < 0) {
						literal("- ${abs(diff.change)}x ") { red() }
					} else {
						literal("+ ${diff.change}x ") { green() }
					}
					append(name)
				}
			})
		}
	}
}