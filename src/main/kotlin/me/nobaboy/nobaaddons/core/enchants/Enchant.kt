package me.nobaboy.nobaaddons.core.enchants

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.repo.Repo

@Serializable
data class Enchant(
	val standard: List<StandardEnchant>,
	val stacking: List<StackingEnchant>,
	val ultimate: List<UltimateEnchant>
) {
	val all: List<EnchantBase> by lazy { standard + stacking + ultimate }

	companion object {
		val ENCHANTS by Repo.create<Enchant>("item_modifiers/enchants.json")

		fun getById(id: String): EnchantBase? = ENCHANTS?.all?.firstOrNull { it.id == id }
		fun getByName(name: String): EnchantBase? = ENCHANTS?.all?.firstOrNull { it.name == name }
	}
}