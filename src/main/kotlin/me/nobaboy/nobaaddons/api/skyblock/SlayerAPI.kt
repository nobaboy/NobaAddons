package me.nobaboy.nobaaddons.api.skyblock

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
//		PacketEvents.RECEIVE.register(this::onPacketReceive)
		ClientReceiveMessageEvents.GAME.register { message, _ -> onChatMessage(message.string.cleanFormatting()) }
	}

	private fun onSecondPassed() {
		if(!SkyBlockAPI.inSkyBlock) return

		val lines = ScoreboardUtils.getSidebarLines()
		questActive = lines.any { it == "Slayer Quest" }
	}

//	private fun onPacketReceive(event: PacketEvents.Receive) {
//		if(!SkyBlockAPI.inSkyBlock) return
//		if(!questActive) return
//
//		val packet = event.packet
//		if(packet !is EntityTrackerUpdateS2CPacket) return
//
//		val entity = EntityUtils.getEntityByID(packet.id) ?: return
//		if(entity !is ArmorStandEntity) return
//
//		val metadata = packet.trackedValues.associate { it.id to it.value }
//		val entry = metadata[2] ?: return
//
//		val optionalValue = entry as? Optional<*> ?: return
//		if(!optionalValue.isPresent) return
//
//		val name = (optionalValue.get() as? Text)?.string ?: return
//
//		val mobType = SlayerMiniBoss.getByName(name) ?: SlayerBoss.getByName(name) ?: return
//		val mob = EntityUtils.getClosestEntity(entity) ?: return
//
//		when(mobType) {
//			is SlayerMiniBoss -> SlayerEvents.MINIBOSS_SPAWN.invoke(SlayerEvents.MiniBossSpawn(mob))
//			is SlayerBoss -> SlayerEvents.BOSS_SPAWN.invoke(SlayerEvents.BossSpawn(mob, mobType, Timestamp.now()))
//		}
//	}

	private fun onChatMessage(message: String) {
		if(!SkyBlockAPI.inSkyBlock) return
		if(!questActive) return

		if(message == "  SLAYER QUEST COMPLETE") SlayerEvents.BOSS_KILL.invoke(SlayerEvents.BossKill(Timestamp.now()))
	}
}