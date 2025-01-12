package me.nobaboy.nobaaddons.features.rift

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.events.ChatMessageEvents
import me.nobaboy.nobaaddons.events.InventoryEvents
import me.nobaboy.nobaaddons.events.SecondPassedEvent
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.asTimestamp
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import me.nobaboy.nobaaddons.utils.tr
import kotlin.math.floor
import kotlin.time.Duration.Companion.hours

object RiftTimers {
	private val config = NobaConfig.INSTANCE.rift

	private val warpLocation by config::warpTarget
	private fun clickToWarp() = tr("nobaaddons.rift.clickToWarp", "Click to warp to ${warpLocation.displayName}").yellow()

	private val freeInfusions by Regex("Free infusions: (?<count>\\d)/\\d").fromRepo("rift.free_infusions")
	private val nextFreeInfusion by Regex("Next infusion in: (?<time>(?:\\d+[hms] ?)+)").fromRepo("rift.next_free_infusion")
	private val splitStealCooldown by Regex("SPLIT! You need to wait (?<time>(?:\\d+[hms] ?)+) before you can play again\\.").fromRepo("rift.split_steal_cooldown")

	private var notifiedSplitStealCooldown = false

	fun init() {
		InventoryEvents.OPEN.register(this::onOpenInventory)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		SecondPassedEvent.EVENT.register(this::onSecondPassed)
	}

	private fun updateNextInfusion() {
		val nextInfusion = RiftTimerData.nextFreeInfusion ?: return
		val gained = floor((nextInfusion - 4.hours).elapsedSince() / 4.hours).toInt().coerceIn(0, 3)
		if(gained == 0) return

		RiftTimerData.freeRiftInfusions = (RiftTimerData.freeRiftInfusions + gained).coerceAtMost(3)
		RiftTimerData.nextFreeInfusion = if(RiftTimerData.freeRiftInfusions < 3) {
			nextInfusion + (gained * 4).hours
		} else {
			null
		}

		if(config.freeInfusionAlert) {
			val count = buildLiteral("(${RiftTimerData.freeRiftInfusions}/3)") { gray() }
			ChatUtils.addMessageWithClickAction(
				tr("nobaaddons.rift.gainedFreeInfusion", "You've regained a free Rift infusion! $count"),
				builder = { hoverText(clickToWarp()) }
			) {
				ChatUtils.queueCommand("warp ${warpLocation.warpName}")
			}
		}
	}

	private fun updateSplitSteal() {
		val nextSS = RiftTimerData.nextSplitSteal ?: return
		if(nextSS.isPast() && !notifiedSplitStealCooldown) {
			notifiedSplitStealCooldown = true
			if(config.splitStealAlert) {
				ChatUtils.addMessageWithClickAction(
					tr("nobaaddons.rift.splitStealOffCooldown", "Split or Steal cooldown has ended!"),
					builder = { hoverText(clickToWarp()) }
				) {
					ChatUtils.queueCommand("warp ${warpLocation.warpName}")
				}
			}
		}
	}

	private fun onSecondPassed(@Suppress("unused") event: SecondPassedEvent) {
		if(!SkyBlockAPI.inSkyBlock) return
		updateNextInfusion()
		updateSplitSteal()
	}

	private fun updateFreeInfusions(event: InventoryEvents.Open) {
		val itemName = when(event.inventory.title) {
			"Dimensional Infusion" -> "Dimensional Infusion"
			"Fast Travel" -> "The Rift - Wizard Tower"
			else -> return
		}
		val lore = event.inventory.items.values.firstOrNull { it.name.string.cleanFormatting() == itemName }?.lore?.stringLines ?: return

		val infusionCount = lore.firstFullMatch(freeInfusions)?.groups["count"]?.value?.toInt() ?: return
		RiftTimerData.freeRiftInfusions = infusionCount

		RiftTimerData.nextFreeInfusion = if(infusionCount < 3) {
			lore.firstFullMatch(nextFreeInfusion)?.groups["time"]?.value?.asTimestamp()
		} else {
			null
		}
	}

	private fun onOpenInventory(event: InventoryEvents.Open) {
		when(event.inventory.title) {
			"Dimensional Infusion", "Fast Travel" -> updateFreeInfusions(event)
			"Split or Steal" -> {
				if(!SkyBlockIsland.RIFT.inIsland()) return
				RiftTimerData.nextSplitSteal = Timestamp.now() + 2.hours
				notifiedSplitStealCooldown = false
			}
		}
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		val string = event.message.string.cleanFormatting()
		if(string == "INFUSED! Used one of your free Rift charges!") {
			RiftTimerData.freeRiftInfusions -= 1
			if(RiftTimerData.nextFreeInfusion == null) {
				RiftTimerData.nextFreeInfusion = Timestamp.now() + 4.hours
			}
		} else if(string.startsWith("SPLIT! You need to wait")) {
			val match = splitStealCooldown.matchEntire(string) ?: return
			RiftTimerData.nextSplitSteal = match.groups["time"]!!.value.asTimestamp() ?: return
			notifiedSplitStealCooldown = false
		}
	}
}