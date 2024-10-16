package me.nobaboy.nobaaddons.utils

import com.mojang.brigadier.Command
import net.minecraft.client.gui.screen.Screen

object ScreenUtils {
	fun Screen.queueOpen(): Int {
		MCUtils.client.let {
			it.send {
				it.setScreen(this)
			}
		}
		return Command.SINGLE_SUCCESS
	}
}