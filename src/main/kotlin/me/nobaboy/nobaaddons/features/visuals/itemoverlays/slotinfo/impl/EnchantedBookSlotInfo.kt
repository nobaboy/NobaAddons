package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.SlotInfo
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.SkyBlockItemData
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object EnchantedBookSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.enchantedBook

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val item = event.itemStack.getSkyBlockItem() ?: return
		if(item.id != "ENCHANTED_BOOK" || item.enchantments.size != 1) return

		drawCount(event, item.enchantments.values.first().toString())
		getEnchantName(item)?.let { drawInfo(event, it) }
	}

	private fun getEnchantName(item: SkyBlockItemData): SlotInfo? {
		val enchant = item.enchantments.keys.first()
		val text = formatEnchantName(enchant)

		return SlotInfo(text)
	}

	private fun formatEnchantName(enchant: String): Text {
		val formattedText = buildText {
			when {
				enchant.startsWith("ultimate_") -> {
					val name = if(enchant.endsWith("wise") || enchant.endsWith("jerry")) enchant else enchant.removePrefix("ultimate_")
					formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD)
					append(formatShortenedName(name))
				}
				enchant.startsWith("turbo_") -> {
					append(formatShortenedName(enchant.removePrefix("turbo_")))
				}
				else -> {
					append(formatShortenedName(enchant))
				}
			}
		}
		return formattedText
	}

	private fun formatShortenedName(rawName: String): String {
		val parts = rawName.replace("_", " ").title().split(" ")
		return if (parts.size > 1) {
			parts.joinToString("") { it.first().toString() }
		} else {
			rawName.take(3).title().plus(".")
		}
	}
}