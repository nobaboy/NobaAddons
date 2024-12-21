package me.nobaboy.nobaaddons.features.visuals.itemoverlays

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.LocationUtils.rayCast
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
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
import net.minecraft.world.World

object EtherwarpHelper {
	private val config get() = NobaConfigManager.config.uiAndVisuals.etherwarpHelper
	private val enabled: Boolean get() = SkyBlockAPI.inSkyBlock && config.enabled && MCUtils.options.sneakKey.isPressed

	private const val BASE_DISTANCE = 57
	private val etherwarpItems = setOf("ASPECT_OF_THE_END", "ASPECT_OF_THE_VOID")
	private var targetBlock: ValidationType? = null

	fun init() {
		HudRenderCallback.EVENT.register { context, _ -> renderFailText(context) }
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderOverlay)
	}

	private fun renderFailText(context: DrawContext) {
		targetBlock.takeIf { config.showFailText }?.let {
			val (x, y) = MCUtils.window.let { it.scaledWidth / 2 to it.scaledHeight / 2 + 10 }
			RenderUtils.drawCenteredText(context, it.text.toText().formatted(Formatting.RED), x, y)
		}
	}

	private fun renderOverlay(context: WorldRenderContext) {
		if(!enabled) {
			targetBlock = null
			return
		}

		val client = MCUtils.client
		val item = client.player?.mainHandStack?.getSkyBlockItem() ?: run {
			targetBlock = null
			return
		}

		if(!etherwarpItems.contains(item.id) || !item.ethermerge) {
			targetBlock = null
			return
		}

		val maxDistance = BASE_DISTANCE + item.tunedTransmission
		val target = client.crosshairTarget

		if(target is BlockHitResult && target.type == HitResult.Type.BLOCK) {
			handleTarget(context, client, target)
		} else if(client.interactionManager != null) {
			val raycast = client.player?.rayCast(maxDistance.toDouble(), context.tickCounter().getTickDelta(true), true) as? BlockHitResult
			raycast?.let { handleTarget(context, client, it) }
		}
	}

	private fun handleTarget(context: WorldRenderContext, client: MinecraftClient, target: BlockHitResult) {
		val world = client.world ?: return

		targetBlock = validateTargetBlock(world, target)
		if(targetBlock == ValidationType.TOO_FAR && !config.allowOverlayOnAir) return

		var color = targetBlock?.let { NobaColor.GRAY.toColor() } ?: config.highlightColor
		RenderUtils.renderOutlinedFilledBox(context, target.blockPos.toNobaVec(), color, throughBlocks = true)
	}

	private fun validateTargetBlock(world: World, target: BlockHitResult): ValidationType? {
		val blockPos = target.blockPos

		val blockState = world.getBlockState(blockPos)
		if(blockState.isAir) return ValidationType.TOO_FAR
		if(!blockState.isSolid) return ValidationType.NOT_SOLID

		val stateAbove = world.getBlockState(blockPos.up())
		val stateTwoAbove = world.getBlockState(blockPos.up(2))

		return if(stateAbove.isSolid && !stateAbove.isAir || !stateTwoAbove.isAir) ValidationType.NO_AIR_ABOVE else null
	}

	private enum class ValidationType(val text: String) {
		TOO_FAR("Too far!"),
		NOT_SOLID("Not solid!"),
		NO_AIR_ABOVE("No air above!")
	}
}