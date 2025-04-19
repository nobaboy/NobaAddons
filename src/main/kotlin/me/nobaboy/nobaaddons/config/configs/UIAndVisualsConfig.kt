package me.nobaboy.nobaaddons.config.configs

import dev.celestialfault.histoire.Object
import me.nobaboy.nobaaddons.utils.NobaColor

class UIAndVisualsConfig {
	var renderInfoBoxesOutsideSkyBlock = false

	@Object val temporaryWaypoints = TemporaryWaypoints()
	@Object val etherwarpOverlay = EtherwarpOverlay()
	@Object val renderingTweaks = RenderingTweaks()
	@Object val swingAnimation = SwingAnimation()
	@Object val itemPosition = FirstPersonItemPosition()

	class TemporaryWaypoints {
		var enabled = false
		var waypointColor = NobaColor.YELLOW
		var expirationTime = 30
	}

	class EtherwarpOverlay {
		var enabled = false
		var highlightColor = NobaColor.BLUE
		var failHighlightColor = NobaColor.GRAY
		var showFailText = false
		var allowOverlayOnAir = false
	}

	class RenderingTweaks {
		var hideLightningBolt = false
		var removeFrontFacingThirdPerson = false
		var fixEnchantedArmorGlint = false
		var removeArmorGlints = false
		var hideAbsorptionHearts = false
		var hideAirBubbles = false
	}

	class SwingAnimation {
		var swingDuration = 1
		var applyToAllPlayers = false
		var staticSwingPosition = false
	}

	class FirstPersonItemPosition {
		var cancelEquipAnimation = false
		var cancelItemUpdateAnimation = false
		var cancelDrinkAnimation = false
		var x = 0
		var y = 0
		var z = 0
		var scale = 1f
	}
}