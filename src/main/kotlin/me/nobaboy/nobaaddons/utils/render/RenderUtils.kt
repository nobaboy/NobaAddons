package me.nobaboy.nobaaddons.utils.render

//? if >=1.21.2 {
import me.nobaboy.nobaaddons.mixins.accessors.DrawContextAccessor
import net.minecraft.client.render.VertexRendering
//?} else {
/*import net.minecraft.client.render.WorldRenderer
import net.minecraft.client.render.GameRenderer
*///?}

//? if >=1.21.5 {
/*import kotlin.math.max
*///?} else {
import com.mojang.blaze3d.systems.RenderSystem
import org.lwjgl.opengl.GL11
//?}

import me.nobaboy.nobaaddons.mixins.accessors.BeaconBlockEntityRendererAccessor
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.StringUtils
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.expand
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.LightmapTextureManager
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.text.Text
import net.minecraft.util.math.Box
import net.minecraft.util.math.MathHelper
import org.joml.Matrix4f
import kotlin.math.roundToInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

object RenderUtils {
	/**
	 * Runs [withScale] with the provided [scale]; this is shorthand for wrapping `withScale` in a try-finally
	 * using [startScale] and [endScale].
	 */
	inline fun scaled(context: DrawContext, scale: Float, withScale: () -> Unit) {
		startScale(context, scale)
		try {
			withScale()
		} finally {
			endScale(context)
		}
	}

	fun startScale(context: DrawContext, scale: Float) {
		context.matrices.push()
		context.matrices.scale(scale, scale, 1f)
	}
	fun endScale(context: DrawContext) = context.matrices.pop()

	fun Text.getWidth(): Int = MCUtils.textRenderer.getWidth(this)
	fun String.getWidth(): Int = MCUtils.textRenderer.getWidth(this)

	fun drawText(
		context: DrawContext,
		text: Text,
		x: Int,
		y: Int,
		scale: Float = 1f,
		color: NobaColor = NobaColor.WHITE,
		shadow: Boolean = true,
		applyScaling: Boolean = true,
	) {
		if(applyScaling && scale != 1f) startScale(context, scale)
		context.drawText(MCUtils.textRenderer, text, (x / scale).toInt(), (y / scale).toInt(), color.rgb, shadow)
		if(applyScaling && scale != 1f) endScale(context)
	}

	fun drawText(
		context: DrawContext,
		text: String,
		x: Int,
		y: Int,
		scale: Float = 1f,
		color: NobaColor = NobaColor.WHITE,
		shadow: Boolean = true,
		applyScaling: Boolean = true,
	) {
		drawText(context, text.toText(), x, y, scale, color, shadow, applyScaling)
	}

	fun drawOutlinedText(
		context: DrawContext,
		text: Text,
		x: Int,
		y: Int,
		scale: Float = 1f,
		color: NobaColor = NobaColor.WHITE,
		outlineColor: NobaColor = NobaColor.BLACK,
		applyScaling: Boolean = true,
	) {
		if(applyScaling) startScale(context, scale)
		val vertexConsumerProvider = context.let {
			(it /*? if >=1.21.2 {*/ as DrawContextAccessor/*?}*/).vertexConsumers
		}
		MCUtils.textRenderer.drawWithOutline(
			text.asOrderedText(),
			x / scale,
			y / scale,
			color.rgb,
			outlineColor.rgb,
			context.matrices.peek().positionMatrix,
			vertexConsumerProvider,
			15728880
		)
		if(applyScaling) endScale(context)
	}

	fun drawOutlinedText(
		context: DrawContext,
		text: String,
		x: Int,
		y: Int,
		scale: Float = 1f,
		color: NobaColor = NobaColor.WHITE,
		outlineColor: NobaColor = NobaColor.BLACK,
		applyScaling: Boolean = true,
	) {
		drawOutlinedText(context, text.toText(), x, y, scale, color, outlineColor, applyScaling)
	}

	fun drawCenteredText(
		context: DrawContext,
		text: Text,
		x: Int,
		y: Int,
		scale: Float = 1f,
		color: NobaColor = NobaColor.WHITE,
		shadow: Boolean = true,
		applyScaling: Boolean = true,
	) {
		val width = (text.getWidth() * scale).toInt()
		drawText(context, text, x - width / 2, y, scale, color, shadow, applyScaling)
	}

	fun drawCenteredText(
		context: DrawContext,
		text: String,
		x: Int,
		y: Int,
		scale: Float = 1f,
		color: NobaColor = NobaColor.WHITE,
		shadow: Boolean = true,
		applyScaling: Boolean = true,
	) {
		val width = (text.getWidth() * scale).toInt()
		drawText(context, text.toText(), x - width / 2, y, scale, color, shadow, applyScaling)
	}

	fun isPointInArea(
		pointX: Double,
		pointY: Double,
		leftX: Double,
		leftY: Double,
		rightX: Double,
		rightY: Double,
	): Boolean = pointX in leftX..rightX && pointY in leftY..rightY

