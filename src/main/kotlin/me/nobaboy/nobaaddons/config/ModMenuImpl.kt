package me.nobaboy.nobaaddons.config

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi

class ModMenuImpl : ModMenuApi {
	override fun getModConfigScreenFactory(): ConfigScreenFactory<*>? {
		return ConfigScreenFactory(NobaConfig::getConfigScreen)
	}
}