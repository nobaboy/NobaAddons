package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.events.skyblock.SecondPassedEvent
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockIslandChangeEvent
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.findMatcher
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity
import java.util.regex.Pattern

object CorpseLocator {
	private val config get() = NobaConfigManager.config.mining.glaciteMineshaft

	private val chatCoordsPattern = Pattern.compile(
		"(?i)(?<username>[A-z0-9_]+): x: (?<x>[0-9.-]+),? y: (?<y>[0-9.-]+),? z: (?<z>[0-9.-]+)(?<info>.*)"
	)

	private val corpses = mutableListOf<Corpse>()

	fun init() {
		SkyBlockIslandChangeEvent.EVENT.register { corpses.clear() }
		SecondPassedEvent.EVENT.register(this::onSecondPassed)
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onSecondPassed(event: SecondPassedEvent) {
		val client = event.client
		if(!isEnabled() || client.player == null) return

		getCorpses(client.player!!)
		shareCorpse(client.player!!)
	}

	private fun onChatMessage(message: String) {
		if(!isEnabled()) return

		chatCoordsPattern.findMatcher(message) {
			val username = group("username")
			if(username == MCUtils.playerName) return

			val vec = NobaVec(
				group("x").toInt(),
				group("y").toInt(),
				group("z").toInt()
			)

			corpses.find { it.entity.getNobaVec().distance(vec) <= 5 }?.shared = true
		}
	}

	private fun getCorpses(player: PlayerEntity) {
		EntityUtils.getEntities<ArmorStandEntity>()
			.filter { entity -> corpses.none { it.entity == entity } }
			.forEach { checkCorpse(it) }

		corpses.forEach { corpse ->
			if(!corpse.seen && player.canSee(corpse.entity)) {
				val article = if(corpse.type == CorpseType.UMBER) "an" else "a"
				val corpseText = "${corpse.type.displayName} Corpse"
				ChatUtils.addMessage("Located $article $corpseText and marked its location with a waypoint.")

				MineshaftWaypoints.waypoints.add(Waypoint(corpse.entity.getNobaVec(), corpseText, corpse.type.color, isCorpse = true))
				corpse.seen = true
			}
		}
	}

	private fun checkCorpse(entity: ArmorStandEntity) {
		if(!isEnabled()) return
		if(entity.isInvisible) return
		if(!entity.shouldShowArms()) return
		//? if >=1.21.2 {
		if(entity.shouldShowBasePlate()) return
		//?} else {
		/*if(!entity.shouldHideBasePlate()) return*/
		//?}

		val item = entity.getEquippedStack(EquipmentSlot.HEAD).getSkyBlockItem() ?: return
		val corpseType = CorpseType.getByHelmetOrNull(item.id) ?: return

		corpses.add(Corpse(entity, corpseType))
	}

	private fun shareCorpse(player: PlayerEntity) {
		if(!config.autoShareCorpseCoords) return
		if(PartyAPI.party == null) return
		if(MineshaftWaypoints.waypoints.isEmpty()) return

		val vec = player.getNobaVec()
		val closestCorpse = corpses
			.filter { !it.shared && it.entity.getNobaVec().distance(vec) <= 5 }
			.minByOrNull { it.entity.getNobaVec().distance(vec) } ?: return

		val (x, y, z) = closestCorpse.entity.getNobaVec().toDoubleArray().map { it.toInt() }

		HypixelCommands.partyChat("x: $x, y: $y, z: $z | (${closestCorpse.type.displayName} Corpse)")
		closestCorpse.shared = true
	}

	data class Corpse(val entity: ArmorStandEntity, val type: CorpseType, var seen: Boolean = false, var shared: Boolean = false)

	private fun isEnabled() = IslandType.MINESHAFT.inIsland() && config.corpseLocator
}