	fun drawTitle(
		text: Text,
		color: NobaColor = NobaColor.WHITE,
		scale: Float = 4f,
		offset: Int = 0,
		duration: Duration = 3.seconds,
		id: String = StringUtils.randomAlphanumeric(),
		subtext: Text? = null,
	) {
		TitleManager.draw(text, color, scale, offset, duration, id, subtext)
	}

	fun drawTitle(
		text: String,
		color: NobaColor = NobaColor.WHITE,
		scale: Float = 4f,
		offset: Int = 0,
		duration: Duration = 3.seconds,
		id: String = StringUtils.randomAlphanumeric(),
		subtext: Text? = null,
	) {
		TitleManager.draw(text.toText(), color, scale, offset, duration, id, subtext)
	}

	fun Box.expandBlock(n: Int = 1) = expand(NobaVec.expandVector * n)
	fun Box.shrinkBlock(n: Int = 1) = expand(NobaVec.expandVector * -n)

	fun renderWaypoint(
		context: WorldRenderContext,
		location: NobaVec,
		color: NobaColor,
		extraSize: Double = 0.0,
		extraSizeTopY: Double = extraSize,
		extraSizeBottomY: Double = extraSize,
		beaconThreshold: Double = 5.0,
		throughBlocks: Boolean = false,
	) {
		renderBeaconBeam(context, location.raise(), color, beaconThreshold)
		renderFilledBox(context, location, color, extraSize, extraSizeTopY, extraSizeBottomY, throughBlocks)
	}

	fun renderOutlinedFilledBox(
		context: WorldRenderContext,
		location: NobaVec,
		color: NobaColor,
		lineWidth: Float = 3f,
		extraSize: Double = 0.0,
		extraSizeTopY: Double = extraSize,
		extraSizeBottomY: Double = extraSize,
		throughBlocks: Boolean = false,
	) {
		renderOutline(context, location, color, lineWidth, extraSize, extraSizeTopY, extraSizeBottomY, throughBlocks)
		renderFilledBox(context, location, color, extraSize, extraSizeTopY, extraSizeBottomY, throughBlocks)
	}

	fun renderBeaconBeam(
		context: WorldRenderContext,
		location: NobaVec,
		color: NobaColor,
		hideThreshold: Double = 5.0,
	) {
		if(!FrustumUtils.isVisible(location, toWorldHeight = true)) return

		val matrices = context.matrixStack() ?: return
		val cameraPos = context.camera().pos.toNobaVec()

		val dist = location.distance(cameraPos, center = true)
		if(dist <= hideThreshold) return

		//? if >=1.21.5 {
		/*val horizontalDist = location.distanceIgnoreY(cameraPos, center = true)
		val scale = max(1f, horizontalDist.toFloat() / 96f)
		*///?}

		val x = location.x - cameraPos.x
		val y = location.y - cameraPos.y
		val z = location.z - cameraPos.z

		matrices.push()
		matrices.translate(x, y, z)

		BeaconBlockEntityRendererAccessor.invokeRenderBeam(
			matrices,
			context.consumers(),
			//? if >=1.21.5 {
			/*context.tickCounter().getTickProgress(true),
			scale,
			*///?} else {
			context.tickCounter().getTickDelta(true),
			//?}
			context.world().time,
			0,
			2048,
			color.rgb
		)

		matrices.pop()
	}

	fun renderFilledBox(
		context: WorldRenderContext,
		location: NobaVec,
		color: NobaColor,
		extraSize: Double = 0.0,
		extraSizeTopY: Double = extraSize,
		extraSizeBottomY: Double = extraSize,
		throughBlocks: Boolean = false,
	) {
		if(!FrustumUtils.isVisible(location)) return

		val matrices = context.matrixStack() ?: return
		val consumers = context.consumers() as? VertexConsumerProvider.Immediate ?: return

		val layer = if(throughBlocks) NobaRenderLayers.FILLED_THROUGH_BLOCKS else NobaRenderLayers.FILLED
		val buffer = consumers.getBuffer(layer)

		val cameraPos = context.camera().pos.toNobaVec()

		val red = color.red / 255f
		val green = color.green / 255f
		val blue = color.blue / 255f

		val distSq = location.distanceSq(cameraPos, center = true)
		val alpha = (0.1f + 0.005f * distSq.toFloat()).coerceIn(0.3f, 0.7f)

		val box = Box(
			location.x - extraSize, location.y - extraSizeBottomY, location.z - extraSize,
			location.x + 1 + extraSize, location.y + 1 + extraSizeTopY, location.z + 1 + extraSize
		).expandBlock()

		matrices.push()
		matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

		//? if >=1.21.2 {
		VertexRendering.drawFilledBox(
		//?} else {
		/*WorldRenderer.renderFilledBox(
		*///?}
			matrices, buffer,
			box.minX, box.minY, box.minZ,
			box.maxX, box.maxY, box.maxZ,
			red, green, blue, alpha
		)

		matrices.pop()
	}

