package me.nobaboy.nobaaddons.features.chat.alerts

import me.nobaboy.nobaaddons.config.option.AbstractConfigOptionGroup
import me.nobaboy.nobaaddons.config.option.booleanController
import net.minecraft.text.Text

abstract class ChatAlert(id: String, private val name: Text) : AbstractConfigOptionGroup(id) {
	protected open val description: Text? = null

	var enabled by config(false) {
		name = this@ChatAlert.name
		description = this@ChatAlert.description
		booleanController()
	}

	abstract fun process(message: String)
}