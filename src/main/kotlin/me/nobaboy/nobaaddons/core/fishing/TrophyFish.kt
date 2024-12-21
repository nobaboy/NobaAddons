package me.nobaboy.nobaaddons.core.fishing

import me.nobaboy.nobaaddons.core.ItemRarity
import me.nobaboy.nobaaddons.repo.RepoObjectArray.Companion.listFromRepository
import me.nobaboy.nobaaddons.utils.TextUtils.formatted
import net.minecraft.text.Text
import net.minecraft.util.Formatting

data class TrophyFish(val id: String, val name: String, val rarity: ItemRarity) {
	val displayName: Text get() = name.formatted(rarity.formatting ?: Formatting.RESET)

	companion object {
		val FISH by TrophyFish::class.listFromRepository("fishing/trophy_fish.json")

		fun get(fish: String) = FISH.firstOrNull { it.name == fish }
	}
}