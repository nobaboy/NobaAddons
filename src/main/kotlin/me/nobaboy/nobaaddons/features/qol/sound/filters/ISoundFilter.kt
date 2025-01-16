package me.nobaboy.nobaaddons.features.qol.sound.filters

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.client.SoundEvents
import me.nobaboy.nobaaddons.features.qol.sound.filters.ability.WitherSkullAbilitiesSoundFilter
import me.nobaboy.nobaaddons.features.qol.sound.filters.dwarvenmines.GoneWithTheWindSoundFilter
import me.nobaboy.nobaaddons.features.qol.sound.filters.misc.PunchSoundFilter
import me.nobaboy.nobaaddons.features.qol.sound.filters.mobs.ReindrakeSoundFilter
import me.nobaboy.nobaaddons.features.qol.sound.filters.rift.KillerSpringSoundFilter

interface ISoundFilter {
	val config get() = NobaConfig.INSTANCE.qol.soundFilters

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
			KillerSpringSoundFilter,
			// Misc
			PunchSoundFilter,
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