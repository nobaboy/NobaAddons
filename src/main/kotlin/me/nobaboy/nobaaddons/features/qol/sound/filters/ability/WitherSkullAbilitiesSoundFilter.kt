package me.nobaboy.nobaaddons.features.qol.sound.filters.ability

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.data.SoundData
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import net.minecraft.util.Identifier

object WitherSkullAbilitiesSoundFilter : ISoundFilter {
	private val SHOOT = Identifier.ofVanilla("entity.wither.shoot")
	private val EXPLODE = Identifier.ofVanilla("entity.generic.explode")

	private val SHOOT_VOLUMES = arrayOf(0.2f, 0.5f)

	override fun shouldFilter(sound: SoundData): Boolean {
		if(sound.id == EXPLODE && sound.pitch in (0.55..0.85) && sound.volume == 4.0f) return true
		if(sound.id == SHOOT && sound.pitch == 1.4920635f && sound.volume in SHOOT_VOLUMES) return true
		return false
	}

	override fun isEnabled(): Boolean = SkyBlockAPI.inSkyblock && config.muteWitherSkullAbilities
}
