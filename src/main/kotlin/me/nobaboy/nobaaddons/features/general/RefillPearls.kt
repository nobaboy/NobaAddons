package me.nobaboy.nobaaddons.features.general

import com.mojang.brigadier.Command
import me.nobaboy.nobaaddons.api.SkyblockAPI
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.StringUtils.lowercaseEquals
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

object RefillPearls : CooldownManager() {
	fun getMissingPearls(): Int {
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

	fun refillPearls(): Int {
		if(!isEnabled()) return 0

		val missingPearls = getMissingPearls()
		if(missingPearls == 0) return 0

		HypixelCommands.getFromSacks("ENDER_PEARL", missingPearls)
		return Command.SINGLE_SUCCESS
	}

	fun isEnabled() = SkyblockAPI.inSkyblock
}