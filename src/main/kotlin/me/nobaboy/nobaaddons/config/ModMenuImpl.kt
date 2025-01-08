package me.nobaboy.nobaaddons.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import me.nobaboy.nobaaddons.screens.NobaMainScreen

class ModMenuImpl : ModMenuApi {
	override fun getModConfigScreenFactory() = ConfigScreenFactory(::NobaMainScreen)
}