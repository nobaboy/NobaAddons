package me.nobaboy.nobaaddons.utils.render

import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.render.RenderUtils.drawCenteredText
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import kotlin.time.Duration

object TitleManager {
	private val titles = mutableMapOf<String, Title>()

	init {
		SkyBlockEvents.ISLAND_CHANGE.register { titles.clear() }
		HudRenderCallback.EVENT.register { context, _ -> render(context) }
	}

	private fun render(context: DrawContext) {
		if(titles.isEmpty()) return

		val (width, height) = MCUtils.window.let { it.scaledWidth to it.scaledHeight }

		titles.values.removeIf { it.expired }
		titles.values.forEach {
			val textHeight = (9 * it.scale).toInt()
			val y = height / 2 - textHeight + it.offset
			context.drawCenteredText(it.text, width / 2, y, it.scale, it.color)
			if(it.subtext != null) context.drawCenteredText(it.subtext, width / 2, y + textHeight + 5, it.scale - 1.5f, it.color)
		}
	}

	fun draw(
		text: Text,
		subtext: Text?,
		color: NobaColor,
		scale: Float,
		offset: Int,
		duration: Duration,
		id: String,
	) {
		val title = Title(text, subtext, color, scale, offset, duration, Timestamp.now())
		titles[id] = title
	}

	data class Title(
		val text: Text,
		val subtext: Text?,
		val color: NobaColor,
		val scale: Float,
		val offset: Int,
		val duration: Duration,
		val timestamp: Timestamp,
	) {
		val expired: Boolean get() = timestamp.elapsedSince() >= duration
	}
}