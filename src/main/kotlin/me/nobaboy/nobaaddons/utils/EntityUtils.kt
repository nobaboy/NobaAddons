package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.LocationUtils.distanceTo
import net.minecraft.entity.Entity

object EntityUtils {
	fun getEntityById(entityId: Int): Entity? = MCUtils.player?.entityWorld?.getEntityById(entityId)

	fun getAllEntities(): Sequence<Entity> {
		val client = MCUtils.client
		val entities = client.world?.entities ?: return emptySequence()
		return entities.let { if(client.isOnThread) it else it.toList() }.asSequence()
	}

	inline fun <reified T : Entity> getEntities(): Sequence<T> =
		getAllEntities().filterIsInstance<T>()

	inline fun <reified T : Entity> getEntitiesNear(location: NobaVec, radius: Double): Sequence<T> =
		getEntities<T>().filter { it.distanceTo(location) < radius }

	inline fun <reified T : Entity> getNextEntity(entity: Entity, offset: Int): T? =
		getEntityById(entity.id + offset) as? T
}