package me.nobaboy.nobaaddons.features

import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.serializers.VersionKSerializer
import net.fabricmc.loader.api.Version
import kotlin.reflect.KProperty

internal val KILLSWITCHES by Repo.create("killswitch.json", serializer<Map<String, KillSwitchData>>())
typealias SerializedVersion = @Serializable(with = VersionKSerializer::class) Version

/**
 * ```json
 * {
 *   "feature": {
 *     // enables kill switch for the entire feature
 *     "all": {
 *         // optional reason for why this feature has been disabled; this is currently never displayed anywhere in-game,
 *         // and is simply for reference in the repo.
 *         "reason": "...",
 *     },
 *     "options": {
 *       // enables kill switch for 'option' for versions <1.0.0
 *       "option": {
 *         "to": "1.0.0"
 *       }
 *     }
 *   }
 * }
 * ```
 */
@Serializable
data class KillSwitchData(
	val all: Option? = null,
	val options: Map<String, Option> = emptyMap(),
) {
	@Serializable
	data class Option(
		val reason: String? = null,
		val from: SerializedVersion? = null,
		val to: SerializedVersion? = null,
	) {
		val isApplicable: Boolean
			get() {
				if(to != null && to <= NobaAddons.VERSION_INFO) return false
				if(from != null && from > NobaAddons.VERSION_INFO) return false
				return true
			}
	}
}

/**
 * Utility method to check if a certain option has been disabled remotely
 *
 * This will throw an error at runtime if used in a class that doesn't extend [Feature]
 *
 * ```kt
 * val option by KillSwitch("option")
 *
 * // returns true if a kill switch is applied to this option
 * if(option) return
 * ```
 */
class KillSwitch(val option: String?) {
	operator fun getValue(instance: Any, @Suppress("unused") property: KProperty<*>): Boolean {
		check(instance is Feature) { "KillSwitch only supports usage in Feature classes" }
		val kill = KILLSWITCHES?.get(instance.id) ?: return false
		return kill.all?.isApplicable == true || option?.let { kill.options[it]?.isApplicable } == true
	}
}