package me.nobaboy.nobaaddons.features.qol.sound.filters.rift

import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.data.SoundData
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import net.minecraft.util.Identifier

object KillerSpringSoundFilter : ISoundFilter {
	override fun shouldFilter(sound: SoundData): Boolean {
		return sound.id == Identifier.ofVanilla("entity.wither.spawn") && sound.volume == 0.085f
	}

	override fun isEnabled(): Boolean = IslandType.RIFT.inIsland() && config.muteKillerSpring
}