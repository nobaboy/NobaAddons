package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

object RenderUtils {
	fun startScale(context: DrawContext, scale: Float) {
		context.matrices.push()
		context.matrices.scale(scale, scale, 1.0f)
	}

	fun endScale(context: DrawContext) = context.matrices.pop()

	fun drawText(context: DrawContext, text: Text, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true) =
		context.drawText(NobaAddons.mc.textRenderer, text, x, y, color, shadow)
	fun drawText(context: DrawContext, text: Text, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true, scale: Float) {
		startScale(context, scale)
		drawText(context, text, (x / scale).toInt(), (y / scale).toInt(), color, shadow)
		endScale(context)
	}

	fun drawCenteredText(context: DrawContext, text: Text, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true) {
		val width = text.getWidth()
		drawText(context, text, x - width / 2, y, color, shadow)
	}
	fun drawCenteredText(context: DrawContext, text: Text, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true, scale: Float) {
		val width = (text.getWidth() * scale).toInt()
		drawText(context, text, x - width / 2, y, color, shadow, scale)
	}
	fun drawCenteredText(context: DrawContext, text: String, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true) {
		drawCenteredText(context, text.toText(), x, y, color, shadow)
	}
	fun drawCenteredText(context: DrawContext, text: String, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true, scale: Float) {
		drawCenteredText(context, text.toText(), x, y, color, shadow, scale)
	}

	fun Text.draw(context: DrawContext, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true) =
		drawText(context, this, x, y, color, shadow)
	fun Text.draw(context: DrawContext, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true, scale: Float) =
		drawText(context, this, x, y, color, shadow, scale)
	fun Text.drawCentered(context: DrawContext, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true) =
		drawCenteredText(context, this, x, y, color, shadow)
	fun Text.drawCentered(context: DrawContext, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true, scale: Float) =
		drawCenteredText(context, this, x, y, color, shadow, scale)

	fun String.draw(context: DrawContext, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true) =
		this.toText().draw(context, x, y, color, shadow)
	fun String.draw(context: DrawContext, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true, scale: Float) =
		this.toText().draw(context, x, y, color, shadow, scale)
	fun String.drawCentered(context: DrawContext, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true) =
		this.toText().drawCentered(context, x, y, color, shadow)
	fun String.drawCentered(context: DrawContext, x: Int, y: Int, color: Int = 0xFFFFFF, shadow: Boolean = true, scale: Float) =
		this.toText().drawCentered(context, x, y, color, shadow, scale)

	fun isPointInArea(pointX: Double, pointY: Double, leftX: Double, leftY: Double, rightX: Double, rightY: Double): Boolean {
		return pointX >= leftX && pointX <= rightX && pointY >= leftY && pointY <= rightY
	}

	fun Text.getWidth(): Int = NobaAddons.mc.textRenderer.getWidth(this)
	fun String.getWidth(): Int = NobaAddons.mc.textRenderer.getWidth(this)
}