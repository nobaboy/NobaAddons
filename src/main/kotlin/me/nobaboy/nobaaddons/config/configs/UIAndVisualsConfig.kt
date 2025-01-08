package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.celestialconfig.ObjectProperty
import dev.celestialfault.celestialconfig.Property
import dev.celestialfault.celestialconfig.Serializer
import me.nobaboy.nobaaddons.utils.serializers.ExtraSerializers.color
import me.nobaboy.nobaaddons.utils.NobaColor

class UIAndVisualsConfig : ObjectProperty<UIAndVisualsConfig>("uiAndVisuals") {
	val temporaryWaypoints by TemporaryWaypoints()
	val etherwarpHelper by EtherwarpHelper()
	val renderingTweaks by RenderingTweaks()
	val swingAnimation by SwingAnimation()
	val itemPosition by FirstPersonItemPosition()

	class TemporaryWaypoints : ObjectProperty<TemporaryWaypoints>("temporaryWaypoints") {
		var enabled by Property.of<Boolean>("enabled", false)
		var waypointColor by Property.of("waypointColor", Serializer.color, NobaColor.YELLOW.toColor())
		var expirationTime by Property.of<Int>("expirationTime", 30)
	}

	class EtherwarpHelper : ObjectProperty<EtherwarpHelper>("etherwarpHelper") {
		var enabled by Property.of<Boolean>("enabled", false)
		var highlightColor by Property.of("highlightColor", Serializer.color, NobaColor.BLUE.toColor())
		var showFailText by Property.of<Boolean>("showFailText", false)
		var allowOverlayOnAir by Property.of<Boolean>("allowOverlayOnAir", false)
	}

	class RenderingTweaks : ObjectProperty<RenderingTweaks>("renderingTweaks") {
		var hideOtherPeopleFishing by Property.of<Boolean>("hideOtherPeopleFishing", false)
		var hideLightningBolt by Property.of<Boolean>("hideLightningBolt", false)
		var removeFrontFacingThirdPerson by Property.of<Boolean>("removeFrontFacingThirdPerson", false)
		var fixEnchantedArmorGlint by Property.of<Boolean>("fixEnchantedArmorGlint", false)
		var removeArmorGlints by Property.of<Boolean>("removeArmorGlints", false)
	}

	class SwingAnimation : ObjectProperty<SwingAnimation>("swingAnimation") {
		var swingDuration by Property.of<Int>("swingDuration", 1)
		var applyToAllPlayers by Property.of<Boolean>("applyToAllPlayers", false)
	}

	class FirstPersonItemPosition : ObjectProperty<FirstPersonItemPosition>("itemPosition") {
		var cancelEquipAnimation by Property.of<Boolean>("applyToAllPlayers", false)
		var cancelItemUpdateAnimation by Property.of<Boolean>("cancelItemUpdateAnimation", false)
		var cancelDrinkAnimation by Property.of<Boolean>("cancelDrinkAnimation", false)
		var x by Property.of("x", Serializer.number<Int>(min = -150, max = 150), 0)
		var y by Property.of("y", Serializer.number<Int>(min = -150, max = 150), 0)
		var z by Property.of("z", Serializer.number<Int>(min = -150, max = 50), 0)
		var scale by Property.of("scale", Serializer.number<Float>(min = 0.1f, max = 2f), 1f)
	}
}