	fun renderOutline(
		context: WorldRenderContext,
		location: NobaVec,
		color: NobaColor,
		lineWidth: Float = 3f,
		extraSize: Double = 0.0,
		extraSizeTopY: Double = extraSize,
		extraSizeBottomY: Double = extraSize,
		throughBlocks: Boolean = false,
	) {
		if(!FrustumUtils.isVisible(location)) return

		val matrices = context.matrixStack() ?: return
		val consumers = context.consumers() as? VertexConsumerProvider.Immediate ?: return

		val layer = if(throughBlocks) NobaRenderLayers.getLinesThroughWalls(lineWidth) else NobaRenderLayers.getLines(lineWidth)
		val buffer = consumers.getBuffer(layer)

		val cameraPos = context.camera().pos.toNobaVec()

		val red = color.red / 255f
		val green = color.green / 255f
		val blue = color.blue / 255f

		val distSq = location.distanceSq(cameraPos, center = true)
		val alpha = (0.1f + 0.005f * distSq.toFloat()).coerceIn(0.7f, 1f)

		val box = Box(
			location.x - extraSize, location.y - extraSizeBottomY, location.z - extraSize,
			location.x + 1 + extraSize, location.y + 1 + extraSizeTopY, location.z + 1 + extraSize
		).expandBlock()

		matrices.push()
		matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z)

		//? if >=1.21.2 {
		VertexRendering.drawBox(
		//?} else {
		/*WorldRenderer.drawBox(
		*///?}
			matrices, buffer,
			box.minX, box.minY, box.minZ,
			box.maxX, box.maxY, box.maxZ,
			red, green, blue, alpha
		)

		matrices.pop()
	}

	fun renderText(
		context: WorldRenderContext,
		location: NobaVec,
		text: Text,
		color: NobaColor = NobaColor.WHITE,
		shadow: Boolean = /*? if >=1.21.2 {*/true/*?} else {*//*false*//*?}*/,
		yOffset: Float = 0f,
		scaleMultiplier: Float = 1f,
		hideThreshold: Double = 0.0,
		throughBlocks: Boolean = false,
	) {
		if(!FrustumUtils.isVisible(location)) return

		val positionMatrix = Matrix4f()
		val consumers = context.consumers() as? VertexConsumerProvider.Immediate ?: return

		val camera = context.camera()
		val cameraPos = camera.pos.toNobaVec()

		val dist = location.distance(cameraPos)
		if(dist <= hideThreshold) return

		var scale = dist.toFloat() / 256f
		scale = (scale * scaleMultiplier).coerceAtLeast(0.025f)

		val x = location.x - cameraPos.x
		val y = location.y - cameraPos.y
		val z = location.z - cameraPos.z

		positionMatrix
			.translate(x.toFloat(), y.toFloat(), z.toFloat())
			.rotate(camera.rotation)
			.scale(scale, -scale, scale)

		val textRenderer = MCUtils.textRenderer
		val xOffset = -textRenderer.getWidth(text) / 2f

		//? if <1.21.5 {
		RenderSystem.enableDepthTest()
		RenderSystem.depthFunc(if(throughBlocks) GL11.GL_ALWAYS else GL11.GL_LEQUAL)
		//?}

		val layer = if(throughBlocks) TextRenderer.TextLayerType.SEE_THROUGH else TextRenderer.TextLayerType.NORMAL

		textRenderer.draw(
			text,
			xOffset,
			yOffset,
			color.rgb,
			shadow,
			positionMatrix,
			consumers,
			layer,
			0x000000FF,
			LightmapTextureManager.MAX_LIGHT_COORDINATE
		)
		consumers.draw()

		//? if <1.21.5 {
		RenderSystem.disableDepthTest()
		RenderSystem.depthFunc(GL11.GL_LEQUAL)
		//?}
	}

	fun renderText(
		context: WorldRenderContext,
		location: NobaVec,
		text: String,
		color: NobaColor = NobaColor.WHITE,
		shadow: Boolean = /*? if >=1.21.2 {*/true/*?} else {*//*false*//*?}*/,
		yOffset: Float = 0f,
		scaleMultiplier: Float = 1f,
		hideThreshold: Double = 0.0,
		throughBlocks: Boolean = false,
	) {
		renderText(context, location, text.toText(), color, shadow, yOffset, scaleMultiplier, hideThreshold, throughBlocks)
	}

	/**
	 * Returns a lerped alpha for [displayTicks], gradually becoming more transparent the closer it is to 0 from [threshold]
	 */
	fun lerpAlpha(partialTick: Float, displayTicks: Int, threshold: Int): Int {
		val lerped = MathHelper.lerp(partialTick, displayTicks.toFloat(), displayTicks - 1f)
		return ((lerped / threshold.toDouble()) * 255.0).roundToInt().coerceIn(0, 255)
	}
}