package me.nobaboy.nobaaddons.features.inventory.slotinfo.items

import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.inventory.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import net.minecraft.entity.EquipmentSlot

object RanchersBootsSpeedSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.ranchersBootsSpeed

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		val item = event.itemStack.asSkyBlockItem ?: return
		if(item.id != "RANCHERS_BOOTS") return

		val player = MCUtils.player ?: return

		val isBlackCatEquipped = PetAPI.currentPet?.let { it.id == "BLACK_CAT" } == true

		val helmet = player.getEquippedStack(EquipmentSlot.HEAD).asSkyBlockItem
		val heldItem = player.mainHandStack.asSkyBlockItem

		val isLimit500 = isBlackCatEquipped ||
			helmet?.id == "RACING HELMET" ||
			(SkyBlockIsland.GARDEN.inIsland() && heldItem?.id == "CACTUS_KNIFE")

		val speed = item.ranchersSpeed ?: return
		val speedLimit = if(isLimit500) 500 else 400
		val color = if(speed > speedLimit) NobaColor.RED else NobaColor.WHITE

		drawCount(event, speed.toString(), color)
	}
}