package me.nobaboy.nobaaddons.utils.sound

import net.minecraft.sound.SoundCategory

interface PlayableSound {
	fun play(category: SoundCategory = SoundCategory.MASTER)
}