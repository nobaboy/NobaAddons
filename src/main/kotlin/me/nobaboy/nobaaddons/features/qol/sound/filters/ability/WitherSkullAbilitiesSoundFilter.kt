package me.nobaboy.nobaaddons.features.qol.sound.filters.ability

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.events.impl.client.SoundEvents
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import net.minecraft.util.Identifier

object WitherSkullAbilitiesSoundFilter : ISoundFilter {
	private val SHOOT = Identifier.ofVanilla("entity.wither.shoot")
	private val EXPLODE = Identifier.ofVanilla("entity.generic.explode")

	private val SHOOT_VOLUMES = listOf(0.2f, 0.5f)

	override val enabled: Boolean get() = config.muteWitherSkullAbilities && SkyBlockAPI.inSkyBlock

	override fun onSound(sound: SoundEvents.AllowSound) {
		val shouldFilter = when(sound.id) {
			EXPLODE -> sound.pitch in (0.55..0.85) && sound.volume == 4.0f
			SHOOT -> sound.pitch == 1.4920635f && sound.volume in SHOOT_VOLUMES
			else -> false
		}

		if(shouldFilter) sound.cancel()
	}
}
