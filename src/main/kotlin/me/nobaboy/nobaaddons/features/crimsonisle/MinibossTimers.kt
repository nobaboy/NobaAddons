package me.nobaboy.nobaaddons.features.crimsonisle

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.Timestamp
import kotlin.time.Duration.Companion.minutes

object MinibossTimers {
	private val config get() = NobaConfig.crimsonIsle.minibossTimers
	private val enabled: Boolean get() = config.enabled && SkyBlockAPI.inSkyBlock

	private val MINIBOSS_SPAWN_REGEX by Regex("^BEWARE - (?:The )?(?<miniboss>[A-z ]+) is spawning\\.").fromRepo("crimson_isle.miniboss_spawn")
	private val MINIBOSS_DOWN_REGEX by Regex("^[ ](?<miniboss>[A-Z]+) DOWN!").fromRepo("crimson_isle.miniboss_down")

	private val minibosses = mutableMapOf<Miniboss, MinibossState>()

	fun init() {
//        SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return

		MINIBOSS_SPAWN_REGEX.onFullMatch(message) {
			return
		}

		MINIBOSS_DOWN_REGEX.onFullMatch(message) {

		}
	}

	private enum class Miniboss(
		val displayName: String,
		val area: Pair<NobaVec, NobaVec>,
	) {
		ASHFANG("Ashfang", NobaVec(-462, 155, -1035) to NobaVec(-507, 131, -955)),
		BARBARIAN_DUKE_X("Barbarian Duke X", NobaVec(-550, 101, -890) to NobaVec(-522, 131, -918)),
		BLADESOUL("Bladesoul", NobaVec(-330, 80, -486) to NobaVec(-257, 107, -545)),
		MAGE_OUTLAW("Mage Outlaw", NobaVec(-200, 98, -843) to NobaVec(-162, 116, -878)),
		MAGMA_BOSS("Magma Boss", NobaVec(-318, 59, -751) to NobaVec(-442, 90, -851)),
		;
	}

	private data class MinibossState(
		val killTime: Timestamp? = null,
		val spawnTime: Timestamp? = null
	) {
		private val respawnTime: Timestamp? get() = killTime?.let { it + 2.minutes }
	}
}