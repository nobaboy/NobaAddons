package me.nobaboy.nobaaddons.features.inventory

import me.nobaboy.nobaaddons.api.InventoryAPI
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.config.option.booleanController
import me.nobaboy.nobaaddons.config.option.enumCycler
import me.nobaboy.nobaaddons.config.option.intSlider
import me.nobaboy.nobaaddons.features.Feature
import me.nobaboy.nobaaddons.features.FeatureCategory
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.ui.TextShadow
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.TextUtils.literal
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.math.abs

object ItemPickupLog : Feature("itemPickupLog", tr("nobaaddons.feature.itemPickupLog", "Item Pickup Log"), FeatureCategory.INVENTORY) {
	private var enabled by config(false) {
		name = CommonText.Config.ENABLED
		booleanController()
	}

	var timeoutSeconds by config(5) {
		name = tr("nobaaddons.config.inventory.itemPickupLog.timeout", "Expire After")
		intSlider(min = 2, max = 10)
		requires { option(::enabled) }
	}

	@Suppress("unused") // TODO TextElement should be reworked to be a @Serializable data class to avoid needing to do this
	private var textStyle by config(TextShadow.SHADOW) {
		name = tr("nobaaddons.config.inventory.itemPickupLog.style", "Text Style")
		enumCycler()
		requires { option(::enabled) }
		onSave { UISettings.itemPickupLog.textShadow = it }
	}

	override fun init() {
		UIManager.add(PickupLogHudElement)
	}

	private object PickupLogHudElement : TextHudElement(UISettings.itemPickupLog) {
		override val name: Text = tr("nobaaddons.ui.itemPickupLog", "Item Pickup Log")
		override val size: Pair<Int, Int> = 125 to 175
		override val enabled: Boolean get() = !killSwitch && ItemPickupLog.enabled
		override val color: Int = 0xFFFFFF
		// FIXME this causes the visual scaling in the hud editor to break?
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