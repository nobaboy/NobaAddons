package me.nobaboy.nobaaddons.features.visuals

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.utils.LocationUtils.rayCast
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.render.RenderUtils.renderFullBox
import me.nobaboy.nobaaddons.utils.toNobaVec
import me.nobaboy.nobaaddons.utils.tr
import me.owdding.ktmodules.Module
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.hit.HitResult
import net.minecraft.world.World

@Module
object EtherwarpOverlay {
	private val config get() = NobaConfig.uiAndVisuals.etherwarpOverlay
	private val enabled: Boolean get() = config.enabled && SkyBlockAPI.inSkyBlock && MCUtils.options.sneakKey.isPressed

	private const val BASE_DISTANCE = 57
	private val etherwarpItems = setOf("ASPECT_OF_THE_END", "ASPECT_OF_THE_VOID")
	private var targetBlock: ValidationType? = null

	init {
		HudRenderCallback.EVENT.register { context, _ -> renderFailText(context) }
		WorldRenderEvents.AFTER_TRANSLUCENT.register(this::renderOverlay)
	}

	private fun renderFailText(context: DrawContext) {
		targetBlock.takeIf { config.showFailText }?.let {
			val (x, y) = MCUtils.window.let { it.scaledWidth / 2 to it.scaledHeight / 2 + 10 }
			RenderUtils.drawCenteredText(context, it.displayName, x, y, color = NobaColor.RED)
		}
	}

	private fun renderOverlay(context: WorldRenderContext) {
		if(!enabled) {
			targetBlock = null
			return
		}

		val client = MCUtils.client
		val item = client.player?.mainHandStack?.asSkyBlockItem ?: run {
			targetBlock = null
			return
		}

		if(!etherwarpItems.contains(item.id) || item.ethermerge != true) {
			targetBlock = null
			return
		}

		val maxDistance = BASE_DISTANCE + (item.tunedTransmission ?: 0)
		val target = client.crosshairTarget

		if(target is BlockHitResult && target.type == HitResult.Type.BLOCK) {
			handleTarget(context, client, target)
		} else if(client.interactionManager != null) {
			val tickDelta = context.tickCounter()./*? if >=1.21.5 {*//*getTickProgress*//*?} else {*/getTickDelta/*?}*/(true)
			val raycast = client.player?.rayCast(maxDistance.toDouble(), tickDelta, true) as? BlockHitResult
			raycast?.let { handleTarget(context, client, it) }
		}
	}

	private fun handleTarget(context: WorldRenderContext, client: MinecraftClient, target: BlockHitResult) {
		val world = client.world ?: return

		targetBlock = validateTargetBlock(world, target)
		if(targetBlock == ValidationType.TOO_FAR && !config.allowOverlayOnAir) return

		val color = targetBlock?.let { config.failHighlightColor } ?: config.highlightColor
		context.renderFullBox(target.blockPos.toNobaVec(), color, throughBlocks = true)
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

	private enum class ValidationType : NameableEnum {
		TOO_FAR,
		NOT_SOLID,
		NO_AIR_ABOVE,
		;

		override fun getDisplayName(): Text = when(this) {
			TOO_FAR -> tr("nobaaddons.config.uiAndVisuals.etherwarpOverlay.validationType.tooFar", "Too far!")
			NOT_SOLID -> tr("nobaaddons.config.uiAndVisuals.etherwarpOverlay.validationType.notSolid", "Not solid!")
			NO_AIR_ABOVE -> tr("nobaaddons.config.uiAndVisuals.etherwarpOverlay.validationType.noAirAbove", "No air above!")
		}
	}
}