package me.nobaboy.nobaaddons.features.inventory.slotinfo.items

import me.nobaboy.nobaaddons.core.enchants.EnchantBase
import me.nobaboy.nobaaddons.core.enchants.UltimateEnchant
import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.features.inventory.slotinfo.ISlotInfo
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object EnchantedBookSlotInfo : ISlotInfo {
	override val enabled: Boolean get() = config.enchantedBookLevel || config.enchantedBookName

	override fun handle(event: ScreenRenderEvents.DrawItem) {
		val item = event.itemStack.asSkyBlockItem ?: return
		if(item.id != "ENCHANTED_BOOK" || item.enchantments.size != 1) return

		item.enchantments.let {
			if(config.enchantedBookLevel) drawCount(event, it.values.first().toString())
			if(config.enchantedBookName) {
				val text = formatEnchantName(it.keys.first())
				drawInfo(event, text)
			}
		}
	}

	private fun formatEnchantName(enchant: EnchantBase): Text {
		val abbreviation = enchant.abbreviation ?: getEnchantAbbreviation(enchant.name)

		return buildText {
			if(enchant is UltimateEnchant) formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD)
			append(abbreviation)
		}
	}

	private fun getEnchantAbbreviation(enchantName: String): String {
		val parts = enchantName.split(" ")

		return if(parts.size > 1) {
			parts.map { it.first() }.joinToString("")
		} else {
			enchantName.take(3) + "."
		}
	}
}