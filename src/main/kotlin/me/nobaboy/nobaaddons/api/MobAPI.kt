package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.events.PacketEvents
import me.nobaboy.nobaaddons.utils.EntityUtils.isRealPlayer
import me.nobaboy.nobaaddons.utils.TimedSet
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.EntityType
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
import kotlin.time.Duration.Companion.seconds

object MobAPI {
	private val packetPlayerEntityIds = TimedSet<Int>(3.seconds)
//	private val playerEntities = TimedSet<PlayerEntity>(1.seconds)

	private val packetEntityIds = TimedSet<Int>(3.seconds)

	fun init() {
		PacketEvents.RECEIVE.register(this::onPacketReceive)
		ClientTickEvents.END_CLIENT_TICK.register(this::onTick)
	}

	private fun onPacketReceive(event: PacketEvents.Receive) {
		if(!SkyBlockAPI.inSkyBlock) return

		val packet = event.packet

		when(packet) {
			is EntitySpawnS2CPacket -> onEntitySpawnPacket(packet)
			is EntityTrackerUpdateS2CPacket -> onEntityUpdatePacket(packet)
		}
	}

	private fun onTick(client: MinecraftClient) {
		packetPlayerEntityIds.forEach { entityId ->
			val entity = client.player?.entityWorld?.getEntityById(entityId) ?: return@forEach
			if(entity !is PlayerEntity) return@forEach
			if(entity.isRealPlayer()) return@forEach
//			if(entity in playerEntities) return

			// get mob type based on whatever enums I have, sea creatures, slayers (and minibosses), etc., any other
			// mob will just be tagged as BASIC, or might make a PLAYER type, for mobs that receive an update packet
			// just cache the spawn packet entity id, if we receive an entity update packet (within 3 seconds) and an
			// id matches, make a skyblock mob class out of that entity and call its event

//			val type =
//			val mob = SkyBlockMob(entity, type)
//			MobEvents.Spawn.PLAYER.invoke(MobEvents.spawn())
		}
	}

	private fun onEntitySpawnPacket(packet: EntitySpawnS2CPacket) {
		when(packet.entityType) {
			EntityType.PLAYER -> packetPlayerEntityIds.add(packet.entityId)
			else -> packetEntityIds.add(packet.entityId)
		}
	}

	private fun onEntityUpdatePacket(packet: EntityTrackerUpdateS2CPacket) {
		if(packet.id !in packetEntityIds) return
	}
}