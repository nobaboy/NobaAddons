package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

object RenderUtils {
    fun startScale(context: DrawContext, scale: Float) {
        context.matrices.push()
        context.matrices.scale(scale, scale, 1.0f)
    }
    fun endScale(context: DrawContext) = context.matrices.pop()

    fun drawText(context: DrawContext, text: Text, x: Int, y: Int, color: Int, shadow: Boolean) = context.drawText(NobaAddons.mc.textRenderer, text, x, y, color, shadow)
    fun drawText(context: DrawContext, text: Text, x: Int, y: Int, color: Int, shadow: Boolean, scale: Float) {
        startScale(context, scale)
        drawText(context, text, (x / scale).toInt(), (y / scale).toInt(), color, shadow)
        endScale(context)
    }

    fun drawCenteredText(context: DrawContext, text: Text, x: Int, y: Int, color: Int, shadow: Boolean) {
        val width = getTextWidth(text)
        drawText(context, text, x - width / 2, y, color, shadow)
    }
    fun drawCenteredText(context: DrawContext, text: Text, x: Int, y: Int, color: Int, shadow: Boolean, scale: Float) {
        val width = (getTextWidth(text) * scale).toInt()
        drawText(context, text, x - width / 2, y, color, shadow, scale)
    }

    fun getTextWidth(text: Text) = NobaAddons.mc.textRenderer.getWidth(text)
    fun getTextWidth(text: String) = NobaAddons.mc.textRenderer.getWidth(text)

    fun Text.draw(context: DrawContext, x: Int, y: Int, color: Int, shadow: Boolean) = drawText(context, this, x, y, color, shadow)
    fun Text.draw(context: DrawContext, x: Int, y: Int, color: Int, shadow: Boolean, scale: Float) = drawText(context, this, x, y, color, shadow, scale)
    fun Text.drawCentered(context: DrawContext, x: Int, y: Int, color: Int, shadow: Boolean) = drawCenteredText(context, this, x, y, color, shadow)
    fun Text.drawCentered(context: DrawContext, x: Int, y: Int, color: Int, shadow: Boolean, scale: Float) = drawCenteredText(context, this, x, y, color, shadow, scale)
    fun String.draw(context: DrawContext, x: Int, y: Int, color: Int, shadow: Boolean) = Text.of(this).draw(context, x, y, color, shadow)
    fun String.draw(context: DrawContext, x: Int, y: Int, color: Int, shadow: Boolean, scale: Float) = Text.of(this).draw(context, x, y, color, shadow, scale)
    fun String.drawCentered(context: DrawContext, x: Int, y: Int, color: Int, shadow: Boolean) = Text.of(this).drawCentered(context, x, y, color, shadow)
    fun String.drawCentered(context: DrawContext, x: Int, y: Int, color: Int, shadow: Boolean, scale: Float) = Text.of(this).drawCentered(context, x, y, color, shadow, scale)

    fun isPointInArea(pointX: Double, pointY: Double, leftX: Double, leftY: Double, rightX: Double, rightY: Double): Boolean {
        return pointX >= leftX && pointX <= rightX && pointY >= leftY && pointY <= rightY
    }
}