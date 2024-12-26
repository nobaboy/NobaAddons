package me.nobaboy.nobaaddons.core.fishing

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Rarity.Companion.rarityFormatted
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text

@Serializable
data class TrophyFish(val id: String, val name: String, val rarity: Rarity) {
	val displayName: Text by lazy { name.toText().rarityFormatted(rarity) }

	companion object {
		val FISH by Repo.createList("fishing/trophy_fish.json", serializer())

		fun get(fish: String) = FISH.firstOrNull { it.name == fish }
	}
}