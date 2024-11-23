package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.BestiarySlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.CollectionTierSlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.DungeonHeadTierSlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.EnchantedBookSlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.GardenPlotPestInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.KuudraKeyTierInfoSlot
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.MasterSkullTierSlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.MasterStarTierSlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.MinionTierSlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.NewYearCakeSlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.PotionLevelSlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.SkillLevelSlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.SkyBlockLevelSlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.TuningPointsSlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.VacuumPestsSlotInfo
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.screen.slot.Slot

// TODO: Implement remaining 2 slot infos once mythological branch is merge as they both require PetAPI
interface ISlotInfo {
	val config get() = NobaConfigManager.config.uiAndVisuals.slotInfo

	val enabled: Boolean
	fun handle(event: ScreenRenderEvents.DrawSlot)

	fun drawInfo(event: ScreenRenderEvents.DrawSlot, info: SlotInfo) {
		renderSlotInfo(event.context, event.textRenderer, event.slot, info)
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
//			PetSlotInfo,
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

		fun renderSlotInfo(context: DrawContext, textRenderer: TextRenderer, slot: Slot, slotInfo: SlotInfo) {
			val width = textRenderer.getWidth(slotInfo.text)
			val scale = if(width > 16) 0.8333333f else 1.0f

			context.matrices.push()

			when(slotInfo.position) {
				Position.TOP_LEFT -> context.matrices.translate(0.0f, 0.0f, 200.0f)
				Position.TOP_RIGHT -> context.matrices.translate(16.0f - width + 1.0f, 0.0f, 200.0f)
				Position.BOTTOM_LEFT -> context.matrices.translate(0.0f, textRenderer.fontHeight.toFloat(), 200.0f)
				Position.BOTTOM_RIGHT -> context.matrices.translate(16.0f - width + 1.0f, textRenderer.fontHeight.toFloat(), 200.0f)
			}

			RenderUtils.drawText(context, slotInfo.text, slot.x, slot.y, scale)
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