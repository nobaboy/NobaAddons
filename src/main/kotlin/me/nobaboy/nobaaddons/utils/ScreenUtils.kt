package me.nobaboy.nobaaddons.utils

import com.mojang.brigadier.Command
import me.nobaboy.nobaaddons.NobaAddons
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.client.gui.screen.Screen

object ScreenUtils {
	fun Command<FabricClientCommandSource>.queueOpenScreen(screen: Screen): Command<FabricClientCommandSource> {
		return Command { screen.queueOpen() }
	}

	fun Screen.queueOpen(): Int {
		NobaAddons.mc.send {
			NobaAddons.mc.setScreen(this)
		}
		return Command.SINGLE_SUCCESS
	}
}