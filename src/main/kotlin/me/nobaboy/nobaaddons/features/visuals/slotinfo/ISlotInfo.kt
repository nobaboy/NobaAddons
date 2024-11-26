package me.nobaboy.nobaaddons.features.visuals.slotinfo

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.impl.*
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.screen.slot.Slot
import net.minecraft.text.Text

// TODO: Implement last slot info (not really last, but planned one)
interface ISlotInfo {
	val config get() = NobaConfigManager.config.uiAndVisuals.slotInfo

	val enabled: Boolean
	fun handle(event: ScreenRenderEvents.DrawSlot)

	fun drawInfo(event: ScreenRenderEvents.DrawSlot, text: Text, position: Position = Position.TOP_LEFT) {
		renderSlotInfo(event.context, event.textRenderer, event.slot, text, position)
	}

	fun drawCount(event: ScreenRenderEvents.DrawSlot, text: String, color: Int = -1) {
		drawStackOverlay(event.context, event.textRenderer, event.x, event.y, text, color)
	}

	companion object {
		private var init = false
		private val slotInfos = listOf<ISlotInfo>(
			BestiarySlotInfo,
			CollectionTierSlotInfo,
			DungeonHeadTierSlotInfo,
			EnchantedBookSlotInfo,
			GardenPlotPestInfo,
			KuudraKeyTierInfoSlot,
			MasterSkullTierSlotInfo,
			MasterStarTierSlotInfo,
			MinionTierSlotInfo,
			NewYearCakeSlotInfo,
			PetSlotInfo,
			PotionLevelSlotInfo,
//			RancherBootsSlotInfo,
			SkillLevelSlotInfo,
			SkyBlockLevelSlotInfo,
			TuningPointsSlotInfo,
			VacuumPestsSlotInfo
		)

		fun init() {
			check(!init) { "Already initialized slot info!" }
			init = true

			slotInfos.forEach { handler ->
				ScreenRenderEvents.DRAW_SLOT.register {
					if(SkyBlockAPI.inSkyBlock && handler.enabled) handler.handle(it)
				}
			}
		}

		fun renderSlotInfo(context: DrawContext, textRenderer: TextRenderer, slot: Slot, text: Text, position: Position) {
			val width = textRenderer.getWidth(text)
			val scale = if(width > 16) 0.8333333f else 1.0f

			context.matrices.push()

			when(position) {
				Position.TOP_LEFT -> context.matrices.translate(0.0f, 0.0f, 200.0f)
				Position.TOP_RIGHT -> context.matrices.translate(16.0f - width + 1.0f, 0.0f, 200.0f)
				Position.BOTTOM_LEFT -> context.matrices.translate(0.0f, textRenderer.fontHeight.toFloat(), 200.0f)
				Position.BOTTOM_RIGHT -> context.matrices.translate(16.0f - width + 1.0f, textRenderer.fontHeight.toFloat(), 200.0f)
			}

			RenderUtils.drawText(context, text, slot.x, slot.y, scale)
			context.matrices.pop()
		}

		fun drawStackOverlay(context: DrawContext, textRenderer: TextRenderer, x: Int, y: Int, text: String, color: Int = -1) {
			context.matrices.push()
			context.matrices.translate(0.0F, 0.0F, 200.0F)
			context.drawText(textRenderer, text, x + 19 - 2 - textRenderer.getWidth(text), y + 6 + 3, color, true)
			context.matrices.pop()
		}
	}
}