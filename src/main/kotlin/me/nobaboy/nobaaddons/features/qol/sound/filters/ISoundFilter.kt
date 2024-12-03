package me.nobaboy.nobaaddons.features.qol.sound.filters

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.SoundEvents
import me.nobaboy.nobaaddons.features.qol.sound.filters.ability.WitherSkullAbilitiesSoundFilter
import me.nobaboy.nobaaddons.features.qol.sound.filters.dwarvenmines.GoneWithTheWindSoundFilter
import me.nobaboy.nobaaddons.features.qol.sound.filters.mobs.ReindrakeSoundFilter
import me.nobaboy.nobaaddons.features.qol.sound.filters.rift.KillerSpringSoundFilter

interface ISoundFilter {
	val config get() = NobaConfigManager.config.qol.soundFilters

	val enabled: Boolean
	fun onSound(sound: SoundEvents.AllowSound)

	companion object {
		private var init = false
		private val filters = arrayOf<ISoundFilter>(
			// Items Abilities
			WitherSkullAbilitiesSoundFilter,
			// Mobs
			ReindrakeSoundFilter,
			// Dwarven Mines
			GoneWithTheWindSoundFilter,
			// Rift
			KillerSpringSoundFilter
		)

		fun init() {
			check(!init) { "Already initialized sound filters!" }
			init = true

			filters.forEach { handler ->
				SoundEvents.ALLOW_SOUND.register {
					if(handler.enabled) handler.onSound(it)
				}
			}
		}
	}
}