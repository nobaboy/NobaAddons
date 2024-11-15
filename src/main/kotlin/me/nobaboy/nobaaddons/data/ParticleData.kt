package me.nobaboy.nobaaddons.data

import me.nobaboy.nobaaddons.utils.NobaVec
import net.minecraft.particle.ParticleType

data class ParticleData(
	val type: ParticleType<*>,
	val location: NobaVec,
	val count: Int,
	val speed: Float,
	val offset: NobaVec,
	val isLongDistance: Boolean
)