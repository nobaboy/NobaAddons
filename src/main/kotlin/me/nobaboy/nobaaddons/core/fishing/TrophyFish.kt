package me.nobaboy.nobaaddons.core.fishing

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Rarity.Companion.rarityFormatted
import me.nobaboy.nobaaddons.repo.Repo
import me.nobaboy.nobaaddons.utils.TextUtils.buildLiteral
import me.nobaboy.nobaaddons.utils.TextUtils.obfuscated
import net.minecraft.text.Text

@Serializable
data class TrophyFish(val id: String, val name: String, val rarity: Rarity) {
	val displayName: Text by lazy {
		buildLiteral(name) {
			if(id.startsWith("OBFUSCATED_")) obfuscated()
			rarityFormatted(rarity)
		}
	}

	companion object {
		val FISH by Repo.createList<TrophyFish>("fishing/trophy_fish.json")

		fun get(fish: String) = FISH?.firstOrNull { it.name == fish }
	}
}