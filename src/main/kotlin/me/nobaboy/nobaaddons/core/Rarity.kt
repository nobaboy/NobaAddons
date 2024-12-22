package me.nobaboy.nobaaddons.core

import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.Codec
import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.StringUtils.title
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EnumArgumentType
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.StringIdentifiable

enum class Rarity(val color: NobaColor? = null) : StringIdentifiable, NameableEnum {
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

	val formatting: Formatting? by lazy { color?.toFormatting() }
	val colorCode: Char? by lazy { color?.colorCode }

	override fun getDisplayName(): Text = Text.literal(name).rarityFormatted(this)
	override fun asString(): String = name
	override fun toString(): String = name.replace("_", " ").title()

	companion object {
		val CODEC: Codec<Rarity> = StringIdentifiable.createCodec { Rarity.entries.toTypedArray() }
		val RARITIES: Map<String, Rarity> = entries.associateBy { it.name.replace("_", " ") }

		fun getRarity(text: String): Rarity = RARITIES.getOrDefault(text, UNKNOWN)
		fun getByColorCode(colorCode: Char): Rarity = entries.firstOrNull { it.colorCode == colorCode } ?: UNKNOWN

		fun MutableText.rarityFormatted(rarity: Rarity): MutableText = apply {
			if(rarity.formatting != null) formatted(rarity.formatting)
		}

		fun ClosedRange<Rarity>.toArray() =
			Rarity.entries.filter { it >= this.start && it <= this.endInclusive }.toTypedArray()
	}

	object RarityArgumentType : EnumArgumentType<Rarity>(CODEC, { Rarity.entries.toTypedArray() }) {
		fun getItemRarity(context: CommandContext<out CommandSource>, id: String): Rarity {
			return context.getArgument(id, Rarity::class.java)
		}
	}
}