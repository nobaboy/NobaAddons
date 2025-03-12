package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.utils.LocationUtils.canBeSeen
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceTo
import me.nobaboy.nobaaddons.utils.LocationUtils.distanceToIgnoreY
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkullTexture
import net.minecraft.entity.Entity
import net.minecraft.entity.EquipmentSlot
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.decoration.ArmorStandEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack

object EntityUtils {
	fun PlayerEntity.isRealPlayer() = uuid?.let { it.version() == 4 } == true

	fun Entity.canBeSeen(radius: Double = 150.0) = getNobaVec().add(y = 0.5).canBeSeen()

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

	inline fun <reified T : Entity> getEntitiesNearIgnoreY(location: NobaVec, radius: Double): Sequence<T> =
		getEntities<T>().filter { it.distanceToIgnoreY(location) < radius }

	inline fun <reified T : Entity> getEntitiesNearPlayer(radius: Double): Sequence<T> =
		getEntitiesNear<T>(LocationUtils.playerLocation, radius)

	inline fun <reified T : Entity> getClosestEntity(location: NobaVec): T? =
		getEntities<T>().minBy { it.distanceTo(location) }

	inline fun <reified T : Entity> getClosestEntity(location: NobaVec, radius: Double): T? =
		getEntitiesNear<T>(location, radius).minBy { it.distanceTo(location) }

	fun getNextEntity(entity: Entity, offset: Int) = getEntityById(entity.id + offset)

	val LivingEntity.equipmentSlots: Map<EquipmentSlot, ItemStack>
		get() = buildMap {
			EquipmentSlot.entries.forEach { put(it, getEquippedStack(it)) }
		}

	fun ArmorStandEntity.armorSkullTexture(texture: String): Boolean {
		val armor = /*? if >=1.21.5-pre2 {*//*equipmentSlots.values*//*?} else {*/armorItems ?: return false/*?}*/
		return armor.any {
			/*? if >=1.21.5-pre2 {*//*@Suppress("SENSELESS_COMPARISON")*//*?}*/
			it != null && it.getSkullTexture() == texture
		}
	}

	fun ArmorStandEntity.heldSkullTexture(texture: String): Boolean {
		val heldItem = this.mainHandStack ?: return false
		return heldItem.getSkullTexture() == texture
	}
}