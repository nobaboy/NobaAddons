package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.withColor
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity

object CorpseLocator {
	private val config get() = NobaConfigManager.config.mining.glaciteMineshaft
	private val enabled: Boolean get() = config.corpseLocator && SkyBlockIsland.MINESHAFT.inIsland()

	private val chatCoordsPattern by Regex(
		"(?<username>[A-z0-9_]+): [Xx]: (?<x>[0-9.-]+),? [Yy]: (?<y>[0-9.-]+),? [Zz]: (?<z>[0-9.-]+)(?<info>.*)"
	).fromRepo("chat_coordinates")

	private val corpses = mutableListOf<Corpse>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { corpses.clear() }
		SecondPassedEvent.EVENT.register(this::onSecondPassed)
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onSecondPassed(event: SecondPassedEvent) {
		if(!enabled) return

		event.client.player?.let {
			getCorpses(it)
			shareCorpse(it)
		}
	}

	private fun onChatMessage(message: String) {
		if(!enabled) return

		chatCoordsPattern.onFullMatch(message) {
			val username = groups["username"]!!.value
			if(username == MCUtils.playerName) return

			val vec = NobaVec(
				groups["x"]!!.value.toInt(),
				groups["y"]!!.value.toInt(),
				groups["z"]!!.value.toInt()
			)

			corpses.firstOrNull { it.entity.getNobaVec().distance(vec) <= 5 }?.shared = true
		}
	}

	private fun getCorpses(player: PlayerEntity) {
		EntityUtils.getEntities<ArmorStandEntity>()
			.filter { entity -> corpses.none { it.entity == entity } }
			.forEach { checkCorpse(it) }

		corpses.filter { !it.seen && player.canSee(it.entity) }.forEach { corpse ->
			val (x, y, z) = corpse.entity.getNobaVec().toDoubleArray().map { it.toInt() }

			val type = corpse.type.toString().toText().withColor(corpse.type.color)
			val text = tr("nobaaddons.corpseLocator.found", "Found $type Corpse at $x, $y, $z")

			ChatUtils.addMessage(text)
			MineshaftWaypoints.waypoints.add(Waypoint(
				corpse.entity.getNobaVec().roundToBlock(),
				"${corpse.type} Corpse",
				corpse.type.color,
				true
			))

			corpse.seen = true
		}
	}

	private fun checkCorpse(entity: ArmorStandEntity) {
		if(!enabled) return
		if(entity.isInvisible) return
		if(!entity.shouldShowArms()) return
		//? if >=1.21.2 {
		if(entity.shouldShowBasePlate()) return
		//?} else {
		/*if(!entity.shouldHideBasePlate()) return
		*///?}

		val item = entity.getEquippedStack(EquipmentSlot.HEAD).getSkyBlockItem() ?: return
		val corpseType = CorpseType.getByHelmetOrNull(item.id) ?: return

		corpses.add(Corpse(entity, corpseType))
	}

	private fun shareCorpse(player: PlayerEntity) {
		if(!config.autoShareCorpseCoords) return
		if(PartyAPI.party == null) return
		if(MineshaftWaypoints.waypoints.isEmpty()) return

		val location = player.getNobaVec()
		val closestCorpse = corpses
			.filter { !it.shared && it.entity.getNobaVec().distance(location) <= 5 }
			.minByOrNull { it.entity.getNobaVec().distance(location) } ?: return

		val (x, y, z) = closestCorpse.entity.getNobaVec().toDoubleArray().map { it.toInt() }

		HypixelCommands.partyChat("x: $x, y: $y, z: $z | (${closestCorpse.type} Corpse)")
		closestCorpse.shared = true
	}

	data class Corpse(val entity: ArmorStandEntity, val type: CorpseType, var seen: Boolean = false, var shared: Boolean = false)
}
