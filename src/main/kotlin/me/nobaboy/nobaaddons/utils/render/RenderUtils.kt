package me.nobaboy.nobaaddons.utils.render

import com.mojang.blaze3d.systems.RenderSystem
import me.nobaboy.nobaaddons.mixins.invokers.BeaconBlockEntityRendererInvoker
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.expand
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.BufferRenderer
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.render.VertexFormat.DrawMode
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.util.BufferAllocator
import net.minecraft.text.Text
import net.minecraft.util.math.Box
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11
import java.awt.Color

object RenderUtils {
	val ALLOCATOR = BufferAllocator(1536)

	fun startScale(context: DrawContext, scale: Float) {
		context.matrices.push()
		context.matrices.scale(scale, scale, 1.0f)
	}
	fun endScale(context: DrawContext) = context.matrices.pop()

	fun Text.getWidth(): Int = MCUtils.textRenderer.getWidth(this)
	fun String.getWidth(): Int = MCUtils.textRenderer.getWidth(this)

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
		context.drawText(MCUtils.textRenderer, text, (x / scale).toInt(), (y / scale).toInt(), color, shadow)
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
		MCUtils.textRenderer.drawWithOutline(
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

	fun Box.expandBlock(n: Int = 1) = expand(NobaVec.expandVector * n)
	fun Box.shrinkBlock(n: Int = 1) = expand(NobaVec.expandVector * -n)

	fun drawWaypoint(
		context: WorldRenderContext,
		vec: NobaVec,
		color: Color,
		extraSize: Double = 0.0,
		extraSizeTopY: Double = extraSize,
		extraSizeBottomY: Double = extraSize,
		beaconThreshold: Double = 5.0,
		throughBlocks: Boolean = false
	) {
		drawBeaconBeam(context, vec, color, beaconThreshold)
		drawFilledBox(context, vec, color, extraSize, extraSizeTopY, extraSizeBottomY, throughBlocks)
	}

	fun drawOutlinedWaypoint(
		context: WorldRenderContext,
		vec: NobaVec,
		color: Color,
		lineWidth: Float = 3.0f,
		extraSize: Double = 0.0,
		extraSizeTopY: Double = extraSize,
		extraSizeBottomY: Double = extraSize,
		beaconThreshold: Double = 5.0,
		throughBlocks: Boolean = false
	) {
		drawBeaconBeam(context, vec, color, beaconThreshold)
		drawOutline(context, vec, color, lineWidth, extraSize, extraSizeTopY, extraSizeBottomY, throughBlocks)
		drawFilledBox(context, vec, color, extraSize, extraSizeTopY, extraSizeBottomY, throughBlocks)
	}

	fun drawOutlinedFilledBox(
		context: WorldRenderContext,
		vec: NobaVec,
		color: Color,
		lineWidth: Float = 3.0f,
		extraSize: Double = 0.0,
		extraSizeTopY: Double = extraSize,
		extraSizeBottomY: Double = extraSize,
		throughBlocks: Boolean = false
	) {
		drawOutline(context, vec, color, lineWidth, extraSize, extraSizeTopY, extraSizeBottomY, throughBlocks)
		drawFilledBox(context, vec, color, extraSize, extraSizeTopY, extraSizeBottomY, throughBlocks)
	}

	fun drawBeaconBeam(context: WorldRenderContext, vec: NobaVec, color: Color, hideThreshold: Double = 5.0) {
		drawBeaconBeam(context, vec, color.rgb, hideThreshold)
	}
	fun drawBeaconBeam(context: WorldRenderContext, vec: NobaVec, color: Int, hideThreshold: Double = 5.0) {
		if(!FrustumUtils.isVisible(vec, toWorldHeight = true)) return

		val matrices = context.matrixStack() ?: return
		val cameraPos = context.camera().pos.toNobaVec()

		val distSq = vec.distanceSq(cameraPos)
		if(distSq < hideThreshold * hideThreshold) return

		matrices.push()
		matrices.translate(vec.x - cameraPos.x, vec.y - cameraPos.y, vec.z - cameraPos.z)

		BeaconBlockEntityRendererInvoker.renderBeam(
			matrices,
			context.consumers(),
			context.tickCounter().getTickDelta(true),
			context.world().time,
			0,
			319,
			color
		)

		matrices.pop()
	}

	fun drawOutline(
		context: WorldRenderContext,
		vec: NobaVec,
		color: Int,
		lineWidth: Float = 3.0f,
		extraSize: Double = 0.0,
		extraSizeTopY: Double = extraSize,
		extraSizeBottomY: Double = extraSize,
		throughBlocks: Boolean = false
	) {
		drawOutline(context, vec, Color(color), lineWidth, extraSize, extraSizeTopY, extraSizeBottomY, throughBlocks)
	}
	fun drawOutline(
		context: WorldRenderContext,
		vec: NobaVec,
		color: Color,
		lineWidth: Float = 3.0f,
		extraSize: Double = 0.0,
		extraSizeTopY: Double = extraSize,
		extraSizeBottomY: Double = extraSize,
		throughBlocks: Boolean = false
	) {
		if(!FrustumUtils.isVisible(vec)) return

		val matrices = context.matrixStack() ?: return
		val cameraPos = context.camera().pos.toNobaVec()
		val tessellator = RenderSystem.renderThreadTesselator()

		RenderSystem.setShader(GameRenderer::getRenderTypeLinesProgram)
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
		RenderSystem.enableBlend()
		RenderSystem.lineWidth(lineWidth)
		RenderSystem.disableCull()
		RenderSystem.enableDepthTest()
		RenderSystem.depthFunc(if(throughBlocks) GL11.GL_ALWAYS else GL11.GL_LEQUAL)

		matrices.push()
		matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

		val red = color.red / 255.0f
		val green = color.green / 255.0f
		val blue = color.blue / 255.0f

		val distSq = vec.distanceSq(cameraPos)
		val alpha = (0.1f + 0.005f * distSq.toFloat()).coerceIn(0.7f, 1.0f)

		val buffer = tessellator.begin(DrawMode.LINES, VertexFormats.LINES)
		WorldRenderer.drawBox(
			matrices, buffer,
			Box(
				vec.x - extraSize, vec.y - extraSizeBottomY, vec.z - extraSize,
				vec.x + 1 + extraSize, vec.y + 1 + extraSizeTopY, vec.z + 1 + extraSize
			).expandBlock(),
			red, green, blue,
			alpha
		)
		BufferRenderer.drawWithGlobalProgram(buffer.end())

		matrices.pop()
		RenderSystem.lineWidth(1f)
		RenderSystem.enableCull()
		RenderSystem.disableBlend()
		RenderSystem.disableDepthTest()
		RenderSystem.depthFunc(GL11.GL_LEQUAL)
	}

	fun drawFilledBox(
		context: WorldRenderContext,
		vec: NobaVec,
		color: Int,
		extraSize: Double = 0.0,
		extraSizeTopY: Double = extraSize,
		extraSizeBottomY: Double = extraSize,
		throughBlocks: Boolean = false
	) {
		drawFilledBox(context, vec, Color(color), extraSize, extraSizeTopY, extraSizeBottomY, throughBlocks)
	}
	fun drawFilledBox(
		context: WorldRenderContext,
		vec: NobaVec,
		color: Color,
		extraSize: Double = 0.0,
		extraSizeTopY: Double = extraSize,
		extraSizeBottomY: Double = extraSize,
		throughBlocks: Boolean = false
	) {
		if(!FrustumUtils.isVisible(vec)) return

		val matrices = context.matrixStack() ?: return
		val cameraPos = context.camera().pos.toNobaVec()

		matrices.push()
		matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

		val consumers = context.consumers() ?: return
		val buffer = consumers.getBuffer(if(throughBlocks) NobaRenderLayers.FILLED_THROUGH_BLOCKS else NobaRenderLayers.FILLED)

		val red = color.red / 255.0f
		val green = color.green / 255.0f
		val blue = color.blue / 255.0f

		val distSq = vec.distanceSq(cameraPos)
		val alpha = (0.1f + 0.005f * distSq.toFloat()).coerceIn(0.2f, 0.7f)

		val box = Box(
			vec.x - extraSize, vec.y - extraSizeBottomY, vec.z - extraSize,
			vec.x + 1 + extraSize, vec.y + 1 + extraSizeTopY, vec.z + 1 + extraSize
		).expandBlock()

		WorldRenderer.renderFilledBox(
			matrices, buffer,
			box.minX, box.minY, box.minZ,
			box.maxX, box.maxY, box.maxZ,
			red, green, blue,
			alpha
		)

		matrices.pop()
	}
	
	fun drawText(
		context: WorldRenderContext,
		vec: NobaVec,
		text: String,
		hideThreshold: Double = 4.5,
		throughBlocks: Boolean = false
	) {
		val positionMatrix = Matrix4f()
		val camera = context.camera()
		val cameraPos = camera.pos.toNobaVec()
		val textRenderer = MCUtils.textRenderer

		val distSq = vec.distanceSq(cameraPos)
		if(distSq < hideThreshold * hideThreshold) return
		val scale = (distSq.toFloat() * 0.00005f).coerceIn(0.04f, 1.75f)

		val x = vec.x - cameraPos.x
		val y = vec.y - cameraPos.y
		val z = vec.z - cameraPos.z

		positionMatrix
			.translate(x.toFloat(), y.toFloat(), z.toFloat())
			.rotate(camera.rotation)
			.scale(scale, -scale, scale)

		val xOffset = -textRenderer.getWidth(text) / 2.0f

		val consumers = VertexConsumerProvider.immediate(ALLOCATOR)
		RenderSystem.depthFunc(if(throughBlocks) GL11.GL_ALWAYS else GL11.GL_LEQUAL)

		textRenderer.draw(text, xOffset, 0.0f, 0xFFFFFF, false, positionMatrix, consumers, TextRenderer.TextLayerType.SEE_THROUGH, 0,
			LightmapTextureManager.MAX_LIGHT_COORDINATE)
		consumers.draw()

		RenderSystem.depthFunc(GL11.GL_LEQUAL)
	}
}