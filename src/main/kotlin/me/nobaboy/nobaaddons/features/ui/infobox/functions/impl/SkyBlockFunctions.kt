package me.nobaboy.nobaaddons.features.ui.infobox.functions.impl

import me.nobaboy.nobaaddons.api.skyblock.SkyBlockAPI
import me.nobaboy.nobaaddons.features.ui.infobox.functions.InfoBoxFunction

object SkyBlockFunctions {
	object LevelFunction : InfoBoxFunction<Int> {
		override val name: String = "level"
		override val aliases: List<String> = listOf("skyblock_level")
		override fun execute(): Int = SkyBlockAPI.level ?: 0
	}

	object LevelColorFunction : InfoBoxFunction<Char> {
		override val name: String = "level_color"
		override val aliases: List<String> = listOf("skyblock_level_color")
		override fun execute(): Char = SkyBlockAPI.getSkyBlockLevelColor().colorCode!!
	}

	object XPFunction : InfoBoxFunction<Int> {
		override val name: String = "xp"
		override val aliases: List<String> = listOf("skyblock_xp")
		override fun execute(): Int = SkyBlockAPI.xp ?: 0
	}

	object CoinsFunction : InfoBoxFunction<Long> {
		override val name: String = "coins"
		override val aliases: List<String> = listOf("skyblock_coins")
		override fun execute(): Long = SkyBlockAPI.coins ?: 0L
	}

	object BitsFunction : InfoBoxFunction<Long> {
		override val name: String = "bits"
		override val aliases: List<String> = listOf("skyblock_bits")
		override fun execute(): Long = SkyBlockAPI.bits ?: 0L
	}

	object ZoneFunction : InfoBoxFunction<String> {
		override val name: String = "zone"
		override val aliases: List<String> = listOf("skyblock_zone")
		override fun execute(): String = SkyBlockAPI.currentZone ?: "Unknown"
	}
}