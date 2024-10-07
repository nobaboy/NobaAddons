package me.nobaboy.nobaaddons.utils

import net.minecraft.util.Util

object OSUtils {
	fun browse(url: String) {
		Util.getOperatingSystem().open(url)
	}
}