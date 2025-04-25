package me.nobaboy.nobaaddons.core

import dev.isxander.yacl3.api.NameableEnum
import me.nobaboy.nobaaddons.utils.NobaColor
import net.minecraft.text.Text
import net.minecraft.util.Formatting

enum class SkyBlockStat(
	val id: String,
	private val statName: String,
	val icon: Char,
	val color: NobaColor,
	vararg val aliases: String
) : NameableEnum {
	HEALTH("health", "Health", '❤', NobaColor.RED, "HP"),
	DEFENSE("defense", "Defense", '❈', NobaColor.GREEN),
//	TRUE_DEFENCE("true_defense", "True Defense", '❂', NobaColor.WHITE),
	SPEED("speed", "Speed", '✦', NobaColor.AQUA),

	DAMAGE("damage", "Damage", '❁', NobaColor.RED),
	STRENGTH("strength", "Strength", '❁', NobaColor.RED),
	CRIT_CHANCE("crit_chance", "Crit Chance", '☣', NobaColor.BLUE),
	CRIT_DAMAGE("crit_damage", "Crit Damage", '☠', NobaColor.BLUE),
//	BONUS_ATTACK_SPEED("bonus_attack_speed", "Bonus Attack Speed", '⚔', NobaColor.YELLOW),

	INTELLIGENCE("intelligence", "Intelligence", '✎', NobaColor.AQUA),
	ABILITY_DAMAGE("ability_damage", "Ability Damage", '๑', NobaColor.RED),

	HEALTH_REGEN("health_regen", "Health Regen", '❣', NobaColor.RED),
//	VITALITY("vitality", "Vitality", '♨', NobaColor.DARK_RED),
//	MENDING("mending", "Mending", '☄', NobaColor.GREEN),

	FISHING_SPEED("fishing_speed", "Fishing Speed", '☂', NobaColor.AQUA),
	SEA_CREATURE_CHANCE("sea_creature_chance", "Sea Creature Chance", 'α', NobaColor.DARK_AQUA),
	DOUBLE_HOOK_CHANCE("double_hook_chance", "Double Hook Chance", '⚓', NobaColor.BLUE),
	TROPHY_FISH_CHANCE("trophy_fish_chance", "Trophy Fish Chance", '♔', NobaColor.GOLD),
	TREASURE_CHANCE("treasure_chance", "Treasure Chance", '⛃', NobaColor.GOLD),
	;

	val prefixedName: String = "$icon $statName"

	val formatting: Formatting? by lazy { color.formatting }

	override fun getDisplayName(): Text = Text.literal(prefixedName).formatted(formatting)

	companion object {
		fun getByName(name: String): SkyBlockStat? = entries.firstOrNull { name.contains(it.statName, ignoreCase = true) }
	}
}