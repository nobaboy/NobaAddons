package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry

class UserInterfaceConfig {
	@SerialEntry
	val infoBoxes: MutableList<String> = mutableListOf()
}