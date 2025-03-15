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
	private val config = NobaConfig.INSTANCE.events.mythological
	private val enabled: Boolean get() = config.findNearbyBurrows && DianaAPI.isActive

	private val CLEAR_BURROWS_MESSAGE by "Poof! You have cleared your griffin burrows!".fromRepo("mythological.clear_burrows")
	private val DEFEAT_MOBS_MESSAGE by "Defeat all the burrow defenders in order to dig it!".fromRepo("mythological.defeat_mobs")

	private val DIG_BURROW_PATTERN by Regex("^(?:You dug out a Griffin Burrow|You finished the Griffin burrow chain)! \\((?<chain>\\d)/4\\)").fromRepo("mythological.dig_burrow")
	private val DIG_MOB_PATTERN by Regex("^(?:Oi|Uh oh|Yikes|Woah|Oh|Danger|Good Grief)! You dug out (?:a )?(?<mob>[A-z ]+)!").fromRepo("mythological.dig_mob")
	private val DIG_TREASURE_PATTERN by Regex("^(RARE DROP|Wow)! You dug out (?:a )?(?<treasure>[A-z0-9-, ]+)(?: coins)?!").fromRepo("mythological.dig_treasure")

	val burrows = mutableSetOf<Burrow>()
	private val recentlyDugBurrows = TimedSet<Burrow>(1.minutes)

	private var lastDugBurrow: Burrow? = null
	private var fakeBurrow: Burrow? = null
	private var mobBurrow: Burrow? = null

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
		if(recentlyDugBurrows.any { it.location == location }) return

		val particleType = BurrowParticleType.getParticleType(event) ?: return
		val burrow = burrows.firstOrNull { it.location == location } ?: Burrow(location).also { burrows.add(it) }

		when(particleType) {
			BurrowParticleType.ENCHANT -> burrow.hasEnchant = true
			BurrowParticleType.START -> burrow.type = BurrowType.START
			BurrowParticleType.MOB -> burrow.type = BurrowType.MOB
			BurrowParticleType.TREASURE -> burrow.type = BurrowType.TREASURE
		}

		if(burrow.found || !burrow.hasEnchant || burrow.type == BurrowType.UNKNOWN) return

		MythologicalEvents.BURROW_FIND.invoke(MythologicalEvents.BurrowFind(location, burrow.type))
		burrow.found = true
	}

	private fun onBlockInteract(event: InteractEvents.BlockInteraction) {
		if(!enabled) return
		if(!DianaAPI.hasSpadeInHand(event.player)) return

		val location = event.location
		if(location.getBlockAt() != Blocks.GRASS_BLOCK) return

		if(location == fakeBurrow?.location) {
			fakeBurrow = null
			tryDigBurrow(location, ignoreFound = true)
			return
		}

		val burrow = burrows.firstOrNull { it.location == location } ?: return
		lastDugBurrow = burrow

		Scheduler.schedule(20) {
			if(lastBurrowChatMessage.elapsedSince() > 2.seconds) burrows.removeIf { it == burrow }
		}
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return

		when {
			message == CLEAR_BURROWS_MESSAGE -> reset()
			message == DEFEAT_MOBS_MESSAGE -> lastBurrowChatMessage = Timestamp.now()
			message.startsWith(" ☠ You were killed by") -> burrows.removeIf { it == mobBurrow }
		}

		DIG_BURROW_PATTERN.onFullMatch(message) {
			data.burrowsDug += 1L
			if(groups["chain"]?.value?.toInt() == 4) data.chainsFinished += 1L

			lastBurrowChatMessage = Timestamp.now()
			if(lastDugBurrow?.let { tryDigBurrow(it.location) } != true) return
			fakeBurrow = lastDugBurrow
		}

		DIG_MOB_PATTERN.onFullMatch(message) {
			val mob = MythologicalMobs.getByName(groups["mob"]?.value ?: return) ?: return
			MythologicalEvents.MOB_DIG.invoke(MythologicalEvents.MobDig(mob))
			lastBurrowChatMessage = Timestamp.now()
			mobBurrow = lastDugBurrow
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
		val burrow = burrows.firstOrNull { it.location == location } ?: return false
		if(!burrow.found && !ignoreFound) return false

		burrows.removeIf { it == burrow }
		recentlyDugBurrows.add(burrow)
		lastDugBurrow = null

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
				return entries.find { it.check(event) }
			}
		}
	}

	data class Burrow(
		val location: NobaVec,
		var hasEnchant: Boolean = false,
		var type: BurrowType = BurrowType.UNKNOWN,
		var found: Boolean = false
	)
}