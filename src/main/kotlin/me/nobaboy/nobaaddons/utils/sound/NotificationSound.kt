package me.nobaboy.nobaaddons.utils.sound

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text

enum class NotificationSound(val sound: PlayableSound?) : NameableEnum {
	DING(SoundUtils.dingSound),
	ZELDA_SECRET_SOUND(SoundUtils.zeldaSecretSound),
	NONE(null);

	fun play() {
		sound?.play()
	}

	override fun getDisplayName(): Text = name.replace("_", " ").title().toText()
}