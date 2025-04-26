@file:UseSerializers(VersionKSerializer::class)

package me.nobaboy.nobaaddons.core.killswitch

import kotlinx.serialization.Serializable
import kotlinx.serialization.UseSerializers
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.serializers.VersionKSerializer
import net.fabricmc.loader.api.Version

private val VERSION by NobaAddons::VERSION_INFO

/**
 * ```json
 * {
 *   // Disable the feature with the internal ID of 'feature'
 *   "feature": [
 *     {
 *       // from is inclusive, while fixed is exclusive
 *       "fromVersion": "1.0.0-Beta.2",
 *       "fixedVersion": "1.0.0",
 *       // Optional reason; displayed in YACL when the associated feature has been disabled
 *       "reason": "Does bad things"
 *     }
 *   ]
 * }
 * ```
 */
@Serializable
data class KillSwitchData(
	val fromVersion: Version? = null,
	val fixedVersion: Version? = null,
	val reason: String? = null,
) {
	val isApplicable: Boolean get() = when {
		fromVersion?.let { it >= VERSION } == true -> false
		fixedVersion?.let { it < VERSION } == true -> false
		else -> true
	}

	companion object {
		val INSTANCE by Repo.create("killswitch.json", MapSerializer(String.serializer(), ListSerializer(serializer())))
	}
}