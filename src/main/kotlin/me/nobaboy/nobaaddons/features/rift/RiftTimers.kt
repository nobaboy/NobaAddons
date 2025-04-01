package me.nobaboy.nobaaddons.features.rift

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI.inIsland
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.SkyBlockIsland
import me.nobaboy.nobaaddons.core.profile.ProfileData
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.InventoryEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.RegexUtils.firstFullMatch
import me.nobaboy.nobaaddons.utils.RegexUtils.indexOfFirstFullMatch
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.darkGray
import me.nobaboy.nobaaddons.utils.TextUtils.gray
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.asTimestamp
import me.nobaboy.nobaaddons.utils.Timestamp.Companion.toShortString
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyBlockId
import me.nobaboy.nobaaddons.utils.items.ItemUtils.stringLines
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.tooltip.TooltipType
import net.minecraft.text.Text
import kotlin.math.floor
import kotlin.time.Duration.Companion.hours

object RiftTimers {
	private val config = NobaConfig.rift
	private val data get() = ProfileData.PROFILE.riftTimers

	private val warpLocation by config::warpTarget
	private fun clickToWarp() = tr("nobaaddons.rift.clickToWarp", "Click to warp to ${warpLocation.displayName}").yellow()

	private val FREE_INFUSIONS_REGEX by Regex("Free infusions: (?<count>\\d)/\\d").fromRepo("rift.free_infusions")
	private val NEXT_FREE_INFUSION_REGEX by Regex("Next infusion in: (?<time>(?:\\d+[hms] ?)+)").fromRepo("rift.next_free_infusion")
	private val SPLIT_STEAL_COOLDOWN_REGEX by Regex("SPLIT! You need to wait (?<time>(?:\\d+[hms] ?)+) before you can play again\\.").fromRepo("rift.split_steal_cooldown")

	private var notifiedSplitStealCooldown = false

	fun init() {
		TickEvents.everySecond { onSecondPassed() }
		InventoryEvents.OPEN.register(this::onOpenInventory)
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		ItemTooltipCallback.EVENT.register(this::addSplitStealItemCooldown)
	}

	private fun onSecondPassed() {
		if(!SkyBlockAPI.inSkyBlock) return
		updateNextInfusion()
		updateSplitSteal()
	}

	private fun onOpenInventory(event: InventoryEvents.Open) {
		when(event.inventory.title) {
			"Dimensional Infusion", "Fast Travel" -> updateFreeInfusions(event)
			"Split or Steal" -> {
				if(!SkyBlockIsland.RIFT.inIsland()) return
				data.nextSplitSteal = Timestamp.now() + 2.hours
				notifiedSplitStealCooldown = false
			}
		}
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		val string = event.message.string.cleanFormatting()
		if(string == "INFUSED! Used one of your free Rift charges!") {
			data.freeInfusions -= 1
			if(data.nextFreeInfusion == null) {
				data.nextFreeInfusion = Timestamp.now() + 4.hours
			}
		} else if(string.startsWith("SPLIT! You need to wait")) {
			val match = SPLIT_STEAL_COOLDOWN_REGEX.matchEntire(string) ?: return
			data.nextSplitSteal = match.groups["time"]!!.value.asTimestamp() ?: return
			notifiedSplitStealCooldown = false
		}
	}

	private fun addSplitStealItemCooldown(item: ItemStack, ctx: Item.TooltipContext, type: TooltipType, lines: MutableList<Text>) {
		if(item.skyBlockId != "UBIKS_CUBE") return
		if(!config.splitStealItemCooldown) return
		val cooldown = data.nextSplitSteal?.takeIf { it.isFuture() }?.timeRemaining()?.toShortString()?.toText()?.yellow() ?: return

		val index = lines.map { it.string.cleanFormatting() }.indexOfFirstFullMatch(CommonPatterns.ITEM_COOLDOWN_REGEX)
		if(index == -1) return

		lines.add(index + 1, tr("nobaaddons.rift.ubikCube.itemCooldown", "On cooldown for: $cooldown").darkGray())
	}

	private fun updateNextInfusion() {
		val data = this.data
		val nextInfusion = data.nextFreeInfusion ?: return
		val gained = floor((nextInfusion - 4.hours).elapsedSince() / 4.hours).toInt().coerceIn(0, 3)
		if(gained == 0) return

		data.freeInfusions = (data.freeInfusions + gained).coerceAtMost(3)
		data.nextFreeInfusion = if(data.freeInfusions < 3) {
			nextInfusion + (gained * 4).hours
		} else {
			null
		}

		if(config.freeInfusionAlert) {
			val count = buildLiteral("(${data.freeInfusions}/3)") { gray() }
			ChatUtils.addMessageWithClickAction(
				tr("nobaaddons.rift.gainedFreeInfusion", "You've regained a free Rift infusion! $count"),
				builder = { hoverText(clickToWarp()) }
			) {
				ChatUtils.queueCommand("warp ${warpLocation.warpName}")
			}
		}
	}

	private fun updateSplitSteal() {
		val nextSS = data.nextSplitSteal ?: return
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

	private fun updateFreeInfusions(event: InventoryEvents.Open) {
		val itemName = when(event.inventory.title) {
			"Dimensional Infusion" -> "Dimensional Infusion"
			"Fast Travel" -> "The Rift - Wizard Tower"
			else -> return
		}
		val lore = event.inventory.items.values.firstOrNull { it.name.string.cleanFormatting() == itemName }?.lore?.stringLines ?: return

		val infusionCount = lore.firstFullMatch(FREE_INFUSIONS_REGEX)?.groups["count"]?.value?.toInt() ?: return
		data.freeInfusions = infusionCount
		data.nextFreeInfusion = if(infusionCount < 3) {
			lore.firstFullMatch(NEXT_FREE_INFUSION_REGEX)?.groups["time"]?.value?.asTimestamp()
		} else {
			null
		}
	}
}