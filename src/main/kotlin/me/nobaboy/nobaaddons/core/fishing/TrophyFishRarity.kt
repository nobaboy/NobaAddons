package me.nobaboy.nobaaddons.core.fishing

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text
import net.minecraft.util.Formatting

enum class TrophyFishRarity(val color: NobaColor, val pityAt: Int?) : NameableEnum {
	BRONZE(NobaColor.DARK_GRAY, null),
	SILVER(NobaColor.GRAY, null),
	GOLD(NobaColor.GOLD, 100),
	DIAMOND(NobaColor.AQUA, 600);

	override fun getDisplayName(): Text = when(this) {
		BRONZE -> tr("nobaaddons.label.trophyFishRarity.bronze", "BRONZE")
		SILVER -> tr("nobaaddons.label.trophyFishRarity.silver", "SILVER")
		GOLD -> tr("nobaaddons.label.trophyFishRarity.gold", "GOLD")
		DIAMOND -> tr("nobaaddons.label.trophyFishRarity.diamond", "DIAMOND")
	}.formatted(formatting, Formatting.BOLD)

	val formatting = color.formatting!!

	companion object {
		fun get(rarity: String) = entries.firstOrNull { it.name.equals(rarity, ignoreCase = true) }
	}
}