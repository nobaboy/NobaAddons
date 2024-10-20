package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.LocationUtils.canBeSeen
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceTo
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToIgnoreY
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkullTexture
import net.minecraft.entity.Entity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity

object EntityUtils {
	fun PlayerEntity.isRealPlayer() = uuid?.let { it.version() == 4 } ?: false

	fun Entity.canBeSeen(radius: Double = 150.0) = getNobaVec().add(y = 0.5).canBeSeen()

	inline fun <reified R : Entity> getEntities(): Sequence<R> = getAllEntities().filterIsInstance<R>()

	fun getAllEntities(): Sequence<Entity> {
		val client = MCUtils.client
		return client.world?.entities?.let {
			if(client.isOnThread) it else it.toMutableList()
		}?.asSequence()?.filterNotNull() ?: emptySequence()
	}

	fun getEntityByID(entityId: Int) = MCUtils.player?.entityWorld?.getEntityById(entityId)

	inline fun <reified T : Entity> getEntitiesNear(location: NobaVec, radius: Double): Sequence<T> =
		getEntities<T>().filter { it.distanceTo(location) < radius }

	inline fun <reified T : Entity> getEntitiesNearIgnoreY(location: NobaVec, radius: Double): Sequence<T> =
		getEntities<T>().filter { it.distanceToIgnoreY(location) < radius }

	inline fun <reified T : Entity> getEntitiesNearPlayer(radius: Double): Sequence<T> =
		getEntitiesNear<T>(LocationUtils.playerLocation(), radius)

	fun ArmorStandEntity.hasSkullTexture(texture: String): Boolean {
		val armor = this.armorItems ?: return false
		return armor.any { it != null && it.getSkullTexture() == texture }
	}
}