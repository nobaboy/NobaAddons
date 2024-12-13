package me.nobaboy.nobaaddons.features.general

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.chat.HypixelCommands
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItemId

object RefillFromSacks {
	fun count(id: String): Int {
		val player = MCUtils.player ?: return 0
		return (0 until player.inventory.size())
			.asSequence()
			.mapNotNull {
				player.inventory.getStack(it)
					?.takeIf { it.getSkyBlockItemId() == id }
					?.count
			}
			.sum()
	}

	fun refill(id: String, target: Int = 64) {
		if(!SkyBlockAPI.inSkyBlock) {
			ChatUtils.addMessage("This is only usable while in SkyBlock")
			return
		}

		val inInventory = count(id)
		if(inInventory >= target) {
			ChatUtils.addMessage("Already at the requested amount of items")
			return
		}

		HypixelCommands.getFromSacks(id, target - inInventory)
		return
	}
}