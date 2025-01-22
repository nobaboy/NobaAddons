package me.nobaboy.nobaaddons.events.impl.client

//? if <=1.21.1 {
/*import net.minecraft.util.TypedActionResult
*///?}

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import me.nobaboy.nobaaddons.utils.NobaVec
import me.nobaboy.nobaaddons.utils.toNobaVec
import net.fabricmc.fabric.api.event.player.AttackBlockCallback
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseItemCallback
import net.minecraft.client.network.ClientPlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.ActionResult
import net.minecraft.util.Hand
import net.minecraft.util.hit.BlockHitResult
import net.minecraft.util.math.Direction

@Suppress("CanBeParameter")
object InteractEvents {
	init {
		UseItemCallback.EVENT.register { player, _, hand ->
			if(player is ClientPlayerEntity) {
				USE_ITEM.invoke(UseItem(player, hand))
			}
			//? if <=1.21.1 {
			/*TypedActionResult.pass(player.getStackInHand(hand))
			*///?} else {
			ActionResult.PASS
			//?}
		}
		AttackBlockCallback.EVENT.register { player, _, hand, block, direction ->
			if(player is ClientPlayerEntity) {
				INTERACT_BLOCK.invoke(AttackBlockInteraction(player, hand, block.toNobaVec(), direction))
			}
			ActionResult.PASS
		}
		UseBlockCallback.EVENT.register { player, _, hand, hitResult ->
			if(player is ClientPlayerEntity) {
				INTERACT_BLOCK.invoke(UseBlockInteraction(player, hand, hitResult))
			}
			ActionResult.PASS
		}
	}

	/**
	 * Event invoked when the client player right clicks; this may also be paired with an [INTERACT_BLOCK] event
	 *
	 * This event is a wrapper around the Fabric API [UseItemCallback] event.
	 */
	val USE_ITEM = EventDispatcher<UseItem>()

	/**
	 * Event invoked whenever the client player interacts with a block (be it a left or right click)
	 *
	 * This event is a wrapper around the Fabric API [AttackBlockCallback] and [UseBlockCallback] events.
	 *
	 * @see AttackBlockInteraction
	 * @see UseBlockInteraction
	 */
	val INTERACT_BLOCK = EventDispatcher<BlockInteraction>()

	sealed class GenericInteractEvent protected constructor(val player: ClientPlayerEntity, val hand: Hand) : Event() {
		val itemInHand: ItemStack = player.getStackInHand(hand)
	}

	class UseItem(player: ClientPlayerEntity, hand: Hand) : GenericInteractEvent(player, hand)

	sealed class BlockInteraction protected constructor(player: ClientPlayerEntity, hand: Hand) : GenericInteractEvent(player, hand) {
		abstract val block: NobaVec
	}

	class AttackBlockInteraction(
		player: ClientPlayerEntity,
		hand: Hand,
		override val block: NobaVec,
		val direction: Direction,
	) : BlockInteraction(player, hand)

	class UseBlockInteraction(
		player: ClientPlayerEntity,
		hand: Hand,
		val hitResult: BlockHitResult,
	) : BlockInteraction(player, hand) {
		override val block: NobaVec = hitResult.toNobaVec()
	}
}