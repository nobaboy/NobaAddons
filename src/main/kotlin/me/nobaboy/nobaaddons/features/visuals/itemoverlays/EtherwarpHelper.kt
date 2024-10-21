package me.nobaboy.nobaaddons.features.visuals.itemoverlays

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.isSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyblockItem
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
		if(!heldItem.isSkyBlockItem) return
		val item = heldItem.skyblockItem()

		if(!item.id.lowercaseEquals(etherwarpItems) || !item.ethermerge) return

		val baseDistance = 57
		val tunedTransmission = item.tunedTransmission
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
		if(!config.showFailText) return

		failText?.let {
			val window = MCUtils.window
			val x = window.scaledWidth / 2
			val y = window.scaledHeight / 2 + 10
			RenderUtils.drawCenteredText(context, it.toText().formatted(Formatting.RED), x, y)
		}
	}

	private fun handleTarget(context: WorldRenderContext, client: MinecraftClient, target: BlockHitResult, maxDistance: Int) {
		if(isTooFar(client, target, maxDistance) && !config.allowOnAir) {
			failText = null
			return
		}

		failText = getFailText(client, target, maxDistance)
		var color = if(failText == null) config.highlightColor else Color.GRAY
		RenderUtils.drawOutlinedBoundingBox(context, target.toNobaVec(), color, throughBlocks = true)
	}

	private fun getFailText(client: MinecraftClient, target: BlockHitResult, maxDistance: Int): String? {
		val blockPos = target.blockPos
		val blockState = client.world?.getBlockState(blockPos) ?: return "Invalid block!"

		if(isTooFar(client, target, maxDistance) || blockState.isAir) return "Invalid block"

		val stateAbove = client.world?.getBlockState(blockPos.up()) ?: return "Invalid block!"
		val stateTwoAbove = client.world?.getBlockState(blockPos.up(2)) ?: return "Invalid block!"

		return when {
			!blockState.isSolid -> "Not solid!"
			stateAbove.isSolid && !stateAbove.isAir || !stateTwoAbove.isAir -> "No air above!"
			else -> null
		}
	}

	fun isTooFar(client: MinecraftClient, target: BlockHitResult, maxDistance: Int): Boolean =
		target.squaredDistanceTo(client.player) > maxDistance * maxDistance

	private fun isEnabled() = SkyBlockAPI.inSkyblock && config.enabled && MCUtils.options.sneakKey.isPressed
}