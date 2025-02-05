package me.nobaboy.nobaaddons.features

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.option.AbstractConfigOptionHolder
import me.nobaboy.nobaaddons.events.AbstractEventDispatcher
import me.nobaboy.nobaaddons.events.Event
import net.minecraft.text.Text

abstract class Feature(id: String, val name: Text, val category: FeatureCategory) : AbstractConfigOptionHolder(id) {
	val killSwitch by KillSwitch(null)

	/**
	 * Registers the given [listener] on the given [dispatcher], only invoking it if this feature's [killSwitch] hasn't
	 * been activated.
	 */
	protected fun <T : Event> listen(
		dispatcher: AbstractEventDispatcher<T, *>,
		featureKillSwitch: () -> Boolean = { false },
		listener: (T) -> Unit
	) {
		dispatcher.register {
			if(!killSwitch && !featureKillSwitch()) listener(it)
		}
	}

	/**
	 * Implement your feature's initialization logic here.
	 *
	 * Make sure you use [listen] for any events, or otherwise ensure you're checking [killSwitch].
	 */
	open fun init() {
	}

	/**
	 * Implementation providing a YACL option group; by default, this automatically generates an option group
	 * based on your [config] properties.
	 */
	override fun buildConfig(category: ConfigCategory.Builder) {
		if(killSwitch) {
			return
		}

		val options = options.values.mapNotNull { it.yaclOption }
		if(options.isEmpty()) {
			return
		}

		category.group(OptionGroup.createBuilder().apply {
			name(name)
			collapsed(true)
			options(options)
		}.build())
	}
}