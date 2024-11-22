package me.nobaboy.nobaaddons.api.mythological

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.ParticleEvents
import me.nobaboy.nobaaddons.events.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockIslandChangeEvent
import me.nobaboy.nobaaddons.features.events.mythological.BurrowType
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.matches
import me.nobaboy.nobaaddons.utils.Scheduler
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.particle.ParticleTypes
import net.minecraft.util.ActionResult
import java.util.regex.Pattern
import kotlin.time.Duration.Companion.seconds

object BurrowAPI {
	private val config get() = NobaConfigManager.config.events.mythological

	private val burrowDugPattern = Pattern.compile("^(You dug out a Griffin Burrow!|You finished the Griffin burrow chain!) \\(\\d/4\\)")
	private val mobDugPattern = Pattern.compile("^.* You dug out a [A-z ]+!")

	private val burrows = mutableMapOf<NobaVec, Burrow>()
	private val recentlyDugBurrows = mutableListOf<NobaVec>()
	private var lastDugBurrow: NobaVec? = null

	// This is here in case the player digs the burrow before the particles spawn
	private var fakeBurrow: NobaVec? = null
	var mobBurrow: NobaVec? = null

	private var lastBurrowChatMessage = Timestamp.distantPast()

	fun init() {
		SkyBlockIslandChangeEvent.EVENT.register { reset() }
		ParticleEvents.PARTICLE.register(this::onParticle)
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
		AttackBlockCallback.EVENT.register { player, _, _, pos, _ -> onBlockClick(player, pos.toNobaVec()) }
		UseBlockCallback.EVENT.register { player, _, _, hitResult -> onBlockClick(player, hitResult.toNobaVec()) }
	}

	private fun onParticle(event: ParticleEvents.Particle) {
		if(!isEnabled()) return

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
		if(!isEnabled()) return

		if(burrowDugPattern.matches(message)) {
			lastBurrowChatMessage = Timestamp.now()
			if(lastDugBurrow != null && !digBurrow(lastDugBurrow!!)) {
				fakeBurrow = lastDugBurrow
			}
		}

		if(mobDugPattern.matches(message)) mobBurrow = lastDugBurrow

		if(message == "Defeat all the burrow defenders in order to dig it!") lastBurrowChatMessage = Timestamp.now()
	}

	@Suppress("SameReturnValue")
	private fun onBlockClick(player: PlayerEntity, location: NobaVec): ActionResult {
		if(!isEnabled()) return ActionResult.PASS
		if(!DianaAPI.hasSpadeInHand(player)) return ActionResult.PASS

		if(location == fakeBurrow) {
			fakeBurrow = null
			digBurrow(location)
			return ActionResult.PASS
		}

		if(!burrows.containsKey(location)) return ActionResult.PASS

		lastDugBurrow = location
		Scheduler.schedule(20) {
			if(lastBurrowChatMessage.elapsedSince() > 2.seconds) burrows.remove(location)
		}

		return ActionResult.PASS
	}

	private fun digBurrow(location: NobaVec): Boolean {
		val burrow = burrows[location] ?: return false
		if(!burrow.found) return false

		burrows.remove(location)
		recentlyDugBurrows.add(location)
		lastDugBurrow = null

		MythologicalEvents.BURROW_DIG.invoke(MythologicalEvents.BurrowDig(location))
		return true
	}

	private fun reset() {
		burrows.clear()
		recentlyDugBurrows.clear()
	}

	private enum class BurrowParticleType(val check: ParticleEvents.Particle.() -> Boolean) {
		ENCHANT({ type == ParticleTypes.ENCHANT && count == 5 && speed == 0.05f && offset.x == 0.5 && offset.y == 0.4 && offset.z == 0.5 }),
		START({ type == ParticleTypes.ENCHANTED_HIT && count == 4 && speed == 0.01f && offset.x == 0.5 && offset.y == 0.1 && offset.z == 0.5 }),
		MOB({ type == ParticleTypes.CRIT && count == 3 && speed == 0.01f && offset.x == 0.5 && offset.y == 0.1 && offset.z == 0.5 }),
		TREASURE({ type == ParticleTypes.DRIPPING_LAVA && count == 2 && speed == 0.01f && offset.x == 0.35 && offset.y == 0.1 && offset.z == 0.35 });

		companion object {
			fun getParticleType(event: ParticleEvents.Particle): BurrowParticleType? {
				if(!event.isLongDistance) return null
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

	private fun isEnabled() = DianaAPI.isActive() && config.findNearbyBurrows
}