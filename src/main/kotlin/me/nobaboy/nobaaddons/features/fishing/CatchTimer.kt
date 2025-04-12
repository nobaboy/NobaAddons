package me.nobaboy.nobaaddons.features.fishing

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.events.impl.render.EntityNametagRenderEvents
import me.nobaboy.nobaaddons.repo.Repo
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
	private val config get() = NobaConfig.fishing
	private val enabled: Boolean get() = config.catchTimerHudElement && SkyBlockAPI.inSkyBlock

	private var timer: ArmorStandEntity? = null

	private val TIMER_REGEX by Repo.regex("fishing.catch_timer", "^(?:\\d+\\.\\d+|!{3})$")

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
		override val enabled: Boolean by CatchTimer::enabled

		// allowing the positional alignment doesn't make sense for this element with how small it is
		override val alignment: ElementAlignment = ElementAlignment.LEFT

		override fun renderText(context: DrawContext) {
			// TODO allow for aligning this text to the center of the element
			timer?.let {
				if(it.isRemoved) {
					timer = null
					return
				}
				renderLine(context, it.displayName!!)
			}
		}
	}
}