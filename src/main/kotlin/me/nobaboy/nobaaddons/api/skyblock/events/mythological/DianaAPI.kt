package me.nobaboy.nobaaddons.api.skyblock.events.mythological

import me.nobaboy.nobaaddons.api.skyblock.MayorAPI.isActive
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.mayor.MayorPerk
import me.nobaboy.nobaaddons.utils.InventoryUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyBlockId
import net.minecraft.entity.player.PlayerEntity

object DianaAPI {
	const val SPADE = "ANCESTRAL_SPADE"

	val isActive: Boolean get() = SkyBlockIsland.HUB.inIsland() && isRitualActive && hasSpadeInHotbar
	private val isRitualActive: Boolean get() = MayorPerk.MYTHOLOGICAL_RITUAL.isActive()

	private val hasSpadeInHotbar: Boolean get() = InventoryUtils.getHotbarItems().any { it.skyBlockId == SPADE }
	fun hasSpadeInHand(player: PlayerEntity): Boolean = player.mainHandStack.skyBlockId == SPADE
}