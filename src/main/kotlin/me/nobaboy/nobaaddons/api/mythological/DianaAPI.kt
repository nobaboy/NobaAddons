package me.nobaboy.nobaaddons.api.mythological

import me.nobaboy.nobaaddons.api.MayorAPI
import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.api.data.IslandType
import me.nobaboy.nobaaddons.core.MayorPerk
import me.nobaboy.nobaaddons.events.skyblock.MythologicalEvents
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItemId
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.server.network.ServerPlayerEntity

object DianaAPI {
	private val spade = "ANCESTRAL_SPADE"

	fun init() {
		ServerEntityEvents.ENTITY_LOAD.register { entity, _ -> onEntityLoad(entity) }
	}

	private fun onEntityLoad(entity: Entity) {
		if(entity !is ServerPlayerEntity) return
		if(entity.name.string != "Minos Inquisitor") return

		MythologicalEvents.INQUISITOR_SPAWN.invoke(MythologicalEvents.InquisitorSpawn(entity))
	}

	private fun hasSpadeInHotbar(): Boolean = InventoryUtils.getItemsInHotbar().any { it.getSkyBlockItemId() == spade }

	fun hasSpadeInHand(player: PlayerEntity): Boolean {
		val heldItem = player.mainHandStack?.getSkyBlockItem() ?: return false
		return heldItem.id == spade
	}

	private fun isRitualActive() = MayorAPI.currentMayor.activePerks.contains(MayorPerk.MYTHOLOGICAL_RITUAL) ||
		MayorAPI.currentMinister.activePerks.contains(MayorPerk.MYTHOLOGICAL_RITUAL)

	fun isActive(): Boolean = IslandType.HUB.inIsland() && isRitualActive() && hasSpadeInHotbar()
}