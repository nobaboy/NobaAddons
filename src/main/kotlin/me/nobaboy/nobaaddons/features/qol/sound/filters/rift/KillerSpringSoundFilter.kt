package me.nobaboy.nobaaddons.features.qol.sound.filters.rift

import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.events.PlaySoundEvent
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import net.minecraft.util.Identifier

object KillerSpringSoundFilter : ISoundFilter {
	override val enabled get(): Boolean = IslandType.RIFT.inIsland() && config.muteKillerSpring

	override fun onSound(sound: PlaySoundEvent.AllowSound) {
		if(sound.id == Identifier.ofVanilla("entity.wither.spawn") && sound.volume == 0.085f) {
			sound.cancel()
		}
	}
}