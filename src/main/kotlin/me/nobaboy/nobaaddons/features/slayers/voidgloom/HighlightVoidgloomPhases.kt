package me.nobaboy.nobaaddons.features.slayers.voidgloom

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.skyblock.SlayerAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.slayer.SlayerBoss
import me.nobaboy.nobaaddons.events.QuarterSecondPassedEvent
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.render.EntityOverlay.highlight
import net.minecraft.block.Blocks
import net.minecraft.entity.mob.EndermanEntity

object HighlightVoidgloomPhases {
	private val config get() = NobaConfig.INSTANCE.slayers.voidgloom
	private val enabled: Boolean get() = config.highlightPhases && SkyBlockIsland.THE_END.inIsland() &&
		SlayerAPI.currentQuest?.let { it.boss == SlayerBoss.VOIDGLOOM && it.spawned && it.entity != null } == true

	fun init() {
		QuarterSecondPassedEvent.EVENT.register { onQuarterSecondPassed() }
	}

	private fun onQuarterSecondPassed() {
		if(!enabled) return

		val color = getHighlightColor() ?: return
		SlayerAPI.currentQuest?.entity?.highlight(color)
	}

	private fun getHighlightColor(): NobaColor? {
		val currentQuest = SlayerAPI.currentQuest ?: return null

		val armorStandName = currentQuest.armorStand?.name?.string.orEmpty()
		val isHoldingBeacon = (currentQuest.entity as? EndermanEntity)?.carriedBlock?.block == Blocks.BEACON

		return when {
			isHoldingBeacon || YangGlyphFeatures.inBeaconPhase -> config.beaconPhaseColor
			armorStandName.contains("Hits") -> config.hitPhaseColor
			else -> config.damagePhaseColor
		}
	}
}