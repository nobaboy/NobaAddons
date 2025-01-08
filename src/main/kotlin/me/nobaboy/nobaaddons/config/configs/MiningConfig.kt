package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.color
import me.nobaboy.nobaaddons.utils.sound.NotificationSound

class MiningConfig : ObjectProperty<MiningConfig>("mining") {
	val wormAlert by WormAlert()
	val glaciteMineshaft by GlaciteMineshaft()

	class WormAlert : ObjectProperty<WormAlert>("wormAlert") {
		var enabled by Property.of<Boolean>("enabled", false)
		var alertColor by Property.of("alertColor", Serializer.color, NobaColor.YELLOW)
		var notificationSound by Property.of("notificationSound", Serializer.enum(), NotificationSound.DING)
	}

	class GlaciteMineshaft : ObjectProperty<GlaciteMineshaft>("glaciteMineshaft") {
		var corpseLocator by Property.of<Boolean>("corpseLocator", false)
		var autoShareCorpses by Property.of<Boolean>("autoShareCorpses", false)
		var entranceWaypoint by Property.of<Boolean>("entranceWaypoint", false)
		var ladderWaypoint by Property.of<Boolean>("ladderWaypoint", false)
	}
}