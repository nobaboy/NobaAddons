package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.OptionFlag
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.client.gui.screen.ConfirmScreen
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.screen.ScreenTexts

// hijacking the ConfirmScreen implementation ~~for fame and profit~~ so that i dont need to write this screen myself
private class WorldSwitchRequired(private val parent: Screen?) : ConfirmScreen(
	{},
	tr("nobaaddons.screen.relogRequired", "Config option requires relog!").red().bold(),
	tr("nobaaddons.screen.relogRequired.text", "One or more options that you reconnect or switch islands to apply the changes"),
	ScreenTexts.EMPTY,
	ScreenTexts.EMPTY,
) {
	override fun addButtons(y: Int) {
		addButton(ButtonWidget.builder(ScreenTexts.OK) { MCUtils.client.setScreen(parent) }.dimensions(this.width / 2 - 155, y, 300, 20).build())
	}
}

private inline fun openScreenFlag(crossinline factory: (Screen?) -> Screen) =
	OptionFlag { MCUtils.client.setScreen(factory(MCUtils.client.currentScreen)) }

/**
 * Adds an [OptionFlag] telling the user that they must relog or switch islands to apply the changes
 *
 * This method does nothing if the user is not currently in a world.
 */
fun OptionBuilder<*>.worldSwitchRequired() {
	if(MCUtils.world == null) return
	flags.add(openScreenFlag(::WorldSwitchRequired))
}
