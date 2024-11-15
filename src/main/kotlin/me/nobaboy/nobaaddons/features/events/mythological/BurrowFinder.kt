package me.nobaboy.nobaaddons.features.events.mythological

import me.nobaboy.nobaaddons.api.mythological.DianaAPI
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.utils.NobaVec

object BurrowFinder {
	private val config get() = NobaConfigManager.config.events.mythological

//	private val burrows = mutableMapOf<NobaVec, Burrow>()
	private val recentlyDugBurrows = mutableListOf<NobaVec>()

	fun init() {
//		ParticleEvent.EVENT.register(this::handleParticle)
	}

	private fun isEnabled() = DianaAPI.isActive() && config.burrowGuess
}