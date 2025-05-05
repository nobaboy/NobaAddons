package me.nobaboy.nobaaddons.utils.mc

import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.screen.ConfirmScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.screen.ScreenTexts

object ScreenUtils {
	val client by MCUtils::client

	fun Screen.queueOpen() {
		MCUtils.client.let {
			it.send {
				it.setScreen(this)
			}
		}
	}

	fun confirmClose(screen: Screen, onConfirm: () -> Unit) {
		client.setScreen(ConfirmScreen(
			{ confirmed ->
				if(confirmed) {
					onConfirm()
				} else {
					client.setScreen(screen)
				}
			},
			tr("nobaaddons.screen.confirmClose", "Cancel Changes"),
			tr(
				"nobaaddons.screen.confirmClose.message",
				"You have unsaved changes; exiting now will discard them, would you like to continue?"
			),
			ScreenTexts.YES,
			ScreenTexts.NO
		))
	}
}