package me.nobaboy.nobaaddons.features.inventory.enchants

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.enchants.Enchant
import me.nobaboy.nobaaddons.core.enchants.EnchantBase
import me.nobaboy.nobaaddons.core.enchants.StackingEnchant
import me.nobaboy.nobaaddons.core.enchants.UltimateEnchant
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.romanToArabic
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.StringUtils.toAbbreviatedString
import me.nobaboy.nobaaddons.utils.mc.TextUtils.blue
import me.nobaboy.nobaaddons.utils.mc.TextUtils.bold
import me.nobaboy.nobaaddons.utils.mc.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.mc.TextUtils.darkGray
import me.nobaboy.nobaaddons.utils.mc.TextUtils.darkRed
import me.nobaboy.nobaaddons.utils.mc.TextUtils.lightPurple
import me.nobaboy.nobaaddons.utils.mc.TextUtils.toText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.asSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.isSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.skyBlockId
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.util.InputUtil
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.Texts
import org.lwjgl.glfw.GLFW

// TODO: Could do with caching the lore
object EnchantmentTooltips {
	private val config get() = NobaConfig.inventory.enchantmentTooltips

	// the extra [\d,]+ is to account for stacking enchants adding their value
	private val ENCHANT_LINE_REGEX = Regex("^(?:.+ [IVX]+(?: [\\d,]+)?(?:$|,))+")

	fun init() {
		ItemTooltipCallback.EVENT.register { item, _, _, lines ->
			try {
				parseEnchants(item, lines)
			} catch(e: Throwable) {
				ErrorManager.logError("Failed to parse enchants on item tooltip", e)
			}
		}
	}

	private fun parseEnchants(item: ItemStack, lines: MutableList<Text>) {
		if(!config.modifyTooltips) return
		if(InputUtil.isKeyPressed(MCUtils.window.handle, GLFW.GLFW_KEY_RIGHT_SHIFT)) return
		if(!item.isSkyBlockItem || item.asSkyBlockItem?.enchantments?.isEmpty() != false) return
		if(lines.size <= 1) return

		// Please do not try to shorten this unless you're keeping the behavior the exact same.
		val firstEnchant: Int = run {
			for((index, line) in lines.withIndex()) {
				// Ignore the first line to avoid matching against item names like Jasper Drill X
				if(index == 0) continue

				// Attempt to match against the first line to account for items that have no stats, but do have
				// enchantments (notably enchanted books)
				if(index == 1 && ENCHANT_LINE_REGEX.matches(line.string.cleanFormatting())) {
					return@run index
				}

				// Only attempt to match against lines that immediately follow a blank line
				if(line.siblings.isNotEmpty()) continue
				val next = index + 1
				val nextLine = lines.getOrNull(next) ?: return

				if(ENCHANT_LINE_REGEX.matches(nextLine.string.cleanFormatting())) {
					return@run next
				}
			}
			return
		}

		val enchants = mutableListOf<ParsedEnchant>()
		var lastEnchant: ParsedEnchant? = null
		// Start by just parsing the enchantments
		for(line in lines.slice(firstEnchant until lines.size)) {
			if(line.siblings.isEmpty()) break

			// Do a quick sniff test to see if this looks like a line with enchants on it, and if not simply add it
			// to the last enchantment's description
			if(ENCHANT_LINE_REGEX.matchEntire(line.string.cleanFormatting()) == null) {
				lastEnchant?.description?.add(line)
				continue
			}

			// Each enchantment is its own sibling in the empty parent text node, which makes it trivially
			// easy to simply do a bit of string manipulation to parse them here.
			line.siblings.forEach {
				val string = it.string
				// for whatever reason, there's an extra blank sibling before ult enchants?
				if(string == ", " || string.isBlank()) return@forEach

				// trim() to account for stacking enchants
				val parts = string.trim().split(" ").toMutableList()

				// ignore components that are just counts from stacking enchants
				if(parts.size == 1 && parts.last().all { it in ('0'..'9') || it == ',' }) {
					return@forEach
				}

				val numeral = parts.removeLast()
				val arabic = numeral.romanToArabic()
				val name = parts.joinToString(" ")

				val enchant = ParsedEnchant(item, name, numeral to arabic, Enchant.getByName(name))
				enchants.add(enchant)
				lastEnchant = enchant
			}
		}

		// Then, remove everything up until the first blank line, or until we (somehow!?) consume everything
		while(!lines.getOrNull(firstEnchant)?.string.isNullOrBlank()) {
			lines.removeAt(firstEnchant)
		}

		val isSingleEnchantBook = item.skyBlockId == "ENCHANTED_BOOK" && enchants.size == 1
		val shouldCompact = when(config.displayMode) {
			EnchantmentDisplayMode.NORMAL -> enchants.size > 5
			// Enchanted books should always display the description if there's only a single enchantment on it
			EnchantmentDisplayMode.COMPACT -> !isSingleEnchantBook
			EnchantmentDisplayMode.LINES -> false
		}
		val enchantText: List<Text> = if(shouldCompact) {
			enchants.chunked(3) {
				Texts.join(it.flatMap { it.toText(false) }, Text.literal(", ").blue())
			}
		} else {
			enchants.flatMap { it.toText(isSingleEnchantBook || config.showDescriptions) }
		}

		// And finally, add them back to the tooltip. asReversed() is important here, as we're always appending at
		// the original first enchant index, and if we don't reverse the list we'll simply reverse their original order.
		enchantText.asReversed().forEach { lines.add(firstEnchant, it) }
	}

	private fun MutableText.colorize(enchant: EnchantBase, tier: Int): MutableText {
		// ult enchants should always match the vanilla tooltip
		if(enchant is UltimateEnchant) return lightPurple().bold()

		if(tier >= enchant.max) return withColor(config.maxColor.rgb)
		// if enchants have a good value of -1 then assume that they never have a proper "good" or "bad" tier, only
		// average and max
		if(tier > enchant.good) return withColor(if(enchant.good == -1) config.averageColor.rgb else config.goodColor.rgb)
		if(tier == enchant.good) return withColor(config.averageColor.rgb)
		return withColor(config.badColor.rgb)
	}

	private fun Pair<EnchantBase, Pair<String, Int>>.toText(): Text {
		val tier = if(config.replaceRomanNumerals) second.second else second.first
		return "${first.name} $tier".toText().colorize(first, second.second)
	}

	private data class ParsedEnchant(
		val item: ItemStack,
		val name: String,
		val tier: Pair<String, Int>,
		val enchant: EnchantBase?,
		val description: MutableList<Text> = mutableListOf(),
	) {
		private val stackingProgress: MutableText? get() {
			if(enchant is StackingEnchant) {
				val (_, current, progress) = item.asSkyBlockItem?.stackingEnchantProgress[enchant] ?: return null
				return buildText {
					append("(")
					append(current.toAbbreviatedString(millionPrecision = 1))
					progress?.let {
						append("/${it.toAbbreviatedString(millionPrecision = 1)}")
					}
					append(")")
					darkGray()
				} as MutableText
			}
			return null
		}

		private fun nameText(includeStacking: Boolean) = buildText {
			if(enchant == null) {
				append("$name ${tier.first}".toText().darkRed())
			} else {
				append((enchant to tier).toText())
			}
			if(includeStacking && config.showStackingProgress) {
				stackingProgress?.let {
					append(" ")
					append(it)
				}
			}
		}

		fun toText(includeDescription: Boolean) = buildList {
			add(nameText(!includeDescription))
			if(description.isNotEmpty() && includeDescription) {
				addAll(description)
			}
		}
	}
}