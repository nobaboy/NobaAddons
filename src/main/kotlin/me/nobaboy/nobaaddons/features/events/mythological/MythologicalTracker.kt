package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.skyblock.events.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.core.events.MythologicalDrops
import me.nobaboy.nobaaddons.core.events.MythologicalMobs
import me.nobaboy.nobaaddons.core.profile.DianaProfileData
import me.nobaboy.nobaaddons.events.impl.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.ui.TextHudElement
import me.nobaboy.nobaaddons.ui.UIManager
import me.nobaboy.nobaaddons.utils.NumberUtils.addSeparators
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.Text
import net.minecraft.util.Formatting

// TODO: Allow for Four-Eyed Fish coins to be tracked, would require some extensive rework of PetAPI
object MythologicalTracker {
	private val config get() = NobaConfig.INSTANCE.events.mythological
	private val enabled: Boolean get() = config.tracker && DianaAPI.isActive

	private val data get() = DianaProfileData.PROFILE

	fun init() {
		MythologicalEvents.MOB_DIG.register(this::onMobDig)
		MythologicalEvents.TREASURE_DIG.register(this::onTreasureDig)
//		MythologicalEvents.MOB_DROP.register(this::onMobDrop)
		UIManager.add(MythologicalTrackerHudElement)
	}

	private fun onMobDig(event: MythologicalEvents.MobDig) {
		if(!enabled) return
		event.mob.let { data.mobs[it] = data.mobs.getOrDefault(it, 0L) + 1L }
	}

	private fun onTreasureDig(event: MythologicalEvents.TreasureDig) {
		if(!enabled) return
		event.drop.let { data.drops[it] = data.drops.getOrDefault(it, 0L) + event.amount }
	}

	object MythologicalTrackerHudElement : TextHudElement(UISettings.mythologicalTracker) {
		override val name: Text = tr("nobaaddons.ui.mythologicalTracker", "Mythological Tracker")
		override val size: Pair<Int, Int> = 125 to 200
		override val enabled: Boolean get() = MythologicalTracker.enabled

		override fun renderText(context: DrawContext) {
			renderLines(context, buildList {
				add(buildText {
					append(tr("nobaaddons.ui.mythologicalTracker.burrowsDug", "Burrows Dug").formatted(Formatting.GOLD))
					append(": ${data.burrowsDug.addSeparators()}")
				})
				add(Text.empty())
				MythologicalMobs.entries.forEach {
					add(buildText {
						append(it.toText())
						append(": ${data.mobs.getOrDefault(it, 0L).addSeparators()}")
					})
				}
				add(Text.empty())
				MythologicalDrops.entries.forEach {
					add(buildText {
						append(it.toText())
						append(": ${data.drops.getOrDefault(it, 0L).addSeparators()}")
					})
				}
			})
		}
	}
}