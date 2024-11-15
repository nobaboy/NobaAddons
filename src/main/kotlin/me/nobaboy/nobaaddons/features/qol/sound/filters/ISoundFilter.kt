package me.nobaboy.nobaaddons.features.qol.sound.filters

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.data.SoundData
import me.nobaboy.nobaaddons.events.PlaySoundEvent
import me.nobaboy.nobaaddons.features.qol.sound.filters.dungeons.DreadlordAndSouleaterSoundFilter

interface ISoundFilter {
	val config get() = NobaConfigManager.config.qol.soundFilters

	fun isEnabled(): Boolean
	fun shouldFilter(sound: SoundData): Boolean

	companion object {
		private var init = false
		private val filters = mutableListOf<ISoundFilter>(
			DreadlordAndSouleaterSoundFilter
		)

		fun init() {
			check(!init) { "Already initialized sound filters!" }
			init = true

			PlaySoundEvent.ALLOW_SOUND.register { sound ->
				filters.asSequence().filter { it.isEnabled() }.none {
					runCatching { it.shouldFilter(sound) }
						.onFailure { error ->
							NobaAddons.LOGGER.error("Sound filter {} threw an error while processing a sound event", it, error)
						}
						.getOrDefault(false)
				}
			}
		}
	}
}