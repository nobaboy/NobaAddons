package me.nobaboy.nobaaddons.utils.sound

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

enum class NotificationSound(val sound: PlayableSound? = null) : NameableEnum {
	DING(SoundUtils.dingLowSound),
	ZELDA_SECRET_SOUND(SoundUtils.zeldaSecretSound),
	NONE,
	;

	fun play() {
		sound?.play()
	}

	override fun getDisplayName(): Text = when(this) {
		DING -> tr("nobaaddons.label.notificationSound.ding", "Ding")
		ZELDA_SECRET_SOUND -> tr("nobaaddons.label.notificationSound.zeldaSecretSound", "Zelda Secret Sound")
		NONE -> tr("nobaaddons.label.notificationSound.none", "None")
	}
}