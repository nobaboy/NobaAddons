package me.nobaboy.nobaaddons.features.qol.sound.filters.misc

import me.nobaboy.nobaaddons.events.SoundEvents
import me.nobaboy.nobaaddons.features.qol.sound.filters.ISoundFilter
import net.minecraft.util.Identifier

object PunchSoundFilter : ISoundFilter {
	private val PLAYER_ATTACK = Identifier.ofVanilla("entity.player.attack.weak")

	override val enabled: Boolean get() = config.mutePunch

	override fun onSound(sound: SoundEvents.AllowSound) {
		if(sound.id == PLAYER_ATTACK) sound.cancel()
	}
}