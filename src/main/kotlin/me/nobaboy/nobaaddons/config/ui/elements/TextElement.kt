package me.nobaboy.nobaaddons.config.ui.elements

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils.getWidth
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

abstract class TextElement(element: Element) : HudElement(element) {
	abstract fun text(): String
	abstract fun textMode(): TextMode
	abstract fun outlineColor(): Int

	override fun render(context: DrawContext, forced: Boolean, hovered: Boolean) {
		if(!shouldRender(forced)) return

		val backgroundColor = if(hovered) HOVERED else NOT_HOVERED
		if(forced) drawBackground(context, backgroundColor)
		RenderUtils.startScale(context, scale.toFloat())
		renderLine(context, x, y, scale.toFloat(), color)
		RenderUtils.endScale(context)
	}

	override fun getBounds(): ElementBounds {
		val width = text().getWidth().takeIf { it > 0 } ?: identifier.getWidth()
		return ElementBounds(x, y, (width * scale).toInt(), (MCUtils.textRenderer.fontHeight * scale).toInt(), scale)
	}

	private fun renderLine(context: DrawContext, x: Int, y: Int, scale: Float, color: Int) {
		when(textMode()) {
			TextMode.PURE -> RenderUtils.drawText(context, text(), x, y, scale, color, shadow = false, applyScaling = false)
			TextMode.SHADOW -> RenderUtils.drawText(context, text(), x, y, scale, color, shadow = true, applyScaling = false)
			TextMode.OUTLINE -> RenderUtils.drawOutlinedText(context, text(), x, y, scale, color, outlineColor(), applyScaling = false)
		}
	}

	enum class TextMode : NameableEnum {
		PURE,
		SHADOW,
		OUTLINE;

		override fun getDisplayName(): Text = name.title().toText()
	}
}