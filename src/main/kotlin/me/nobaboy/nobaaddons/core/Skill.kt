package me.nobaboy.nobaaddons.core

import com.mojang.brigadier.context.CommandContext
import com.mojang.serialization.Codec
import net.minecraft.command.CommandSource
import net.minecraft.command.argument.EnumArgumentType
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

	object SkillArgumentType : EnumArgumentType<Skill>(CODEC, { entries.toTypedArray() }) {
		fun getItemRarity(context: CommandContext<out CommandSource>, id: String): Skill {
			return context.getArgument(id, Skill::class.java)
		}
	}
}