package me.nobaboy.nobaaddons.features.qol

//? if <1.21.2 {
/*import me.nobaboy.nobaaddons.utils.NobaVec
*///?}

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.impl.client.PacketEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.toNobaVec
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket

object MouseLock {
	val config = NobaConfig.INSTANCE.qol.garden

	private val FARMING_TOOLS: List<String> = buildList {
		val gardeningTools = listOf("HOE", "AXE")
		val gardeningToolTiers = listOf("BASIC", "ADVANCED")
		gardeningToolTiers.forEach { tier ->
			gardeningTools.forEach { tool ->
				add("${tier}_GARDENING_$tool")
			}
		}

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
		if(!config.reduceMouseSensitivity) return false

		val heldItem = MCUtils.player?.mainHandStack?.getSkyBlockItem() ?: return false
		if(heldItem.id == "DAEDALUS_AXE" || heldItem.id == "STARRED_DAEDALUS_AXE") return config.isDaedalusFarmingTool
		return heldItem.id in FARMING_TOOLS
	}

	fun init() {
		SkyBlockEvents.ISLAND_CHANGE.register { locked = false }
		PacketEvents.EARLY_RECEIVE.register(this::onEarlyPacketReceive)
	}

	private fun onEarlyPacketReceive(event: PacketEvents.EarlyReceive) {
		if(!config.autoUnlockMouseOnTeleport) return
		if(!locked) return

		val packet = event.packet
		if(packet !is PlayerPositionLookS2CPacket) return

		val playerLocation = LocationUtils.playerLocation().round(2)
		//? if >=1.21.2 {
		val packetLocation = packet.change.position.toNobaVec().round(2)
		//?} else {
		/*val packetLocation = NobaVec(packet.x, packet.y, packet.z).round(2)
		*///?}
		if(packetLocation.distance(playerLocation) >= 5) lockMouse()
	}

	fun lockMouse() {
		locked = !locked

		val text = if(locked) tr("nobaaddons.command.mouseLock.locked", "Mouse locked")
		else tr("nobaaddons.command.mouseLock.unlocked", "Mouse unlocked")

		ChatUtils.addMessage(text)
	}
}