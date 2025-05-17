package me.nobaboy.nobaaddons.events.impl.interact

import net.minecraft.util.ActionResult

import me.nobaboy.nobaaddons.events.EventDispatcher
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.Hand

/**
 * Event invoked when the client player right clicks with an item while not looking at a block; you may
 * also want to pair this with a [BlockInteractionEvent.EVENT] listener to handle when the player
 * is looking at a block.
 */
data class ItemUseEvent(
	override val player: ClientPlayerEntity,
	override val hand: Hand,
) : GenericInteractEvent {
	companion object {
		init {
			UseItemCallback.EVENT.register { player, _, hand ->
				if(player is ClientPlayerEntity) {
					EVENT.dispatch(ItemUseEvent(player, hand))
				}
				ActionResult.PASS
			}
		}

		/**
		 * This event is a wrapper around the Fabric API [UseItemCallback] event.
		 *
		 * @see ItemUseEvent
		 */
		val EVENT = EventDispatcher<ItemUseEvent>()
	}
}