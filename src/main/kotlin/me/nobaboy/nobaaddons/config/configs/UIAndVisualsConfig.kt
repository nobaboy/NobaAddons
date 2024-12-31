package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.utils.NobaColor
import java.awt.Color

class UIAndVisualsConfig {
	@SerialEntry
	val temporaryWaypoints: TemporaryWaypoints = TemporaryWaypoints()

	@SerialEntry
	val etherwarpHelper: EtherwarpHelper = EtherwarpHelper()

	@SerialEntry
	val renderingTweaks: RenderingTweaks = RenderingTweaks()

	@SerialEntry
	val swingAnimation = SwingAnimation()

	@SerialEntry
	val itemPosition = FirstPersonItemPosition()

	class TemporaryWaypoints {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var waypointColor: Color = NobaColor.YELLOW.toColor()

		@SerialEntry
		var expirationTime: Int = 15
	}

	class EtherwarpHelper {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var highlightColor: Color = NobaColor.BLUE.toColor()

		@SerialEntry
		var showFailText: Boolean = false

		@SerialEntry
		var allowOverlayOnAir: Boolean = false
	}

	class RenderingTweaks {
		@SerialEntry
		var hideOtherPeopleFishing: Boolean = false

		@SerialEntry
		var hideLightningBolt: Boolean = false

		@SerialEntry
		var removeFrontFacingThirdPerson: Boolean = false

		@SerialEntry
		var fixEnchantedArmorGlint: Boolean = false

		@SerialEntry
		var removeArmorGlints: Boolean = false
	}

	class SwingAnimation {
		@SerialEntry
		var swingDuration: Int = 1

		@SerialEntry
		var applyToAllPlayers: Boolean = false
	}

	class FirstPersonItemPosition {
		@SerialEntry
		var scale: Float = 1f // [0.1f, 2f]

		@SerialEntry
		var x: Int = 0 // [-150, 150]

		@SerialEntry
		var y: Int = 0 // [-150, 150]

		@SerialEntry
		var z: Int = 0 // [-150, 50]

		@SerialEntry
		var cancelEquipAnimation: Boolean = false

		@SerialEntry
		var cancelItemUpdateAnimation: Boolean = false

		@SerialEntry
		var cancelDrinkAnimation: Boolean = false
	}
}