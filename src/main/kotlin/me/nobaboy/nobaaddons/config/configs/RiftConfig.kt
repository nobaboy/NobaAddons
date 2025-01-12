package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.features.rift.RiftWarpTarget

class RiftConfig : ObjectProperty<RiftConfig>("rift") {
	var splitStealAlert by Property.of("splitStealAlert", false)
	var freeInfusionAlert by Property.of("freeInfusionAlert", false)
	var warpTarget by Property.of("warpTarget", Serializer.enum(), RiftWarpTarget.WIZARD_TOWER)
}