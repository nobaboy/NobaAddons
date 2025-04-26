package me.nobaboy.nobaaddons.features

import me.nobaboy.nobaaddons.core.killswitch.KillSwitch
import net.minecraft.text.Text
import kotlin.reflect.jvm.isAccessible

abstract class AbstractFeature(featureId: String, val name: Text, val description: Text? = null) {
	val killSwitch by KillSwitch(featureId)

	init {
		if(!killSwitch) {
			val declaration = FeatureDeclaration(this)
			declaration.declare()
		}
	}

	/**
	 * Declare any event listeners that this feature needs; this could also be interpreted as a general setup method.
	 */
	protected abstract fun FeatureDeclaration.declare()

	companion object {
		fun AbstractFeature.getKillSwitch(): KillSwitch = this::killSwitch.let {
			it.isAccessible = true
			it.getDelegate() as KillSwitch
		}
	}
}