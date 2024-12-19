package me.nobaboy.nobaaddons.core.fishing

import me.nobaboy.nobaaddons.core.ItemRarity
import net.minecraft.text.Text
import net.minecraft.util.Formatting

enum class TrophyFish(val fishName: String, val rarity: ItemRarity) {
	// Common
	SULPHUR_SKITTER("Sulphur Skitter", ItemRarity.COMMON),
	BLOBFISH("Blobfish", ItemRarity.COMMON),
	OBFUSCATED_1("Obfuscated 1", ItemRarity.COMMON),
	STEAMING_HOT_FLOUNDER("Steaming-Hot Flounder", ItemRarity.COMMON),
	GUSHER("Gusher", ItemRarity.COMMON),

	// Uncommon
	OBFUSCATED_2("Obfuscated 2", ItemRarity.UNCOMMON),
	SLUGFISH("Slugfish", ItemRarity.UNCOMMON),
	FLYFISH("Flyfish", ItemRarity.UNCOMMON),

	// Rare
	OBFUSCATED_3("Obfuscated 3", ItemRarity.RARE),
	VANILLE("Vanille", ItemRarity.RARE),
	LAVAHORSE("Lavahorse", ItemRarity.RARE),
	MANA_RAY("Mana Ray", ItemRarity.RARE),
	VOLCANIC_STONEFISH("Volcanic Stonefish", ItemRarity.RARE),

	// Epic
	SKELETON_FISH("Skeleton Fish", ItemRarity.EPIC),
	MOLDFIN("Moldfin", ItemRarity.EPIC),
	SOUL_FISH("Soul Fish", ItemRarity.EPIC),
	KARATE_FISH("Karate Fish", ItemRarity.EPIC),

	// Legendary
	GOLDEN_FISH("Golden Fish", ItemRarity.LEGENDARY),
	;

	val displayName: Text = Text.literal(fishName).formatted(rarity.formatting ?: Formatting.RESET)

	companion object {
		fun get(fish: String) = entries.firstOrNull { it.fishName == fish }
	}
}