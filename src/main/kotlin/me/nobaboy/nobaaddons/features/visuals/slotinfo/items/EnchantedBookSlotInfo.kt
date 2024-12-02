package me.nobaboy.nobaaddons.features.visuals.slotinfo.items

import me.nobaboy.nobaaddons.events.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.visuals.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object EnchantedBookSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.enchantedBookLevel || config.enchantedBookName

	private val correctEnchantIds = mutableMapOf<String, String>(
		"aiming" to "dragon_tracer",
		"ultimate_reiterate" to "ultimate_duplex",
		"ultimate_wise" to "ultimate_ultimate_wise",
		"ultimate_jerry" to "ultimate_ultimate_jerry"
	)

	override fun handle(event: ScreenRenderEvents.DrawSlot) {
		val item = event.itemStack.getSkyBlockItem() ?: return
		if(item.id != "ENCHANTED_BOOK" || item.enchantments.size != 1) return

		if(config.enchantedBookLevel) drawCount(event, item.enchantments.values.first().toString())
		if(config.enchantedBookName) {
			val enchantId = item.enchantments.keys.first()
			drawInfo(event, formatEnchantName(enchantId))
		}
	}

	private fun formatEnchantName(id: String): Text {
		val enchantId = correctEnchantIds[id] ?: id

		val text = buildText {
			when {
				enchantId.startsWith("ultimate_") -> {
					formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD)
					append(formatShortenedName(enchantId.removePrefix("ultimate_")))
				}
				enchantId.startsWith("turbo_") -> {
					append(formatShortenedName(enchantId.removePrefix("turbo_")))
				}
				else -> {
					append(formatShortenedName(enchantId))
				}
			}
		}

		return text
	}

	private fun formatShortenedName(rawName: String): String {
		val parts = rawName.replace("_", " ").title().split(" ")
		return if(parts.size > 1) {
			parts.joinToString("") { it.first().toString() }
		} else {
			rawName.take(3).title().plus(".")
		}
	}
}