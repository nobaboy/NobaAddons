package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.events.skyblock.SlayerEvents
import me.nobaboy.nobaaddons.utils.ScoreboardUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.Timestamp
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

object SlayerAPI {
	var questActive: Boolean = false

	fun init() {
		SecondPassedEvent.EVENT.register { onSecondPassed() }
//		ClientEntityEvents.ENTITY_LOAD.register { entity, _ -> onEntityLoad(entity) }
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onSecondPassed() {
		if(!SkyBlockAPI.inSkyBlock) return

		val lines = ScoreboardUtils.getSidebarLines()
		questActive = lines.any { it == "Slayer Quest" }
	}

//	private fun onEntityLoad(entity: Entity) {
//		if(!SkyBlockAPI.inSkyBlock) return
//		if(!questActive) return
//
//		val name = entity.name.string
//		val mob = SlayerMiniBoss.getByName(name) ?: SlayerBoss.getByName(name) ?: return
//
//		when(mob) {
//			is SlayerMiniBoss -> SlayerEvents.MINIBOSS_SPAWN.invoke(SlayerEvents.MiniBossSpawn(entity))
//			is SlayerBoss -> SlayerEvents.BOSS_SPAWN.invoke(SlayerEvents.BossSpawn(entity, mob, Timestamp.now()))
//		}
//	}

	private fun onChatMessage(message: String) {
		if(!SkyBlockAPI.inSkyBlock) return
		if(!questActive) return

		if(message == "SLAYER QUEST COMPLETE") SlayerEvents.BOSS_KILL.invoke(SlayerEvents.BossKill(Timestamp.now()))
	}
}