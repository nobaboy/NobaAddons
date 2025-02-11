package me.nobaboy.nobaaddons.core.profile

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.utils.safeLoad
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import java.util.UUID

sealed class AbstractPerProfileDataLoader<T : AbstractPerProfileConfig> {
	init {
		SkyBlockEvents.PROFILE_CHANGE.register { getOrPut(it.profileId) }
	}

	protected val profiles = mutableMapOf<UUID?, T>()
	private var _profile: T? = null

	@Suppress("PropertyName") // this is designed to be extended by `companion object`s
	val PROFILE: T get() {
		var current = _profile?.takeIf { it.profile == SkyBlockAPI.currentProfile }
		if(current == null) {
			current = getOrPut(SkyBlockAPI.currentProfile)
			_profile = current
		}
		return current
	}

	private fun getOrPut(id: UUID?): T {
		return profiles.getOrPut(id) {
			val data = create(id)
			data.safeLoad()
			if(id != null) postLoad(id, data)
			data
		}
	}

	protected abstract fun create(id: UUID?): T
	protected open fun postLoad(id: UUID, data: T) { /* noop */ }
}