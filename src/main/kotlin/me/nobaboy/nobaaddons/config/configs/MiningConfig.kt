package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property

class MiningConfig : ObjectProperty<MiningConfig>("mining") {
	val glaciteMineshaft by GlaciteMineshaft()

	class GlaciteMineshaft : ObjectProperty<GlaciteMineshaft>("glaciteMineshaft") {
		var corpseLocator by Property.of<Boolean>("corpseLocator", false)
		var autoShareCorpses by Property.of<Boolean>("autoShareCorpses", false)
		var entranceWaypoint by Property.of<Boolean>("entranceWaypoint", false)
		var ladderWaypoint by Property.of<Boolean>("ladderWaypoint", false)
	}
}