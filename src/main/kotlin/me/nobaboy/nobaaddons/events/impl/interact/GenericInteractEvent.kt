package me.nobaboy.nobaaddons.events.impl.interact

import me.nobaboy.nobaaddons.events.Event
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Hand

/**
 * Generic interact event, invoked when the client player performs any kind of interaction with the world.
 *
 * @see ItemUseEvent
 * @see BlockInteractionEvent
 */
sealed interface GenericInteractEvent : Event {
	val player: ClientPlayerEntity
	val hand: Hand
	val itemInHand: ItemStack get() = player.getStackInHand(hand)
}