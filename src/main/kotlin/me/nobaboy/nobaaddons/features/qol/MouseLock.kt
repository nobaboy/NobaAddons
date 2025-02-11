package me.nobaboy.nobaaddons.features.qol

//? if <1.21.2 {
/*import me.nobaboy.nobaaddons.utils.NobaVec
*///?}

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.option.booleanController
import me.nobaboy.nobaaddons.config.option.intSlider
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.EventListener
import me.nobaboy.nobaaddons.events.impl.client.PacketEvents
import me.nobaboy.nobaaddons.events.impl.skyblock.SkyBlockEvents
import me.nobaboy.nobaaddons.features.Feature
import me.nobaboy.nobaaddons.features.FeatureCategory
import me.nobaboy.nobaaddons.utils.LocationUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.darkAqua
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.toNobaVec
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket
import kotlin.reflect.jvm.isAccessible

object MouseLock : Feature(
	id = "mouseLock",
	name = tr("nobaaddons.feature.mouseLock", "Mouse Sensitivity"),
	category = FeatureCategory.QOL,
) {
	private val lockMouseCommand = buildLiteral("/noba lockmouse") { darkAqua() }

	private var autoUnlockMouseOnTeleport by config(false) {
		name = tr("nobaaddons.config.qol.garden.autoUnlockMouseOnTeleport", "Auto Unlock Mouse on Teleport")
		description = tr("nobaaddons.config.qol.garden.autoUnlockMouseOnTeleport.tooltip", "Automatically unlocks your mouse when teleporting more than 5 blocks if locked with $lockMouseCommand")
		booleanController()
	}

	private var reduceMouseSensitivity by config(false) {
		name = tr("nobaaddons.config.qol.garden.reduceMouseSensitivity", "Reduce Mouse Sensitivity")
		description = tr("nobaaddons.config.qol.garden.reduceMouseSensitivity.tooltip", "Reduces your mouse sensitivity in the Garden while holding a farming tool and on the ground. Your mouse may also be locked with $lockMouseCommand")
		booleanController()
	}

	@JvmStatic var reductionMultiplier by config(6) {
		name = tr("nobaaddons.config.qol.garden.reductionMultiplier", "Reduction Multiplier")
		intSlider(min = 2, max = 10)
		requires { option(::reduceMouseSensitivity) }
	}

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
		if(!reduceMouseSensitivity) return false

		val heldItem = MCUtils.player?.mainHandStack?.getSkyBlockItem() ?: return false
		return heldItem.id in FARMING_TOOLS
	}

	override fun init() {
		listen(SkyBlockEvents.ISLAND_CHANGE) { locked = false }
		// TODO
		PacketEvents.EarlyReceive.registerFunction(this::onEarlyPacketReceive.also { it.isAccessible = true }, this)
	}

	@EventListener
	private fun onEarlyPacketReceive(event: PacketEvents.EarlyReceive) {
		if(!autoUnlockMouseOnTeleport) return
		if(!locked) return

		val packet = event.packet as? PlayerPositionLookS2CPacket ?: return

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