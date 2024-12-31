package me.nobaboy.nobaaddons.utils.sound

import me.nobaboy.nobaaddons.utils.StringUtils.title

enum class NotificationSound(val sound: PlayableSound?) {
	DING(SoundUtils.dingLowSound),
	ZELDA_SECRET_SOUND(SoundUtils.zeldaSecretSound),
	NONE(null);

	fun play() {
		sound?.play()
	}

	override fun toString(): String = name.replace("_", " ").title()
}