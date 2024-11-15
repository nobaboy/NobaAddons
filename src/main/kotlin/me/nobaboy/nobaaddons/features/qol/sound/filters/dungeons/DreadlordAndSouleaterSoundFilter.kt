package me.nobaboy.nobaaddons.features.qol.sound.filters.dungeons

import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.data.SoundData
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import net.minecraft.util.Identifier

object DreadlordAndSouleaterSoundFilter : ISoundFilter {
	override fun shouldFilter(sound: SoundData): Boolean {
		// Dreadlord and Souleater
		if(sound.id == Identifier.ofVanilla("entity.generic.explode") && sound.volume == 4.0f) return true
		if(sound.id == Identifier.ofVanilla("entity.wither.shoot") && sound.pitch == 1.4920635f && sound.volume == 0.2f) return true
		return false
	}

	override fun isEnabled() = IslandType.DUNGEONS.inIsland() && config.muteDreadlordAndSouleater
}