package me.nobaboy.nobaaddons.api.skyblock.events.mythological

import me.nobaboy.nobaaddons.api.skyblock.MayorAPI.isActive
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.mayor.MayorPerk
import me.nobaboy.nobaaddons.events.impl.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyBlockId
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientEntityEvents
import net.minecraft.client.network.OtherClientPlayerEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity

object DianaAPI {
	private const val SPADE = "ANCESTRAL_SPADE"

	val isActive: Boolean get() = SkyBlockIsland.HUB.inIsland() && isRitualActive() && hasSpadeInHotbar()

	fun init() {
		ClientEntityEvents.ENTITY_LOAD.register { entity, _ -> onEntityLoad(entity) }
	}

	private fun onEntityLoad(entity: Entity) {
		if(entity !is OtherClientPlayerEntity) return
		if(entity.name.string != "Minos Inquisitor") return

		MythologicalEvents.INQUISITOR_SPAWN.invoke(MythologicalEvents.InquisitorSpawn(entity))
	}

	private fun hasSpadeInHotbar(): Boolean = InventoryUtils.getItemsInHotbar().any { it.skyBlockId == SPADE }

	fun hasSpadeInHand(player: PlayerEntity): Boolean {
		val heldItem = player.mainHandStack?.asSkyBlockItem ?: return false
		return heldItem.id == SPADE
	}

	private fun isRitualActive() = MayorPerk.MYTHOLOGICAL_RITUAL.isActive()
}