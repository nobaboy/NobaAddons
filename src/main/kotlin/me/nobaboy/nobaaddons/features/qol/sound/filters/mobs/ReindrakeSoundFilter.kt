package me.nobaboy.nobaaddons.features.qol.sound.filters.mobs

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.SoundEvents
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import net.minecraft.util.Identifier

object ReindrakeSoundFilter : ISoundFilter {
	private val SPAWN_SOUND = Identifier.ofVanilla("entity.warden.emerge")
	private val GIFT_DROP_SOUND = Identifier.ofVanilla("item.totem.use")

	override val enabled: Boolean get() = SkyBlockIsland.JERRYS_WORKSHOP.inIsland()

	override fun onSound(sound: SoundEvents.AllowSound) {
		// we're not bothering with pitches or volumes here since these are the only times these sounds
		// are ever used on the island
		val shouldFilter = when(sound.id) {
			SPAWN_SOUND -> config.muteReindrakeSpawn
			GIFT_DROP_SOUND -> config.muteReindrakeGiftDrop
			else -> false
		}

		if(shouldFilter) sound.cancel()
	}
}