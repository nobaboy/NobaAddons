package me.nobaboy.nobaaddons.events.impl.interact

import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.Direction

sealed interface BlockInteractionEvent : GenericInteractEvent {
	val location: NobaVec

	companion object {
		init {
			AttackBlockCallback.EVENT.register { player, _, hand, block, direction ->
				if(player is ClientPlayerEntity) {
					EVENT.dispatch(Attack(block.toNobaVec(), player, hand, direction))
				}
				ActionResult.PASS
			}
			UseBlockCallback.EVENT.register { player, _, hand, hitResult ->
				if(player is ClientPlayerEntity) {
					EVENT.dispatch(Interact(hitResult, player, hand))
				}
				ActionResult.PASS
			}
		}

		/**
		 * Event invoked whenever the client player interacts with a block (be it a left or right click)
		 *
		 * This event is a wrapper around the Fabric API [AttackBlockCallback] and [UseBlockCallback] events.
		 *
		 * @see Attack
		 * @see Interact
		 * @see EventDispatcher.registerIf
		 */
		val EVENT = EventDispatcher<BlockInteractionEvent>()
	}

	/**
	 * Event invoked when the player "attacks" - or left clicks - a block in the world
	 *
	 * @see EVENT
	 */
	data class Attack(
		override val location: NobaVec,
		override val player: ClientPlayerEntity,
		override val hand: Hand,
		val direction: Direction,
	) : BlockInteractionEvent

	/**
	 * Event invoked when the player "interacts" with - or right clicks - a block in the world
	 *
	 * @see EVENT
	 */
	data class Interact(
		val hitResult: BlockHitResult,
		override val player: ClientPlayerEntity,
		override val hand: Hand,
	) : BlockInteractionEvent {
		override val location: NobaVec = hitResult.toNobaVec()
	}
}