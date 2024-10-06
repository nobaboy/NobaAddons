package me.nobaboy.nobaaddons.features.chat.filter

import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

enum class StatType(val identifiers: List<String>, val text: String, val color: Formatting) {
	STRENGTH(listOf("Strength"), "❁ Strength", Formatting.RED),
	CRIT_DAMAGE(listOf("Crit Damage"), "☠ Crit Damage", Formatting.BLUE),
	DEFENSE(listOf("Defense", "Defence"), "❈ Defense", Formatting.GREEN),
	DAMAGE(listOf("Damage"), "❁ Damage", Formatting.RED),
	ABILITY_DAMAGE(listOf("Ability Damage"), "๑ Ability Damage", Formatting.RED),
	HEALTH(listOf("Health", "HP"), "❤ Health", Formatting.RED),
	HEALTH_REGEN(listOf("Health Regen"), "❣ Health Regen", Formatting.RED),
	SPEED(listOf("Speed"), "✦ Speed", Formatting.WHITE),
	INTELLIGENCE(listOf("Intelligence"), "✎ Intelligence", Formatting.AQUA);

	fun toText(): Text = text.toText().formatted(color)
}