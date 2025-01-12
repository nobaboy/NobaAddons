package me.nobaboy.nobaaddons.features.rift

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.timestamp

class RiftTimerData : ObjectProperty<RiftTimerData>("rift") {
	var nextSplitSteal by Property.ofNullable("nextSplitSteal", serializer = Serializer.timestamp)
	var freeRiftInfusions by Property.of("freeInfusions", 3)
	var nextFreeInfusion by Property.ofNullable("nextFreeInfusion", serializer = Serializer.timestamp)
}