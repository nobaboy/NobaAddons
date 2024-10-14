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

	fun drawText(
		context: DrawContext,
		text: Text,
		x: Int,
		y: Int,
		scale: Float = 1.0f,
		color: Int = 0xFFFFFF,
		shadow: Boolean = true,
		applyScaling: Boolean = true
	) {
		if(applyScaling) startScale(context, scale)
		context.drawText(NobaAddons.mc.textRenderer, text, (x / scale).toInt(), (y / scale).toInt(), color, shadow)
		if(applyScaling) endScale(context)
	}
	fun drawText(
		context: DrawContext,
		text: String,
		x: Int,
		y: Int,
		scale: Float = 1.0f,
		color: Int = 0xFFFFFF,
		shadow: Boolean = true,
		applyScaling: Boolean = true
	) {
		drawText(context, text.toText(), x, y, scale, color, shadow, applyScaling)
	}

	fun drawOutlinedText(
		context: DrawContext,
		text: Text,
		x: Int,
		y: Int,
		scale: Float = 1.0f,
		color: Int = 0xFFFFFF,
		outlineColor: Int = 0x000000,
		applyScaling: Boolean = true
	) {
		if(applyScaling) startScale(context, scale)
		NobaAddons.mc.textRenderer.drawWithOutline(
			text.asOrderedText(),
			(x / scale).toFloat(),
			(y / scale).toFloat(),
			color,
			outlineColor,
			context.matrices.peek().positionMatrix,
			context.vertexConsumers,
			15728880
		)
		if(applyScaling) endScale(context)
	}
	fun drawOutlinedText(
		context: DrawContext,
		text: String,
		x: Int,
		y: Int,
		scale: Float = 1.0f,
		color: Int = 0xFFFFFF,
		outlineColor: Int = 0x000000,
		applyScaling: Boolean = true
	) {
		drawOutlinedText(context, text.toText(), x, y, scale, color, outlineColor, applyScaling)
	}

	fun drawCenteredText(
		context: DrawContext,
		text: Text,
		x: Int,
		y: Int,
		scale: Float = 1.0f,
		color: Int = 0xFFFFFF,
		shadow: Boolean = true,
		applyScaling: Boolean = true
	) {
		val width = (text.getWidth() * scale).toInt()
		drawText(context, text, x - width / 2, y, scale, color, shadow, applyScaling)
	}
	fun drawCenteredText(
		context: DrawContext,
		text: String,
		x: Int,
		y: Int,
		scale: Float = 1.0f,
		color: Int = 0xFFFFFF,
		shadow: Boolean = true,
		applyScaling: Boolean = true
	) {
		drawCenteredText(context, text.toText(), x, y, scale, color, shadow, applyScaling)
	}

	fun Text.draw(
		context: DrawContext,
		x: Int,
		y: Int,
		scale: Float = 1.0f,
		color: Int = 0xFFFFFF,
		shadow: Boolean = true,
		applyScaling: Boolean = true
	) {
		drawText(context, this, x, y, scale, color, shadow, applyScaling)
	}
	fun Text.drawCentered(
		context: DrawContext,
		x: Int,
		y: Int,
		scale: Float = 1.0f,
		color: Int = 0xFFFFFF,
		shadow: Boolean = true,
		applyScaling: Boolean = true
	) {
		drawCenteredText(context, this, x, y, scale, color, shadow, applyScaling)
	}

	fun String.draw(
		context: DrawContext,
		x: Int,
		y: Int,
		scale: Float = 1.0f,
		color: Int = 0xFFFFFF,
		shadow: Boolean = true,
		applyScaling: Boolean = true
	) {
		drawText(context, this.toText(), x, y, scale, color, shadow, applyScaling)
	}
	fun String.drawCentered(
		context: DrawContext,
		x: Int,
		y: Int,
		scale: Float = 1.0f,
		color: Int = 0xFFFFFF,
		shadow: Boolean = true,
		applyScaling: Boolean = true
	) {
		drawCenteredText(context, this.toText(), x, y, scale, color, shadow, applyScaling)
	}

	fun isPointInArea(
		pointX: Double,
		pointY: Double,
		leftX: Double,
		leftY: Double,
		rightX: Double,
		rightY: Double
	): Boolean = pointX in leftX..rightX && pointY in leftY..rightY

	fun Text.getWidth(): Int = NobaAddons.mc.textRenderer.getWidth(this)
	fun String.getWidth(): Int = NobaAddons.mc.textRenderer.getWidth(this)
}

