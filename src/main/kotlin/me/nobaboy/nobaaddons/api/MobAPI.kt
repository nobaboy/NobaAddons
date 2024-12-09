package me.nobaboy.nobaaddons.api

import me.nobaboy.nobaaddons.events.PacketEvents
import me.nobaboy.nobaaddons.utils.EntityUtils
import me.nobaboy.nobaaddons.utils.TimedSet
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.minecraft.client.MinecraftClient
import net.minecraft.entity.LivingEntity
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket
import java.util.concurrent.ConcurrentLinkedQueue
import kotlin.time.Duration.Companion.seconds

object MobAPI {
	private val spawnPacketEntityIds = TimedSet<Int>(3.seconds)
	private val updatePacketEntityIds = ConcurrentLinkedQueue<Int>()

	fun init() {
//		SkyBlockEvents.ISLAND_CHANGE.register { reset() }
		PacketEvents.RECEIVE.register(this::onPacketReceive)
		ClientTickEvents.END_CLIENT_TICK.register(this::onTick)
	}

	private fun onPacketReceive(event: PacketEvents.Receive) {
		if(!SkyBlockAPI.inSkyBlock) return

		when(val packet = event.packet) {
			is EntitySpawnS2CPacket -> addEntityId(packet.entityId)
			is EntityTrackerUpdateS2CPacket -> addEntityId(packet.id)
		}
	}

	private fun onTick(client: MinecraftClient) {
		if(!SkyBlockAPI.inSkyBlock) return

		checkEntityUpdates()
	}

	private fun addEntityId(entityId: Int) {
		if(entityId in spawnPacketEntityIds) {
			updatePacketEntityIds.add(entityId)
		} else {
			spawnPacketEntityIds.add(entityId)
		}
	}

	private fun checkEntityUpdates() {
		val processedEntities = updatePacketEntityIds.filter { entityId ->
			val entity = EntityUtils.getEntityById(entityId) as? LivingEntity ?: return@filter false

			// get entity type then call its corresponding spawn event, or add it to a list of entities and use that
			// to call events

			true
		}

		processedEntities.forEach { updatePacketEntityIds.remove(it) }
	}
}