package me.nobaboy.nobaaddons.features.visuals.slotinfo.impl

import me.nobaboy.nobaaddons.api.PetAPI
import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.features.visuals.slotinfo.Position
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object PetSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.petLevel || config.petItem || config.petCandy

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val pet = PetAPI.getPetData(event.itemStack) ?: return

		if(config.petLevel && pet.level < 100) drawCount(event, pet.level.toString())
		if(config.petCandy && pet.candy > 0) drawInfo(event, Text.literal("■").formatted(Formatting.RED))

		if(config.petItem) {
			when(pet.heldItem) {
				"PET_ITEM_TIER_BOOST" -> drawInfo(event, Text.literal("↑").formatted(Formatting.GOLD), Position.TOP_RIGHT)
				"PET_ITEM_LUCKY_CLOVER" -> drawInfo(event, Text.literal("♣").formatted(Formatting.GREEN), Position.TOP_RIGHT)
			}
		}
	}
}