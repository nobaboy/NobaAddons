package me.nobaboy.nobaaddons.utils.render

import me.nobaboy.nobaaddons.events.skyblock.SkyBlockIslandChangeEvent
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.Timestamp
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.time.Duration

object TitleManager {
	private var titleText: Text = Text.empty()
	private var displayTime = Timestamp.distantPast()
	private var titleScale: Float = 4.0f

	init {
		SkyBlockIslandChangeEvent.EVENT.register { displayTime = Timestamp.distantPast() }
		HudRenderCallback.EVENT.register { context, _ -> render(context) }
	}

	private fun render(context: DrawContext) {
		if(displayTime.isPast()) return

		val (width, height) = MCUtils.window.let { it.scaledWidth to it.scaledHeight }
		val y = height / 2 - 9 * (titleScale.toInt() + 1)

		RenderUtils.drawCenteredText(context, titleText, width / 2, y, scale = titleScale)
	}

	fun draw(text: Text, duration: Duration, scale: Float = 4.0f) {
		titleText = text
		displayTime = Timestamp.now() + duration
		titleScale = scale
	}
}