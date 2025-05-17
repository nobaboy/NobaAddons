package me.nobaboy.nobaaddons.features.inventory

import me.nobaboy.nobaaddons.api.InventoryAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.utils.mc.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.mc.TextUtils.green
import me.nobaboy.nobaaddons.utils.mc.TextUtils.literal
import me.nobaboy.nobaaddons.utils.mc.TextUtils.red
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.math.abs

object ItemPickupLog {
	private val config get() = NobaConfig.inventory.itemPickupLog

	init {
		UIManager.add(PickupLogHudElement)
	}

	private object PickupLogHudElement : TextHudElement(UISettings.itemPickupLog) {
		override val name: Text = tr("nobaaddons.ui.itemPickupLog", "Item Pickup Log")
		override val size: Pair<Int, Int> = 125 to 175
		override val enabled: Boolean get() = config.enabled
		override val color: Int = 0xFFFFFF
		override val maxScale: Float = 1f

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