package me.nobaboy.nobaaddons.api.skyblock.events.mythological

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.render.ParticleEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.features.events.mythological.BurrowType
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.BlockUtils.getBlockAt
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TimedSet
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.block.Blocks
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.ActionResult
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object BurrowAPI {
	private val config get() = NobaConfig.INSTANCE.events.mythological
	private val enabled: Boolean get() = config.findNearbyBurrows && DianaAPI.isActive

	private val burrowDugPattern by Regex("^(You dug out a Griffin Burrow!|You finished the Griffin burrow chain!) \\(\\d/4\\)").fromRepo("mythological.dig_burrow")
	private val mobDugPattern by Regex("^[A-z ]+! You dug out (?:a )?[A-z ]+!").fromRepo("mythological.dig_mob")

	private val burrows = mutableMapOf<NobaVec, Burrow>()
	private val recentlyDugBurrows = TimedSet<NobaVec>(1.minutes)
	private var lastDugBurrow: NobaVec? = null

	// This is here in case the player digs the burrow before the particles spawn
	private var fakeBurrow: NobaVec? = null
	var mobBurrow: NobaVec? = null

	private var lastBurrowChatMessage = Timestamp.distantPast()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		ParticleEvents.PARTICLE.register(this::onParticle)
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
		AttackBlockCallback.EVENT.register { player, _, _, pos, _ -> onBlockClick(player, pos.toNobaVec()) }
		UseBlockCallback.EVENT.register { player, _, _, hitResult -> onBlockClick(player, hitResult.toNobaVec()) }
	}

	private fun onParticle(event: ParticleEvents.Particle) {
		if(!enabled) return

		val particleType = BurrowParticleType.getParticleType(event) ?: return

		val location = event.location.roundToBlock().lower()
		if(location in recentlyDugBurrows) return

		val burrow = burrows.getOrPut(location) { Burrow(location) }

		when(particleType) {
			BurrowParticleType.ENCHANT -> burrow.hasEnchant = true
			BurrowParticleType.START -> burrow.type = BurrowType.START
			BurrowParticleType.MOB -> burrow.type = BurrowType.MOB
			BurrowParticleType.TREASURE -> burrow.type = BurrowType.TREASURE
		}

		if(!burrow.hasEnchant || burrow.type == BurrowType.UNKNOWN || burrow.found) return

		MythologicalEvents.BURROW_FIND.invoke(MythologicalEvents.BurrowFind(burrow.location, burrow.type))
		burrow.found = true
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return

		when {
			burrowDugPattern.matches(message) -> {
				lastBurrowChatMessage = Timestamp.now()

				if(lastDugBurrow == null || !tryDigBurrow(lastDugBurrow!!)) return
				fakeBurrow = lastDugBurrow
			}
			mobDugPattern.matches(message) -> mobBurrow = lastDugBurrow
			message == "Defeat all the burrow defenders in order to dig it!" -> lastBurrowChatMessage = Timestamp.now()
		}
	}

	@Suppress("SameReturnValue")
	private fun onBlockClick(player: PlayerEntity, location: NobaVec): ActionResult {
		if(!enabled) return ActionResult.PASS
		if(!DianaAPI.hasSpadeInHand(player)) return ActionResult.PASS
		if(location.getBlockAt() != Blocks.GRASS_BLOCK) return ActionResult.PASS

		if(location == fakeBurrow) {
			fakeBurrow = null
			tryDigBurrow(location, ignoreFound = true)
			return ActionResult.PASS
		}

		if(!burrows.containsKey(location)) return ActionResult.PASS

		lastDugBurrow = location
		Scheduler.schedule(20) {
			if(lastBurrowChatMessage.elapsedSince() > 2.seconds) burrows.remove(location)
		}

		return ActionResult.PASS
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
				for(type in entries) {
					if(type.check(event)) return type
				}
				return null
			}
		}
	}

	class Burrow(
		var location: NobaVec,
		var hasEnchant: Boolean = false,
		var type: BurrowType = BurrowType.UNKNOWN,
		var found: Boolean = false
	)
}