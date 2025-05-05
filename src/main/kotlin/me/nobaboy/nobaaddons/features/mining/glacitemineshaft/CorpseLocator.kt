package me.nobaboy.nobaaddons.features.mining.glacitemineshaft

import me.nobaboy.nobaaddons.api.PartyAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.mc.EntityUtils
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.RegexUtils.onPartialMatch
import me.nobaboy.nobaaddons.utils.mc.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.mc.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.hypixel.HypixelCommands
import me.nobaboy.nobaaddons.utils.getNobaVec
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity

object CorpseLocator {
	private val config get() = NobaConfig.mining.glaciteMineshaft
	private val enabled: Boolean get() = config.corpseLocator && SkyBlockIsland.MINESHAFT.inIsland()

	private val corpses = mutableListOf<Corpse>()

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { corpses.clear() }
		TickEvents.everySecond(this::onSecondPassed)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
	}

	private fun onSecondPassed(event: TickEvents.Tick) {
		if(!enabled) return

		event.client.player?.let {
			getCorpses(it)
			shareCorpse(it)
		}
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!enabled) return

		CommonPatterns.CHAT_COORDINATES_REGEX.onPartialMatch(event.cleaned) {
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

			val article = if(corpse.type == CorpseType.UMBER) "an" else "a"
			val message = buildText {
				append("Found $article ")
				append(corpse.type.formattedName)
				append(" at $x, $y, $z!")
			}

			ChatUtils.addMessage(message)
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

		val item = entity.getEquippedStack(EquipmentSlot.HEAD).asSkyBlockItem ?: return
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

	data class Corpse(
		val entity: ArmorStandEntity,
		val type: CorpseType,
		var seen: Boolean = false,
		var shared: Boolean = false,
	)
}
