package me.nobaboy.nobaaddons.config.util.builders

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionEventListener
import dev.isxander.yacl3.api.controller.ControllerBuilder
import me.nobaboy.nobaaddons.config.AbstractNobaConfig
import me.nobaboy.nobaaddons.config.util.OptionRequirement
import me.nobaboy.nobaaddons.config.util.OptionRequirementFactory
import me.nobaboy.nobaaddons.config.util.require
import me.nobaboy.nobaaddons.core.killswitch.KillSwitch
import me.nobaboy.nobaaddons.utils.TextUtils.bold
import me.nobaboy.nobaaddons.utils.TextUtils.italic
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

/**
 * Generic config option builder
 */
class OptionBuilder<T> @PublishedApi internal constructor(val binding: Binding<T>) {
	lateinit var name: Text
	var description: OptionDescription? = null

	lateinit var controller: (Option<T>) -> ControllerBuilder<T>

	var requirement: OptionRequirement? = null
	var killSwitch: KillSwitch? = null

	fun require(builder: OptionRequirementFactory.() -> OptionRequirement) {
		this.requirement = builder(OptionRequirementFactory)
	}

	val updateListeners: MutableList<OptionEventListener<T>> = mutableListOf()

	fun onUpdate(listener: OptionEventListener<T>) {
		updateListeners.add(listener)
	}

	@PublishedApi
	internal fun build(): Option<T> {
		val option = Option.createBuilder<T>().apply {
			name(name)
			binding(binding)
			controller(controller)

			if(killSwitch?.active == true) {
				available(false)
				val originalDescription = description
				description = OptionDescription.createBuilder().apply {
					originalDescription?.let { text(it.text(), Text.empty()) }
					text(tr("nobaaddons.feature.killSwitch", "This feature has been remotely disabled:").bold().red())
					text((killSwitch?.get()?.reason?.toText() ?: tr("nobaaddons.feature.killSwitch.noReason", "No reason specified").italic()).yellow())
				}.build()
			}

			description?.let(::description)
		}.build()

		updateListeners.forEach(option::addEventListener)

		if(killSwitch?.active != true) {
			requirement?.let(option::require)
		}

		return option
	}

	companion object {
		lateinit var defaults: AbstractNobaConfig

		/**
		 * Convenience parameter wrapping the provided [Text] in an [OptionDescription]
		 */
		var OptionBuilder<*>.descriptionText: Text?
			get() = description?.text()
			set(value) { description = value?.let { OptionDescription.of(it) } }
	}
}
