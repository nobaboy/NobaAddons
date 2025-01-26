package me.nobaboy.nobaaddons.features

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property

internal sealed class AbstractFeatureConfig(enableByDefault: Boolean = false) : ObjectProperty<FeatureConfig>("") {
	var enabled by Property.of("enabled", enableByDefault)
}

internal class SimpleFeatureConfig(enableByDefault: Boolean = false) : AbstractFeatureConfig(enableByDefault)

internal class FeatureConfig(enableByDefault: Boolean = false, options: FeatureOptions) : AbstractFeatureConfig(enableByDefault) {
	val options by options
}

abstract class FeatureOptions : ObjectProperty<FeatureOptions>("config")