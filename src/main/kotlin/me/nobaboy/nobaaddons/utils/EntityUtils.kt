package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.LocationUtils.distanceTo
import net.minecraft.entity.Entity

object EntityUtils {
	fun getAllEntities(): Sequence<Entity> {
		val client = MCUtils.client
		return client.world?.entities?.let {
			if(client.isOnThread) it else it.toMutableList()
		}?.asSequence()?.filterNotNull() ?: emptySequence()
	}

	inline fun <reified T : Entity> getEntities(): Sequence<T> = getAllEntities().filterIsInstance<T>()

	fun getEntityById(entityId: Int) = MCUtils.player?.entityWorld?.getEntityById(entityId)

	inline fun <reified T : Entity> getEntitiesNear(location: NobaVec, radius: Double): Sequence<T> =
		getEntities<T>().filter { it.distanceTo(location) < radius }

	fun getNextEntity(entity: Entity, offset: Int) = getEntityById(entity.id + offset)
}