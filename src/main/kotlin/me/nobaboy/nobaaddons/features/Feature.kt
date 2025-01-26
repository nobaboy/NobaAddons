package me.nobaboy.nobaaddons.features

import me.nobaboy.nobaaddons.events.Listener
import net.minecraft.text.Text

abstract class Feature(
	val id: String,
	val category: FeatureCategory,
	val name: Text,
	private val enabledByDefault: Boolean = false,
) {
	private var listeners: List<Listener<*>>? = null
	val killswitch by KillSwitch(null)

	internal val config: AbstractFeatureConfig by lazy {
		val options = this::class.nestedClasses.firstOrNull { it.objectInstance is FeatureOptions }?.let {
			it.objectInstance as? FeatureOptions
		}

		if(options == null) {
			SimpleFeatureConfig(enabledByDefault)
		} else {
			FeatureConfig(enabledByDefault, options)
		}
	}

	open fun enable() {
		if(killswitch) return
		listeners = initListeners()
	}

	open fun disable() {
		listeners?.forEach { it.unsubscribe() }
	}

	// TODO this feels janky? i'm not sure how else to do something like this without just tearing
	//      apart the entire event system though
	protected abstract fun initListeners(): List<Listener<*>>
}