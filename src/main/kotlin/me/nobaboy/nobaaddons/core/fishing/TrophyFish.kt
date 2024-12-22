package me.nobaboy.nobaaddons.core.fishing

import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Rarity.Companion.rarityFormatted
import me.nobaboy.nobaaddons.repo.RepoObjectArray.Companion.listFromRepository
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text

data class TrophyFish(val id: String, val name: String, val rarity: Rarity) {
	val displayName: Text by lazy { name.toText().rarityFormatted(rarity) }

	companion object {
		val FISH by TrophyFish::class.listFromRepository("fishing/trophy_fish.json")

		fun get(fish: String) = FISH.firstOrNull { it.name == fish }
	}
}