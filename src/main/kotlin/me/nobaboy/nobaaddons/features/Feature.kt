package me.nobaboy.nobaaddons.features

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.option.AbstractVersionedConfigOptionGroup
import me.nobaboy.nobaaddons.config.option.ConfigOption
import me.nobaboy.nobaaddons.events.AbstractEventDispatcher
import me.nobaboy.nobaaddons.events.Event
import net.minecraft.text.Text

abstract class Feature(id: String, val name: Text, val category: FeatureCategory) : AbstractVersionedConfigOptionGroup(id) {
	@Deprecated("")
	protected fun <T : Event> listen(
		dispatcher: AbstractEventDispatcher<T, *>,
		listener: (T) -> Unit
	) {
		dispatcher.register(listener)
	}

	/**
	 * Implement your feature's initialization logic here
	 */
	open fun init() {
	}

	/**
	 * Implementation providing a YACL option group; by default, this automatically generates an option group
	 * based on your [config] properties.
	 */
	override fun buildConfig(category: ConfigCategory.Builder) {
		deepBuildYaclOptions()
		val options = options.values.mapNotNull { (it as? ConfigOption<*>)?.yaclOption }
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