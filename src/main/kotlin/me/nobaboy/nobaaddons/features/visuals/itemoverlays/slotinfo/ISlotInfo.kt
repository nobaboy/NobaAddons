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
	fun shouldRender(itemStack: ItemStack): Boolean

	fun getSlotInfos(): List<SlotInfo>? = null
	fun getStackOverlay(): String? = null

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
					.filter { it.shouldRender(slot.stack) }
					.forEach { slotInfo ->
						slotInfo.getStackOverlay()?.let { stackOverlay ->
							context.drawStackOverlay(textRenderer, slot.stack, slot.x, slot.y, stackOverlay)
						}

						slotInfo.getSlotInfos()?.forEach {
							renderSlotInfo(context, textRenderer, slot, it)
						}
					}
			}
		}

		// TODO: Implement position handling
		private fun renderSlotInfo(context: DrawContext, textRenderer: TextRenderer, slot: Slot, slotInfo: SlotInfo) {
			val width = textRenderer.getWidth(slotInfo.text)
			val scale = if(width > 16) 0.8333333f else 1.0f

			context.matrices.translate(0.0f, 0.0f, 200.0f)
			RenderUtils.drawText(context, slotInfo.text, slot.x, slot.y, scale)
		}
	}
}