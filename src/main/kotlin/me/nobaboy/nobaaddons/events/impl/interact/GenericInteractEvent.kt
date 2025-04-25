package me.nobaboy.nobaaddons.events.impl.interact

import me.nobaboy.nobaaddons.events.Event
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand

sealed interface GenericInteractEvent : Event {
	val player: ClientPlayerEntity
	val hand: Hand
	val itemInHand: ItemStack get() = player.getStackInHand(hand)
}