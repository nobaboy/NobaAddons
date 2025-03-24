package me.nobaboy.nobaaddons.features.qol.sound.filters.mobs

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.impl.client.SoundEvents
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import net.minecraft.util.Identifier

object BansheeSoundFilter : ISoundFilter {
	private val GHAST_WARN = Identifier.ofVanilla("entity.ghast.warn")

	override val enabled: Boolean get() = config.muteBanshee && SkyBlockIsland.BACKWATER_BAYOU.inIsland()

	override fun onSound(sound: SoundEvents.AllowSound) {
		if(sound.id == GHAST_WARN && sound.pitch in 0.1f..0.5f) sound.cancel()
	}
}