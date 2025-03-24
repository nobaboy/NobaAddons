package me.nobaboy.nobaaddons.features.inventory.slotinfo

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.inventory.slotinfo.items.*
import me.nobaboy.nobaaddons.features.inventory.slotinfo.uielements.*
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import net.minecraft.client.font.TextRenderer
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text

interface ISlotInfo {
	val config get() = NobaConfig.INSTANCE.inventory.slotInfo

	val enabled: Boolean
	fun handle(event: ScreenRenderEvents.DrawItem)

	fun drawInfo(event: ScreenRenderEvents.DrawItem, text: Text, position: Position = Position.TOP_LEFT) {
		renderSlotInfo(event.context, event.textRenderer, event.x, event.y, text, position)
	}

	fun drawCount(event: ScreenRenderEvents.DrawItem, text: Text, color: NobaColor = NobaColor.WHITE) {
		drawStackOverlay(event.context, event.textRenderer, event.x, event.y, text, color)
	}

	fun drawCount(event: ScreenRenderEvents.DrawItem, text: String, color:NobaColor = NobaColor.WHITE) {
		drawStackOverlay(event.context, event.textRenderer, event.x, event.y, text, color)
	}

	companion object {
		const val CHECK = "✔"

		private var init = false
		private val slotInfos = arrayOf(
			// UI Elements
			BestiarySlotInfo,
			CollectionTierSlotInfo,
			GardenPlotPestSlotInfo,
			SkillLevelSlotInfo,
			SkyBlockLevelSlotInfo,
			TuningPointsSlotInfo,
			TrophyFishSlotInfo,
			// Items
			AttributeShardSlotInfo,
			DungeonHeadTierSlotInfo,
			EnchantedBookSlotInfo,
			KuudraKeyTierSlotInfo,
			MasterSkullTierSlotInfo,
			MasterStarTierSlotInfo,
			MinionTierSlotInfo,
			NewYearCakeSlotInfo,
			PetSlotInfo,
			PotionLevelSlotInfo,
			RanchersBootsSpeedSlotInfo,
			VacuumPestsSlotInfo,
		)

		fun init() {
			check(!init) { "Already initialized slot info!" }
			init = true

			slotInfos.forEach { slotInfo ->
				ScreenRenderEvents.DRAW_ITEM.register {
					if(SkyBlockAPI.inSkyBlock && slotInfo.enabled) slotInfo.handle(it)
				}
			}
		}

		fun renderSlotInfo(context: DrawContext, textRenderer: TextRenderer, x: Int, y: Int, text: Text, position: Position) {
			val width = textRenderer.getWidth(text)
			val scale = if(width > 16) 0.8333333f else 1f

			context.matrices.push()

			when(position) {
				Position.TOP_LEFT -> context.matrices.translate(0f, 0f, 200f)
				Position.TOP_RIGHT -> context.matrices.translate(16f - width + 1f, 0f, 200f)
				Position.BOTTOM_LEFT -> context.matrices.translate(0f, textRenderer.fontHeight.toFloat(), 200f)
				Position.BOTTOM_RIGHT -> context.matrices.translate(16f - width + 1f, textRenderer.fontHeight.toFloat(), 200f)
			}

			RenderUtils.drawText(context, text, x, y, scale)
			context.matrices.pop()
		}

		fun drawStackOverlay(context: DrawContext, textRenderer: TextRenderer, x: Int, y: Int, text: Text, color: NobaColor = NobaColor.WHITE) {
			context.matrices.push()
			context.matrices.translate(0f, 0f, 200f)
			context.drawText(textRenderer, text, x + 19 - 2 - textRenderer.getWidth(text), y + 6 + 3, color.rgb, true)
			context.matrices.pop()
		}

		fun drawStackOverlay(context: DrawContext, textRenderer: TextRenderer, x: Int, y: Int, text: String, color: NobaColor = NobaColor.WHITE) {
			context.matrices.push()
			context.matrices.translate(0f, 0f, 200f)
			context.drawText(textRenderer, text, x + 19 - 2 - textRenderer.getWidth(text), y + 6 + 3, color.rgb, true)
			context.matrices.pop()
		}
	}
}