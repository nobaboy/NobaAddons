package me.nobaboy.nobaaddons.features

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import me.nobaboy.nobaaddons.events.Listener
import net.minecraft.text.Text

abstract class Feature(
	val id: String,
	val name: Text,
	enabledByDefault: Boolean? = null,
	val hidden: Boolean = false,
) : ObjectProperty<Feature>(id) {
	init {
		FeatureManager.FEATURES.add(this)
	}

	private var loaded = false
	var enabled by Property.of("enabled", enabledByDefault ?: (hidden == true))

	private var listeners: List<Listener<*>>? = null
	val killswitch: FeatureKillSwitch? get() = FeatureManager.getKillswitch(id)

	open fun enable() {
		if(killswitch?.isApplicable == true) return
		if(!loaded) {
			FeatureManager.CONFIG[id]?.let(::load)
			loaded = true
		}
		listeners = initListeners()
	}

	open fun disable() {
		listeners?.forEach { it.unsubscribe() }
	}

	// TODO this feels janky? i'm not sure how else to do something like this without just tearing
	//      apart the entire event system though
	protected abstract fun initListeners(): List<Listener<*>>
}