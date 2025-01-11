package me.nobaboy.nobaaddons.features.qol.sound.filters.rift

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.SoundEvents
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import net.minecraft.util.Identifier

object KillerSpringSoundFilter : ISoundFilter {
	private val WITHER_SPAWN = Identifier.ofVanilla("entity.wither.spawn")

	override val enabled: Boolean get() = config.muteKillerSpring && SkyBlockIsland.RIFT.inIsland()

	override fun onSound(sound: SoundEvents.AllowSound) {
		if(sound.id == WITHER_SPAWN && sound.volume == 0.085f) sound.cancel()
	}
}