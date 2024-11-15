package me.nobaboy.nobaaddons.data

import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.util.Identifier

data class SoundData(
	val id: Identifier,
	val location: NobaVec,
	val pitch: Float,
	val volume: Float
)