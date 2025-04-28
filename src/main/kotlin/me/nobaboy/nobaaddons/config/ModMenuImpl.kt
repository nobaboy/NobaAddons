package me.nobaboy.nobaaddons.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.nobaboy.nobaaddons.screens.NobaMainScreen
import net.minecraft.client.gui.screen.Screen

class ModMenuImpl : ModMenuApi {
	override fun getModConfigScreenFactory() = ConfigScreenFactory {
		if(Screen.hasShiftDown()) NobaConfig.getConfigScreen(it) else NobaMainScreen(it)
	}
}