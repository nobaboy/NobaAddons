package me.nobaboy.nobaaddons.features.ui.infobox.functions.impl

import me.nobaboy.nobaaddons.features.ui.infobox.functions.InfoBoxFunction
import me.nobaboy.nobaaddons.utils.mc.MCUtils
import me.nobaboy.nobaaddons.utils.mc.MCUtils.day
import me.nobaboy.nobaaddons.utils.mc.PingUtils

object MinecraftFunctions {
	object PingFunction : InfoBoxFunction<Int> {
		override val name: String = "ping"
		override fun execute(): Int = PingUtils.ping
	}

	object TpsFunction : InfoBoxFunction<Double> {
		override val name: String = "tps"
		override fun execute(): Double = PingUtils.averageTps
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