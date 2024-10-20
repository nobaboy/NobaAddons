package me.nobaboy.nobaaddons.features.visuals

import me.nobaboy.nobaaddons.api.SkyblockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.ItemUtils.getCustomData
import me.nobaboy.nobaaddons.utils.ItemUtils.getSkyblockID
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.util.Formatting
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import java.awt.Color

object EtherwarpHelper {
	private val config get() = NobaConfigManager.get().uiAndVisuals.etherwarpHelper

	private val etherwarpItems = listOf("ASPECT_OF_THE_END", "ASPECT_OF_THE_VOID")
	private var failText: String? = null

	fun init() {
		WorldRenderEvents.AFTER_TRANSLUCENT.register { context -> renderOverlay(context) }
		HudRenderCallback.EVENT.register { context, _ -> renderFailText(context) }
	}

	private fun renderOverlay(context: WorldRenderContext) {
		if(!isEnabled()) {
			failText = null
			return
		}

		val client = MCUtils.client
		val heldItem = client.player?.mainHandStack ?: return
		val customData = heldItem.getCustomData()
		val itemID = customData.getSkyblockID()

		if(!itemID.lowercaseEquals(etherwarpItems) || customData.getInt("ethermerge") != 1) return

		val baseDistance = 57
		val tunedTransmission = customData.getInt("tuned_transmission").or(0)
		val maxDistance = baseDistance + tunedTransmission

		val target = client.crosshairTarget

		if(target is BlockHitResult && target.type == HitResult.Type.BLOCK) {
			handleTarget(context, client, target, maxDistance)
		} else if(client.interactionManager != null) {
			val raycast = client.player?.raycast(maxDistance.toDouble(), context.tickCounter().getTickDelta(true), false) as? BlockHitResult
			raycast?.let { handleTarget(context, client, it, maxDistance) }
		}
	}

	private fun renderFailText(context: DrawContext) {
		failText?.let {
			val window = MCUtils.window
			val x = window.scaledWidth / 2
			val y = window.scaledHeight / 2 + 10
			RenderUtils.drawCenteredText(context, it.toText().formatted(Formatting.RED), x, y)
		}
	}

	private fun handleTarget(context: WorldRenderContext, client: MinecraftClient, target: BlockHitResult, maxDistance: Int) {
		if(isTooFar(client, target, maxDistance) && !config.allowOnAir) return

		if(config.showFailText) failText = getFailText(client, target, maxDistance)
		val color = if(failText != null) Color.GRAY else config.overlayColor
		RenderUtils.drawOutlinedBoundingBox(context, target.toNobaVec(), color)
	}

	// TODO: Replace isSolid since it's deprecated
	private fun getFailText(client: MinecraftClient, target: BlockHitResult, maxDistance: Int): String? {
		val blockPos = target.blockPos
		val blockState = client.world?.getBlockState(blockPos) ?: return "Invalid block!"

		if(isTooFar(client, target, maxDistance) || blockState.isAir) return "Invalid block"

		val stateAbove = client.world?.getBlockState(blockPos.up()) ?: return "Invalid block!"
		val stateTwoAbove = client.world?.getBlockState(blockPos.up(2)) ?: return "Invalid block!"

		return when {
			!blockState.isSolid -> "Not solid!"
			!stateAbove.isAir || !stateTwoAbove.isAir -> "No air above!"
			else -> null
		}
	}

	fun isTooFar(client: MinecraftClient, target: BlockHitResult, maxDistance: Int): Boolean =
		target.squaredDistanceTo(client.player) > maxDistance * maxDistance

	private fun isEnabled() = SkyblockAPI.inSkyblock && config.enabled && MCUtils.options.sneakKey.isPressed
}