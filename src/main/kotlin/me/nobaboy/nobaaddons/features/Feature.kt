package me.nobaboy.nobaaddons.features

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.config.option.AbstractVersionedConfigOptionGroup
import me.nobaboy.nobaaddons.config.option.ConfigOption
import me.nobaboy.nobaaddons.events.AbstractEventDispatcher
import me.nobaboy.nobaaddons.events.Event
import me.nobaboy.nobaaddons.events.EventListener
import net.minecraft.text.Text
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberFunctions
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.jvm.isAccessible
import kotlin.reflect.jvm.jvmErasure

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
		for(function in this::class.memberFunctions) {
			function.isAccessible = true
			if(!function.hasAnnotation<EventListener>()) {
				continue
			}

			val eventClass = function.parameters.first { it.type.isSubtypeOf(Event::class.starProjectedType) }.type.jvmErasure
			val dispatcher = eventClass.companionObjectInstance as AbstractEventDispatcher<*, *>
			dispatcher.registerFunction(function, this)
		}
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