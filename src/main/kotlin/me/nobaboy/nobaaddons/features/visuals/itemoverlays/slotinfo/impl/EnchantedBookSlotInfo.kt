package me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.impl

import me.nobaboy.nobaaddons.api.SkyBlockAPI
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.features.visuals.itemoverlays.slotinfo.SlotInfo
import me.nobaboy.nobaaddons.utils.StringUtils.title
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.SkyBlockItemData
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object EnchantedBookSlotInfo : ISlotInfo {
	private var cachedItem: SkyBlockItemData? = null

	override fun shouldRender(itemStack: ItemStack): Boolean {
		val item = itemStack.getSkyBlockItem() ?: return false
		if(item.id != "ENCHANTED_BOOK" || item.enchantments.size != 1) return false

		cachedItem = item
		return true
	}

	override fun getSlotInfos(): List<SlotInfo>? {
		val enchant = cachedItem?.enchantments?.keys?.first() ?: return null
		val text = formatEnchantName(enchant, cachedItem!!.rarity.color?.toFormatting() ?: Formatting.WHITE)

		return listOf(SlotInfo(text))
	}

	override fun getStackOverlay(): String? = cachedItem?.enchantments?.values?.first().toString()

	private fun formatEnchantName(enchant: String, formatting: Formatting): Text {
		val formattedText = buildText {
			when {
				enchant.startsWith("ultimate_") -> {
					val name = if(enchant.endsWith("wise") || enchant.endsWith("jerry")) enchant else enchant.removePrefix("ultimate_")
					formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD)
					append(formatShortenedName(name))
				}
				enchant.startsWith("turbo_") -> {
					formatted(formatting)
					append(formatShortenedName(enchant.removePrefix("turbo_")))
				}
				else -> {
					formatted(formatting)
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

	override fun isEnabled(): Boolean = SkyBlockAPI.inSkyblock && config.enchantedBook
}