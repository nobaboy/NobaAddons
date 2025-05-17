package me.nobaboy.nobaaddons.core

import com.mojang.serialization.Codec
import net.minecraft.util.StringIdentifiable

enum class Skill(val isCosmetic: Boolean = false, val includeInSkillAverage: Boolean = !isCosmetic) : StringIdentifiable {
	FARMING,
	MINING,
	COMBAT,
	FORAGING,
	FISHING,
	ENCHANTING,
	ALCHEMY,
	TAMING,
	CARPENTRY,
	RUNECRAFTING(isCosmetic = true),
	SOCIAL(isCosmetic = true),
	;

	override fun asString(): String = name

	companion object {
		val CODEC: Codec<Skill> = StringIdentifiable.createCodec { entries.toTypedArray() }
	}
}