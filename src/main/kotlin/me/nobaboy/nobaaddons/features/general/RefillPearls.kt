package me.nobaboy.nobaaddons.features.general

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object RefillPearls : CooldownManager() {
	fun refillPearls() {
		if(!isEnabled()) return

		val missingPearls = getMissingPearls()
		if(missingPearls == 0) return

		HypixelCommands.getFromSacks("ENDER_PEARL", missingPearls)
	}

	private fun getMissingPearls(): Int {
		if(!isEnabled()) return 0

		var sum = 0
		val player = MCUtils.player ?: return 0
		for(i in 0 until player.inventory.size()) {
			val itemStack: ItemStack? = player.inventory.getStack(i)
			if(itemStack?.item != Items.ENDER_PEARL) continue
			if(!itemStack.name.string.cleanFormatting().lowercaseEquals("ender pearl")) continue

			sum += itemStack.count
		}
		return (16 - sum).coerceAtLeast(0)
	}

	private fun isEnabled() = SkyBlockAPI.inSkyblock
}