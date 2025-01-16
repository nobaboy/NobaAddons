package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity

object CorpseLocator {
	private val config get() = NobaConfig.INSTANCE.mining.glaciteMineshaft
	private val enabled: Boolean get() = config.corpseLocator && SkyBlockIsland.MINESHAFT.inIsland()

	private val chatCoordsPattern by Regex(
		"(?<username>[A-z0-9_]+): [Xx]: (?<x>[0-9.-]+),? [Yy]: (?<y>[0-9.-]+),? [Zz]: (?<z>[0-9.-]+)(?<info>.*)"
	).fromRepo("chat_coordinates")

	private val corpses = mutableListOf<Corpse>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { corpses.clear() }
		TickEvents.everySecond(this::onSecondPassed)
		ChatMessageEvents.CHAT.register { (message) -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onSecondPassed(event: TickEvents.Tick) {
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
			val location = corpse.entity.getNobaVec().roundToBlock()
			val (x, y, z) = location.toDoubleArray().map { it.toInt() }

			val text = tr("nobaaddons.mining.corpseLocator.found", "Found ${corpse.type.formattedDisplayName} at $x, $y, $z!")

			ChatUtils.addMessage(text)
			MineshaftWaypoints.addWaypoint(location, corpse.type.displayName, corpse.type.color, MineshaftWaypointType.CORPSE)

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
		if(!config.autoShareCorpses) return
		if(PartyAPI.party == null) return
		if(MineshaftWaypoints.waypoints.isEmpty()) return

		val location = player.getNobaVec()
		val closestCorpse = corpses
			.filter { !it.shared && it.entity.getNobaVec().distance(location) <= 5 }
			.minByOrNull { it.entity.getNobaVec().distance(location) } ?: return

		val (x, y, z) = closestCorpse.entity.getNobaVec().toDoubleArray().map { it.toInt() }

		HypixelCommands.partyChat("x: $x, y: $y, z: $z | (${closestCorpse.type.displayName.string})")
		closestCorpse.shared = true
	}

	data class Corpse(val entity: ArmorStandEntity, val type: CorpseType, var seen: Boolean = false, var shared: Boolean = false)
}
