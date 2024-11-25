package me.nobaboy.nobaaddons.utils.render

import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.Timestamp
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.time.Duration

object TitleManager {
	private val titles = mutableListOf<Title>()

	init {
		SkyBlockEvents.ISLAND_CHANGE.register { titles.clear() }
		HudRenderCallback.EVENT.register { context, _ -> render(context) }
	}

	private fun render(context: DrawContext) {
		if(titles.isEmpty()) return

		val (width, height) = MCUtils.window.let { it.scaledWidth to it.scaledHeight }

		titles.removeIf { it.expired }
		titles.forEach {
			val y = (height / it.height - 9 * (it.scale + 1)).toInt()
			RenderUtils.drawCenteredText(context, it.text, width / 2, y, it.scale, it.color)
		}
	}

	fun draw(text: Text, color: Int, duration: Duration, scale: Float, height: Double) {
		val title = Title(text, color, Timestamp.now(), duration, scale, height)
		titles.add(title)
	}

	data class Title(val text: Text, val color: Int, val timestamp: Timestamp, val duration: Duration, val scale: Float, val height: Double) {
		val expired: Boolean get() = timestamp.elapsedSince() >= duration
	}
}