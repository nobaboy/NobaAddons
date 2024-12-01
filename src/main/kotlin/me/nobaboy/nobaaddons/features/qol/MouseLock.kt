package me.nobaboy.nobaaddons.features.qol

import me.nobaboy.nobaaddons.api.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem

object MouseLock {
	private val FARMING_TOOLS: List<String> = buildList {
		val theoreticalHoeCrops = listOf("WHEAT", "CARROT", "POTATO", "WARTS", "CANE")
		val dicers = listOf("MELON_DICER", "PUMPKIN_DICER")
		(1..3).forEach { tier ->
			theoreticalHoeCrops.forEach { add("THEORETICAL_HOE_${it}_$tier") }
			dicers.forEach { add("${it}${if(tier > 1) "_$tier" else ""}") }
		}
		add("CACTUS_KNIFE")
		add("COCO_CHOPPER")
		add("FUNGI_CUTTER")
	}

	@get:JvmStatic
	@get:JvmName("isLocked")
	var locked: Boolean = false
		private set

	@get:JvmStatic
	@get:JvmName("isReduced")
	val reduced: Boolean get() {
		if(!SkyBlockIsland.GARDEN.inIsland()) return false
		if(MCUtils.player?.abilities?.flying == true) return false

		val config = NobaConfigManager.config.qol.garden
		if(!config.reduceMouseSensitivity) return false

		val heldItem = MCUtils.player?.mainHandStack?.getSkyBlockItem() ?: return false
		if(heldItem.id == "DAEDALUS_AXE" || heldItem.id == "STARRED_DAEDALUS_AXE") return config.isDaedalusFarmingTool
		return heldItem.id in FARMING_TOOLS
	}

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { locked = false }
	}

	fun lockMouse() {
		locked = !locked

		val text = if(locked) "Mouse locked" else "Mouse unlocked"
		ChatUtils.addMessage(text)
	}
}