package me.nobaboy.nobaaddons.features.inventory

import me.nobaboy.nobaaddons.config.NobaConfigManager
import me.nobaboy.nobaaddons.core.enchants.Enchant
import me.nobaboy.nobaaddons.core.enchants.EnchantBase
import me.nobaboy.nobaaddons.core.enchants.StackingEnchant
import me.nobaboy.nobaaddons.core.enchants.UltimateEnchant
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NumberUtils.romanToArabic
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import me.nobaboy.nobaaddons.utils.StringUtils.toAbbreviatedString
import me.nobaboy.nobaaddons.utils.TextUtils.blue
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.darkGray
import me.nobaboy.nobaaddons.utils.TextUtils.darkRed
import me.nobaboy.nobaaddons.utils.TextUtils.lightPurple
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.getSkyBlockItemId
import me.nobaboy.nobaaddons.utils.items.ItemUtils.isSkyBlockItem
import me.nobaboy.nobaaddons.utils.items.ItemUtils.nbtCompound
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback
import net.minecraft.client.util.InputUtil
import net.minecraft.item.ItemStack
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.text.Texts
import org.lwjgl.glfw.GLFW

object EnchantParsing {
	private val config get() = NobaConfigManager.config.uiAndVisuals.enchantments
	// the extra [\d,]+ is to account for stacking enchants adding their value
	private val ENCHANT_LINE = Regex("^(?:(?<name>.+) (?<tier>[IVX]+)(?: [\\d,]+)?(?:$|,))+")

	fun init() {
		ItemTooltipCallback.EVENT.register { item, _, _, lines ->
			try {
				parseEnchants(item, lines)
			} catch(e: Throwable) {
				ErrorManager.logError("Failed to parse enchants on item", e)
			}
		}
	}

	private fun parseEnchants(item: ItemStack, lines: MutableList<Text>) {
		if(!config.parseItemEnchants) return
		if(InputUtil.isKeyPressed(MCUtils.window.handle, GLFW.GLFW_KEY_RIGHT_SHIFT)) return
		if(!item.isSkyBlockItem || item.getSkyBlockItem()?.enchantments?.isEmpty() != false) return

		// Find the first line with an enchantment
		val firstEnchant = lines.toMutableList()
			.indexOfFirst { ENCHANT_LINE matches it.string.cleanFormatting() }.takeIf { it > -1 }
			?: return

		val enchants = mutableListOf<ParsedEnchant>()
		var lastEnchant: ParsedEnchant? = null
		for(line in lines.slice(firstEnchant until lines.size)) {
			val string = line.string.takeIf { it.isNotBlank() } ?: break
			val lineEnchants = ENCHANT_LINE.findAll(string).toList()
				// Split commas (since regex-ing that requires more workshopping on this regex pattern)
				.flatMap { it.groups[0]!!.value.split(", ") }
				// And then rerun it through the regex to get the groups
				.mapNotNull { ENCHANT_LINE.find(it) }
				.associate { it.groups["name"]!!.value to it.groups["tier"]!!.value }
			if(lineEnchants.isEmpty()) {
				lastEnchant?.description?.add(line)
				continue
			}
			lineEnchants.forEach {
				val enchant = ParsedEnchant(
					item,
					it.key,
					it.value to it.value.romanToArabic(),
					Enchant.getByName(it.key),
				)
				enchants.add(enchant)
				lastEnchant = enchant
			}
		}

		// Start by removing everything up until the first blank line, or until we (somehow!?) consume everything
		while(!lines.getOrNull(firstEnchant)?.string.isNullOrBlank()) {
			lines.removeAt(firstEnchant)
		}

		// Then chunk the enchantments up, either into lines of 3 if >=5 enchants or the config option
		// is enabled (and the item isn't an enchanted book), otherwise give them their own lines
		val shouldCompact = when {
			enchants.size > 5 -> true
			config.alwaysCompact -> item.getSkyBlockItemId() != "ENCHANTED_BOOK" || enchants.size > 1
			else -> false
		}
		val enchantText: List<Text> = if(shouldCompact) {
			enchants.chunked(3) {
				Texts.join(it.flatMap { it.toText(false) }, Text.literal(", ").blue())
			}
		} else {
			enchants.flatMap { it.toText(true) }
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
		val tier = if(config.useRomanNumerals) second.first else second.second
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
				val progress = item.nbtCompound.copyNbt().getInt(enchant.nbtKey)
				val nextTier = enchant.tiers.lastOrNull { it >= progress }
				return buildText {
					append("(")
					append(progress.toAbbreviatedString(millionPrecision = 1))
					if(nextTier != null) {
						append("/${nextTier.toAbbreviatedString(millionPrecision = 1)}")
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
			if(includeStacking) {
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