package me.nobaboy.nobaaddons.events.impl.fabric

import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventDispatcher
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Text

data class ItemTooltipEvent(
	val itemStack: ItemStack,
	val ctx: Item.TooltipContext,
	val type: TooltipType,
	val lines: MutableList<Text>,
) : Event() {
	companion object {
		init {
			ItemTooltipCallback.EVENT.register { stack, ctx, type, lines ->
				EVENT.invoke(ItemTooltipEvent(stack, ctx, type, lines))
			}
		}

		val EVENT = EventDispatcher<ItemTooltipEvent>()
	}
}
