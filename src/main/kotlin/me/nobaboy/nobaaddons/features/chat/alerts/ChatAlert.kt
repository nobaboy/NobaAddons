package me.nobaboy.nobaaddons.features.chat.alerts

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.option.AbstractConfigOptionHolder
import me.nobaboy.nobaaddons.config.option.booleanController
import net.minecraft.text.Text

abstract class ChatAlert(id: String, private val name: Text) : AbstractConfigOptionHolder(id) {
	protected open val description: Text? = null

	var enabled by config(false) {
		name = this@ChatAlert.name
		description = this@ChatAlert.description
		booleanController()
	}

	abstract fun process(message: String)

	override fun buildConfig(category: ConfigCategory.Builder) {
		throw UnsupportedOperationException()
	}
}