package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.events.PacketEvents
import me.nobaboy.nobaaddons.utils.EntityUtils.isRealPlayer
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TimedSet
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
import kotlin.time.Duration.Companion.seconds

object MobAPI {
	private val packetEntityIds = TimedSet<Int>(3.seconds)

	fun init() {
		PacketEvents.RECEIVE.register(this::onPacketReceive)
	}

	private fun onPacketReceive(event: PacketEvents.Receive) {
		if(!SkyBlockAPI.inSkyBlock) return

		val packet = event.packet

		when(packet) {
			is EntitySpawnS2CPacket -> onEntitySpawnPacket(packet)
			is EntityTrackerUpdateS2CPacket -> onEntityUpdatePacket(packet)
		}
	}

	private fun onEntitySpawnPacket(packet: EntitySpawnS2CPacket) {
		when(packet.entityType) {
			EntityType.PLAYER -> {
				checkPlayerEntity(packet.entityId)
				println(packet.uuid)
			}
			else -> packetEntityIds.add(packet.entityId)
		}
	}

	private fun onEntityUpdatePacket(packet: EntityTrackerUpdateS2CPacket) {
		if(packet.id !in packetEntityIds) return
	}

	private fun checkPlayerEntity(entityId: Int) {
		val entity = MCUtils.player?.entityWorld?.getEntityById(entityId) ?: return
		if(entity !is PlayerEntity) return
		if(entity.isRealPlayer()) return

//		val type =
//		val mob = SkyBlockMob(entity, type)

//		MobEvents.Spawn.PLAYER.invoke(MobEvents.Spawn(mob))
	}
}