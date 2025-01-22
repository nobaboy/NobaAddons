package me.nobaboy.nobaaddons.features

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.serializers.VersionKSerializer
import net.fabricmc.loader.api.Version

@Serializable
data class FeatureKillSwitch(
	val reason: String? = null,
	val from: @Serializable(with = VersionKSerializer::class) Version? = null,
	val to: @Serializable(with = VersionKSerializer::class) Version? = null,
) {
	val isApplicable: Boolean
		get() {
			if(to != null && to <= NobaAddons.VERSION_INFO) return false
			if(from != null && from > NobaAddons.VERSION_INFO) return false
			return true
		}
}