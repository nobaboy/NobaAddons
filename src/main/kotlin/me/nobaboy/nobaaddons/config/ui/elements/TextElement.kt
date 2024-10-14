package me.nobaboy.nobaaddons.config.ui.elements

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.RenderUtils
import me.nobaboy.nobaaddons.utils.RenderUtils.getWidth
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

abstract class TextElement(
	identifier: String,
	x: Int,
	y: Int,
	scale: Double = 1.0,
	color: Int = 0xFFFFFF
) : HudElement(identifier, x, y, scale, color) {
	abstract fun string(): String
	abstract fun lineMode(): LineMode
	abstract fun outlineColor(): Int

	override fun render(context: DrawContext, withBackground: Boolean, hovered: Boolean) {
		if(!shouldRender(withBackground)) return

		val backgroundColor = if(hovered) HOVERED else NOT_HOVERED
		if(withBackground) drawBackground(context, backgroundColor)
		RenderUtils.startScale(context, scale.toFloat())
		renderLine(context, x, y, scale.toFloat(), color)
		RenderUtils.endScale(context)
	}

	override fun getBounds(): ElementBounds {
		val width = string().getWidth().takeIf { it > 0 } ?: identifier.getWidth()
		return ElementBounds(x, y, (width * scale).toInt(), (NobaAddons.mc.textRenderer.fontHeight * scale).toInt(), scale)
	}

	private fun renderLine(context: DrawContext, x: Int, y: Int, scale: Float, color: Int) {
		when(lineMode()) {
			LineMode.PURE -> RenderUtils.drawText(context, string(), x, y, scale, color, shadow = false, applyScaling = false)
			LineMode.SHADOW -> RenderUtils.drawText(context, string(), x, y, scale, color, shadow = true, applyScaling = false)
			LineMode.OUTLINE -> RenderUtils.drawOutlinedText(context, string(), x, y, scale, color, outlineColor(), applyScaling = false)
		}
	}

	enum class LineMode : NameableEnum {
		PURE,
		SHADOW,
		OUTLINE;

		override fun getDisplayName(): Text = name.toText()
	}
}