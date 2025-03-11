package me.nobaboy.nobaaddons.features.inventory.slotinfo.items

import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.inventory.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.features.inventory.slotinfo.Position
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object PetSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.petLevel || config.petItem || config.petCandy

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		val pet = PetAPI.getPetData(event.itemStack) ?: return

		if(config.petLevel && pet.level != pet.maxLevel) drawCount(event, pet.level.toString())
		if(config.petCandy && pet.candy > 0) drawInfo(event, Text.literal("■").formatted(Formatting.RED))

		if(config.petItem) {
			val text = when(pet.heldItem) {
				"PET_ITEM_TIER_BOOST" -> Text.literal("↑").formatted(Formatting.GOLD)
				"PET_ITEM_LUCKY_CLOVER" -> Text.literal("♣").formatted(Formatting.GREEN) // maybe dark green?
				else -> null
			} ?: return

			drawInfo(event, text, Position.TOP_RIGHT)
		}
	}
}