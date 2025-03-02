package me.nobaboy.nobaaddons.features.rift

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.timestamp

class RiftTimerData : ObjectProperty<RiftTimerData>("riftTimers") {
	var freeRiftInfusions by Property.of("freeInfusions", 3)
	var nextFreeInfusion by Property.ofNullable("nextFreeInfusion", Serializer.timestamp)
	var nextSplitSteal by Property.ofNullable("nextSplitSteal", Serializer.timestamp)
}