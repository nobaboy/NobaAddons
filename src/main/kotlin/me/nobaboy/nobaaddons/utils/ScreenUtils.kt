package me.nobaboy.nobaaddons.utils

import net.minecraft.client.gui.screen.Screen

object ScreenUtils {
	fun Screen.queueOpen() {
		MCUtils.client.let {
			it.send {
				it.setScreen(this)
			}
		}
	}
}