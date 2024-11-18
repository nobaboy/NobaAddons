package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl.EnchantedBookSlotInfo
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack
import net.minecraft.screen.slot.Slot

interface ISlotInfo {
	val config get() = NobaConfigManager.config.uiAndVisuals.slotInfo

	fun isEnabled(): Boolean

	fun getSlotInfos(itemStack: ItemStack): List<SlotInfo>? = null
	fun getStackOverlay(itemStack: ItemStack): String? = null

	companion object {
		private var init = false
		private val slotInfos = mutableListOf<ISlotInfo>(
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

			ScreenRenderEvents.DRAW_SLOT.register { context, textRenderer, slot ->
				slotInfos.asSequence()
					.filter { it.config.enabled && it.isEnabled() }
					.forEach { slotInfo ->
						slotInfo.getStackOverlay(slot.stack)?.let { stackOverlay ->
							context.drawStackOverlay(textRenderer, slot.stack, slot.x, slot.y, stackOverlay)
						}

						slotInfo.getSlotInfos(slot.stack)?.forEach {
							renderSlotInfo(context, textRenderer, slot, it)
						}
					}
			}
		}

		// TODO: Implement position handling
		private fun renderSlotInfo(context: DrawContext, textRenderer: TextRenderer, slot: Slot, slotInfo: SlotInfo) {
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
	}
}