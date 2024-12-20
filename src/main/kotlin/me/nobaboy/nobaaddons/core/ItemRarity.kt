package me.nobaboy.nobaaddons.core

import com.mojang.brigadier.context.CommandContext
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.title
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EnumArgumentType
import net.minecraft.util.StringIdentifiable

enum class ItemRarity(val color: NobaColor? = null) : StringIdentifiable {
	COMMON(NobaColor.WHITE),
	UNCOMMON(NobaColor.GREEN),
	RARE(NobaColor.BLUE),
	EPIC(NobaColor.DARK_PURPLE),
	LEGENDARY(NobaColor.GOLD),
	MYTHIC(NobaColor.LIGHT_PURPLE),
	DIVINE(NobaColor.AQUA),
	SPECIAL(NobaColor.RED),
	VERY_SPECIAL(NobaColor.RED),
	ULTIMATE(NobaColor.DARK_RED),
	ADMIN(NobaColor.RED),
	UNKNOWN;

	val formatting by lazy { color?.toFormatting() }
	val colorCode by lazy { color?.colorCode }

	fun isAtLeast(rarity: ItemRarity): Boolean = this.ordinal >= rarity.ordinal

	override fun asString(): String? = name
	override fun toString(): String = name.replace("_", " ").title()

	companion object {
		val CODEC = StringIdentifiable.createCodec { ItemRarity.entries.toTypedArray() }
		val RARITIES = entries.associateBy { it.name.replace("_", " ") }

		fun getRarity(text: String): ItemRarity = RARITIES.getOrDefault(text, UNKNOWN)
		fun getByColorCode(colorCode: Char): ItemRarity = entries.firstOrNull { it.colorCode == colorCode } ?: UNKNOWN
	}

	object ItemRarityArgumentType : EnumArgumentType<ItemRarity>(CODEC, { ItemRarity.entries.toTypedArray() }) {
		fun getItemRarity(context: CommandContext<out CommandSource>, id: String): ItemRarity {
			return context.getArgument(id, ItemRarity::class.java)
		}
	}
}