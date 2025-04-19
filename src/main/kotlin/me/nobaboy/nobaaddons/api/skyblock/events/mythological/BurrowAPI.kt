package me.nobaboy.nobaaddons.api.skyblock.events.mythological

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.events.MythologicalDrops
import me.nobaboy.nobaaddons.core.events.MythologicalMobs
import me.nobaboy.nobaaddons.core.profile.DianaProfileData
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.InteractEvents
import me.nobaboy.nobaaddons.events.impl.render.ParticleEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.features.events.mythological.BurrowType
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.BlockUtils.getBlockAt
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TimedSet
import me.nobaboy.nobaaddons.utils.Timestamp
import net.minecraft.block.Blocks
import net.minecraft.particle.ParticleTypes
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object BurrowAPI {
	private val config get() = NobaConfig.events.mythological
	private val enabled: Boolean get() = config.findNearbyBurrows && DianaAPI.isActive

	private val CLEAR_BURROWS_MESSAGE by "Poof! You have cleared your griffin burrows!".fromRepo("mythological.clear_burrows")
	private val DEFEAT_MOBS_MESSAGE by "Defeat all the burrow defenders in order to dig it!".fromRepo("mythological.defeat_mobs")

	private val DIG_BURROW_PATTERN by Regex("^(?:You dug out a Griffin Burrow|You finished the Griffin burrow chain)! \\((?<chain>\\d)/4\\)").fromRepo("mythological.dig_burrow")
	private val DIG_MOB_PATTERN by Regex("^(?:Oi|Uh oh|Yikes|Woah|Oh|Danger|Good Grief)! You dug out (?:a )?(?<mob>[A-z ]+)!").fromRepo("mythological.dig_mob")
	private val DIG_TREASURE_PATTERN by Regex("^(?:RARE DROP|Wow)! You dug out (?:a )?(?<treasure>[A-z0-9-, ]+)(?: coins)?!").fromRepo("mythological.dig_treasure")

	private val burrows = mutableMapOf<NobaVec, Burrow>()
	private val recentlyDugBurrows = TimedSet<NobaVec>(1.minutes)

	private var lastDugBurrow: NobaVec? = null
	private var fakeBurrow: NobaVec? = null
	private var mobBurrow: NobaVec? = null

	private var lastBurrowChatMessage = Timestamp.distantPast()

	private val data get() = DianaProfileData.PROFILE

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		ParticleEvents.PARTICLE.register(this::onParticle)
		InteractEvents.BLOCK_INTERACT.register(this::onBlockInteract)
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onParticle(event: ParticleEvents.Particle) {
		if(!enabled) return

		val location = event.location.roundToBlock().lower()
		if(location in recentlyDugBurrows) return

		val particleType = BurrowParticleType.getParticleType(event) ?: return
		val burrow = burrows.getOrPut(location) { Burrow() }

		when(particleType) {
			BurrowParticleType.ENCHANT -> burrow.hasEnchant = true
			BurrowParticleType.START -> burrow.type = BurrowType.START
			BurrowParticleType.MOB -> burrow.type = BurrowType.MOB
			BurrowParticleType.TREASURE -> burrow.type = BurrowType.TREASURE
		}

		if(!burrow.hasEnchant || burrow.type == BurrowType.UNKNOWN || burrow.found) return

		MythologicalEvents.BURROW_FIND.invoke(MythologicalEvents.BurrowFind(location, burrow.type))
		burrow.found = true
	}

	private fun onBlockInteract(event: InteractEvents.BlockInteraction) {
		if(!enabled) return
		if(!DianaAPI.hasSpadeInHand(event.player)) return

		val location = event.location
		if(location.getBlockAt() != Blocks.GRASS_BLOCK) return

		if(location == fakeBurrow) {
			fakeBurrow = null
			tryDigBurrow(location, ignoreFound = true)
			return
		}

		if(location !in burrows) return
		lastDugBurrow = location

		Scheduler.schedule(20) {
			if(lastBurrowChatMessage.elapsedSince() > 2.seconds) burrows.remove(location)
		}
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return

		when {
			message.startsWith(" ☠ You were killed by") -> burrows.remove(mobBurrow)
			message == DEFEAT_MOBS_MESSAGE -> lastBurrowChatMessage = Timestamp.now()
			message == CLEAR_BURROWS_MESSAGE -> reset()
		}

		DIG_BURROW_PATTERN.onFullMatch(message) {
			data.burrowsDug += 1L
			if(groups["chain"]?.value?.toInt() == 4) data.chainsFinished += 1L

			lastBurrowChatMessage = Timestamp.now()
			if(lastDugBurrow?.let { tryDigBurrow(it) } != true) return
			fakeBurrow = lastDugBurrow
		}

		DIG_MOB_PATTERN.onFullMatch(message) {
			lastBurrowChatMessage = Timestamp.now()
			mobBurrow = lastDugBurrow

			val mob = MythologicalMobs.getByName(groups["mob"]?.value ?: return) ?: return
			MythologicalEvents.MOB_DIG.invoke(MythologicalEvents.MobDig(mob))
		}

		DIG_TREASURE_PATTERN.onFullMatch(message) {
			val treasure = groups["treasure"]?.value ?: return
			lastBurrowChatMessage = Timestamp.now()

			treasure.replace(",", "").toIntOrNull()?.let { coins ->
				MythologicalEvents.TREASURE_DIG.invoke(MythologicalEvents.TreasureDig(MythologicalDrops.COINS, coins))
				return
			}

			val drop = MythologicalDrops.getByName(treasure) ?: return
			MythologicalEvents.TREASURE_DIG.invoke(MythologicalEvents.TreasureDig(drop))
		}
	}

	private fun tryDigBurrow(location: NobaVec, ignoreFound: Boolean = false): Boolean {
		val burrow = burrows[location] ?: return false
		if(!burrow.found && !ignoreFound) return false

		burrows.remove(location)
		recentlyDugBurrows.add(location)
		lastDugBurrow = null

		MythologicalEvents.BURROW_DIG.invoke(MythologicalEvents.BurrowDig(location))
		return true
	}

	fun reset() {
		burrows.clear()
		recentlyDugBurrows.clear()
		lastDugBurrow = null
		fakeBurrow = null
		mobBurrow = null
	}

	private enum class BurrowParticleType(val check: ParticleEvents.Particle.() -> Boolean) {
		ENCHANT({ type == ParticleTypes.ENCHANT && count == 5 && speed == 0.05f && offset.x == 0.5 && offset.y == 0.4 && offset.z == 0.5 }),
		START({ type == ParticleTypes.ENCHANTED_HIT && count == 4 && speed == 0.01f && offset.x == 0.5 && offset.y == 0.1 && offset.z == 0.5 }),
		MOB({ type == ParticleTypes.CRIT && count == 3 && speed == 0.01f && offset.x == 0.5 && offset.y == 0.1 && offset.z == 0.5 }),
		TREASURE({ type == ParticleTypes.DRIPPING_LAVA && count == 2 && speed == 0.01f && offset.x == 0.35 && offset.y == 0.1 && offset.z == 0.35 });

		companion object {
			fun getParticleType(event: ParticleEvents.Particle): BurrowParticleType? {
				if(!event.forceSpawn) return null
				return entries.firstOrNull { it.check(event) }
			}
		}
	}

	private data class Burrow(
		var hasEnchant: Boolean = false,
		var type: BurrowType = BurrowType.UNKNOWN,
		var found: Boolean = false
	)
}