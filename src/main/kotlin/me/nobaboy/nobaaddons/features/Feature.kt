package me.nobaboy.nobaaddons.features

import dev.celestialfault.celestialconfig.Property
import me.nobaboy.nobaaddons.events.Listener
import net.minecraft.text.Text

abstract class Feature(
	val id: String,
	val category: FeatureCategory,
	val name: Text,
	enabledByDefault: Boolean = false,
) {
	var enabled by Property.of("enabled", enabledByDefault)

	private var listeners: List<Listener<*>>? = null
	val killswitch: FeatureKillSwitch? get() = FeatureManager.getKillSwitch(id)

	abstract val config: FeatureConfig?

	open fun enable() {
		if(killswitch?.isApplicable == true) return
		listeners = initListeners()
	}

	open fun disable() {
		listeners?.forEach { it.unsubscribe() }
	}

	// TODO this feels janky? i'm not sure how else to do something like this without just tearing
	//      apart the entire event system though
	protected abstract fun initListeners(): List<Listener<*>>
}