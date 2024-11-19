package me.nobaboy.nobaaddons.config.configs

import dev.isxander.yacl3.config.v2.api.SerialEntry
import me.nobaboy.nobaaddons.screens.hud.elements.TextElement
import me.nobaboy.nobaaddons.utils.NobaColor
import java.awt.Color

class UIAndVisualsConfig {
	@SerialEntry
	var showUsageText: Boolean = true

	@SerialEntry
	val temporaryWaypoints: TemporaryWaypoints = TemporaryWaypoints()

	@SerialEntry
	val etherwarpHelper: EtherwarpHelper = EtherwarpHelper()

	@SerialEntry
	val slotInfo: SlotInfo = SlotInfo()

	@SerialEntry
	val renderingTweaks: RenderingTweaks = RenderingTweaks()

	@SerialEntry
	val swingAnimation = SwingAnimation()

	@SerialEntry
	val itemPosition = FirstPersonItemPosition()

	@SerialEntry
	val infoBoxes = mutableListOf<TextElement>()

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
		var allowOnAir: Boolean = false
	}

	class SlotInfo {
		@SerialEntry
		var enabled: Boolean = false

		@SerialEntry
		var bestiaryMilestone: Boolean = false

		@SerialEntry
		var bestiaryTier: Boolean = false

		@SerialEntry
		var collectionTier: Boolean = false

		@SerialEntry
		var dungeonBossHead: Boolean = false

		@SerialEntry
		var enchantedBook: Boolean = false

		@SerialEntry
		var kuudraKey: Boolean = false

		@SerialEntry
		var masterSkull: Boolean = false

		@SerialEntry
		var masterStar: Boolean = false

		@SerialEntry
		var minionTier: Boolean = false

		@SerialEntry
		var potionLevel: Boolean = false

		@SerialEntry
		var skillLevel: Boolean = false

		@SerialEntry
		var skyBlockLevel: Boolean = false
	}

	class RenderingTweaks {
		@SerialEntry
		var hideOtherPeopleFishing: Boolean = false

		@SerialEntry
		var hideLightningBolt: Boolean = false

		@SerialEntry var removeFrontFacingThirdPerson: Boolean = false
	}

	class SwingAnimation {
		@SerialEntry var swingDuration: Int = 1
		@SerialEntry var applyToAllPlayers: Boolean = false
	}

	class FirstPersonItemPosition {
		@SerialEntry var scale: Float = 1f // [0.1f, 2f]
		@SerialEntry var x: Int = 0 // [-150, 150]
		@SerialEntry var y: Int = 0 // [-150, 150]
		@SerialEntry var z: Int = 0 // [-150, 50]
		@SerialEntry var cancelEquipAnimation: Boolean = false
		@SerialEntry var cancelDrinkAnimation: Boolean = false
	}
}