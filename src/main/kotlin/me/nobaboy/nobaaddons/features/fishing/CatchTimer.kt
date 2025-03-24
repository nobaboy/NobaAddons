package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.events.impl.render.EntityNametagRenderEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.ui.ElementAlignment
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.text.Text

object CatchTimer {
	private val enabled get() = NobaConfig.INSTANCE.fishing.catchTimerHudElement && SkyBlockAPI.inSkyBlock
	private var timer: ArmorStandEntity? = null

	private val TIMER_REGEX by Regex("^(?:\\d+\\.\\d+|!{3})$").fromRepo("fishing.catch_timer")

	fun init() {
		EntityNametagRenderEvents.VISIBILITY.register(this::hideTimer)
		UIManager.add(CatchTimerHudElement)
	}

	private fun hideTimer(event: EntityNametagRenderEvents.Visibility) {
		if(!enabled || MCUtils.player?.fishHook == null) return

		val entity = event.entity as? ArmorStandEntity ?: return
		if(TIMER_REGEX.matchEntire(entity.name.string.cleanFormatting()) == null) return

		timer = entity
		event.shouldRender = false
	}

	private object CatchTimerHudElement : TextHudElement(UISettings.catchTimer) {
		override val name: Text = tr("nobaaddons.ui.fishingCatchTimer", "Fishing Catch Timer")
		override val size: Pair<Int, Int> = 20 to 20
		override val minScale: Float = 1.5f

		override val enabled: Boolean by CatchTimer::enabled

		override fun renderText(context: DrawContext) {
			timer?.let {
				if(it.isRemoved) {
					timer = null
					return
				}
				renderLine(context, it.displayName!!, alignment = ElementAlignment.CENTER)
			}
		}

		override fun reset() {
			scale = 2f
			// TODO scaling up causes element positions to shift slightly between different screen sizes,
			//      which is especially noticeable with this
			elementPosition.x = 0.48
			elementPosition.y = 0.55
		}
	}
}