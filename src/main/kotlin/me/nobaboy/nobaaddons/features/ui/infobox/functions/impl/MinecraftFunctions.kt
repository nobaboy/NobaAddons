package me.nobaboy.nobaaddons.features.ui.infobox.functions.impl

import me.nobaboy.nobaaddons.features.ui.infobox.functions.InfoBoxFunction
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.MCUtils.day
import me.nobaboy.nobaaddons.utils.PingUtils

object MinecraftFunctions {
	object PingFunction : InfoBoxFunction<Int> {
		override val name: String = "ping"
		override fun execute(): Int = PingUtils.ping
	}

	object FpsFunction : InfoBoxFunction<Int> {
		override val name: String = "fps"
		override fun execute(): Int = MCUtils.client.currentFps
	}

	object DayFunction : InfoBoxFunction<Long?> {
		override val name: String = "day"
		override fun execute(): Long? = MCUtils.world?.day
	}
}