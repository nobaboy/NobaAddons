package me.nobaboy.nobaaddons.commands.debug

import dev.celestialfault.commander.annotations.Command
import dev.celestialfault.commander.annotations.Group
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.ui.data.GenericTextElement
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Colors

@Suppress("unused")
@Group("hud")
object HudDebugCommands {
	private val colors = listOf(
		Colors.WHITE, Colors.GREEN, Colors.LIGHT_RED, Colors.GRAY,
		Colors.ALTERNATE_WHITE, Colors.BLUE, Colors.LIGHT_YELLOW,
		Colors.YELLOW
	)

	private class DebugHudElement : TextHudElement(GenericTextElement(color = colors.random())) {
		override fun renderText(context: DrawContext) {
			renderLine(context, "Zoop!".toText())
		}

		override val name: Text = Text.literal("Debug HUD")
		override val size: Pair<Int, Int> = 100 to 25
	}

	@Command
	fun addElement() {
		UIManager.add(DebugHudElement())
	}

	@Command
	fun clear() {
		UIManager.removeAll { it is DebugHudElement }
	}
}