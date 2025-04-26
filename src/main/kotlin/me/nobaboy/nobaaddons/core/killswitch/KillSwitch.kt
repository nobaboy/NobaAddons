package me.nobaboy.nobaaddons.core.killswitch

import kotlin.reflect.KProperty

class KillSwitch(val feature: String) {
	fun get(): KillSwitchData? =
		KillSwitchData.INSTANCE?.get(feature)?.firstOrNull(KillSwitchData::isApplicable)

	val active: Boolean get() = get() != null

	@Suppress("unused")
	operator fun getValue(instance: Any?, property: KProperty<*>): Boolean = active
}