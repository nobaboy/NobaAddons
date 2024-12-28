package me.nobaboy.nobaaddons.core.enchants

import kotlinx.serialization.Serializable
import me.nobaboy.nobaaddons.core.SkyBlockItemType

interface EnchantBase {
	val id: String
	val name: String
	val good: Int
	val max: Int
	val items: List<SkyBlockItemType>?
	val exclusiveToItems: List<String>?
	val conflicts: List<String>?
	val abbreviation: String?
}

@Serializable
data class StandardEnchant(
	override val id: String,
	override val name: String,
	override val good: Int,
	override val max: Int,
	override val items: List<SkyBlockItemType>? = null,
	override val exclusiveToItems: List<String>? = null,
	override val conflicts: List<String>? = null,
	override val abbreviation: String? = null,
) : EnchantBase

@Serializable
data class StackingEnchant(
	override val id: String,
	override val name: String,
	val nbtKey: String,
	val tiers: List<Int>,
	override val items: List<SkyBlockItemType>? = null,
	override val exclusiveToItems: List<String>? = null,
	override val conflicts: List<String>? = null,
	override val abbreviation: String? = null
) : EnchantBase {
	override val good: Int = -1
	override val max: Int = tiers.size + 1
}

@Serializable
data class UltimateEnchant(
	override val id: String,
	override val name: String,
	override val max: Int,
	override val items: List<SkyBlockItemType>? = null,
	override val exclusiveToItems: List<String>? = null,
	override val conflicts: List<String>? = null,
	override val abbreviation: String? = null
) : EnchantBase {
	override val good: Int = -1
}