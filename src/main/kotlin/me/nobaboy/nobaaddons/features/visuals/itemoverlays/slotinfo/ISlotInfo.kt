package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.EnchantedBookSlotInfo
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.screen.slot.Slot
import net.minecraft.util.Colors

interface ISlotInfo {
	val config get() = NobaConfigManager.config.uiAndVisuals.slotInfo

	val enabled: Boolean
	fun handle(event: ScreenRenderEvents.DrawSlot)

	fun drawInfo(event: ScreenRenderEvents.DrawSlot, info: SlotInfo) {
		renderSlotInfo(event.ctx, event.textRenderer, event.slot, info)
	}

	fun drawOverlay(event: ScreenRenderEvents.DrawSlot, text: String, color: Int = Colors.WHITE) {
		drawStackOverlay(event.ctx, event.textRenderer, event.x, event.y, text, color)
	}

	companion object {
		private var init = false
		private val slotInfos = listOf<ISlotInfo>(
//			BestiaryLevelSlotInfo,
//			CollectionTierSlotInfo,
//			DungeonHeadSlotInfo,
			EnchantedBookSlotInfo,
//			KuudraKeySlotInfo,
//			MasterSkullSlotInfo,
//			MasterStarSlotInfo,
//			MinionTierSlotInfo,
//			PetLevelSlotInfo,
//			PotionSlotInfo,
//			RancherBootsSpeedSlotInfo,
//			SkillLevelSlotInfo,
//			SkyBlockLevelSlotInfo
		)

		fun init() {
			check(!init) { "Already initialized slot info!" }
			init = true

			slotInfos.forEach { handler ->
				ScreenRenderEvents.DRAW_SLOT.register {
					if(handler.enabled) handler.handle(it)
				}
			}
		}

		// TODO: Implement position handling
		fun renderSlotInfo(context: DrawContext, textRenderer: TextRenderer, slot: Slot, slotInfo: SlotInfo) {
			val width = textRenderer.getWidth(slotInfo.text)
			val scale = if(width > 16) 0.8333333f else 1.0f

			when(slotInfo.position) {
				Position.TOP_LEFT -> context.matrices.translate(0.0f, 0.0f, 200.0f)
				Position.TOP_RIGHT -> context.matrices.translate(slot.x + 16.0f - width, 0.0f, 200.0f)
				Position.BOTTOM_LEFT -> context.matrices.translate(0.0f, 16.0f - textRenderer.fontHeight, 200.0f)
				Position.BOTTOM_RIGHT -> context.matrices.translate(slot.x + 16.0f - width, 16.0f - textRenderer.fontHeight, 200.0f)
			}

			RenderUtils.drawText(context, slotInfo.text, slot.x, slot.y, scale)
		}

		fun drawStackOverlay(ctx: DrawContext, textRenderer: TextRenderer, x: Int, y: Int, text: String, color: Int = Colors.WHITE) {
			ctx.matrices.push()
			ctx.matrices.translate(0.0F, 0.0F, 200.0F)
			ctx.drawText(textRenderer, text, x + 19 - 2 - textRenderer.getWidth(text), y + 6 + 3, color, true)
			ctx.matrices.pop()
		}
	}
}