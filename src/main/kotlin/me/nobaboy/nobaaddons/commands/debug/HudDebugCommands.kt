package me.nobaboy.nobaaddons.commands.debug

import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Group
import me.nobaboy.nobaaddons.ui.ElementAlignment
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.ui.data.GenericTextElement
import me.nobaboy.nobaaddons.utils.mc.TextUtils.toText
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.math.ColorHelper.channelFromFloat
import kotlin.random.Random

@Suppress("unused")
@Group("hud")
object HudDebugCommands {
	private fun randomLightColor(): Int {
		val rand = Random
		val r = channelFromFloat(rand.nextFloat() / 2f + 0.5f)
		val g = channelFromFloat(rand.nextFloat() / 2f + 0.5f)
		val b = channelFromFloat(rand.nextFloat() / 2f + 0.5f)
		return ((r shl 16) + (g shl 8) + b)
	}

	private class DebugHudElement(
		private val text: String,
		private val align: ElementAlignment?,
	) : TextHudElement(GenericTextElement(color = randomLightColor())) {
		override fun renderText(context: DrawContext) {
			renderLine(context, text.toText(), alignment = align)
		}

		override val name: Text = Text.literal("Debug HUD")
		override val size: Pair<Int, Int> = 100 to 25
	}

	@Command
	fun addElement(text: String = "Zoop!", alignment: ElementAlignment? = null) {
		UIManager.add(DebugHudElement(text, alignment))
	}

	@Command
	fun clear() {
		UIManager.removeAll { it is DebugHudElement }
	}

	@Command
	fun showBounds(showBounds: Boolean) {
		UIManager.renderElementBounds = showBounds
	}
}