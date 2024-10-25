package me.nobaboy.nobaaddons.features.chat.filters

import me.nobaboy.nobaaddons.utils.TextUtils.toText
import net.minecraft.text.Text
import net.minecraft.util.Formatting

enum class StatType(val identifier: String, val text: String, val color: Formatting) {
	STRENGTH("Strength", "❁ Strength", Formatting.RED),
	CRIT_DAMAGE("Crit Damage", "☠ Crit Damage", Formatting.BLUE),
	DEFENSE("Defense", "❈ Defense", Formatting.GREEN),
	DAMAGE("Damage", "❁ Damage", Formatting.RED),
	ABILITY_DAMAGE("Ability Damage", "๑ Ability Damage", Formatting.RED),
	HEALTH("HP", "❤ Health", Formatting.RED),
	HEALTH_REGEN("Health Regen", "❣ Health Regen", Formatting.RED),
	SPEED("Speed", "✦ Speed", Formatting.WHITE),
	INTELLIGENCE("Intelligence", "✎ Intelligence", Formatting.AQUA);

	fun toText(): Text = text.toText().formatted(color)
}