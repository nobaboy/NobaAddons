package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.button
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.slider
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesScreen
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.strikethrough
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text
import net.minecraft.text.Texts
import java.util.Calendar

private val year = Calendar.getInstance().get(Calendar.YEAR)

private fun societyIsCrumbling(@Suppress("SameParameterValue") fromYear: Int): Text {
	return Texts.join(buildList {
		addAll((fromYear until year).map { it.toText().strikethrough() })
		add(year.toText())
	}, " ".toText())
}

object UIAndVisualsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.uiAndVisuals", "UI & Visuals")) {
		button(tr("nobaaddons.screen.infoBoxes", "Info Boxes"), text = CommonText.SCREEN_OPEN) {
			MCUtils.client.setScreen(InfoBoxesScreen(it))
		}

		// region Temporary Waypoints
		buildGroup(tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints", "Temporary Waypoints")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.uiAndVisuals.temporaryWaypoints.enabled,
				property = config.uiAndVisuals.temporaryWaypoints::enabled
			)
			color(
				tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints.waypointColor", "Waypoint Color"),
				default = defaults.uiAndVisuals.temporaryWaypoints.waypointColor,
				property = config.uiAndVisuals.temporaryWaypoints::waypointColor
			)
			slider(
				tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints.expirationTime", "Expiration Time"),
				tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints.expirationTime.tooltip", "Sets the duration after which a temporary waypoint disappears"),
				default = defaults.uiAndVisuals.temporaryWaypoints.expirationTime,
				property = config.uiAndVisuals.temporaryWaypoints::expirationTime,
				min = 1,
				max = 120,
				step = 1
			)
		}
		// endregion

		// region Etherwarp Helper
		buildGroup(tr("nobaaddons.config.uiAndVisuals.etherwarpHelper", "Etherwarp Overlay")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.uiAndVisuals.etherwarpHelper.enabled,
				property = config.uiAndVisuals.etherwarpHelper::enabled
			)
			color(
				tr("nobaaddons.config.uiAndVisuals.etherwarpHelper.highlightColor", "Highlight Color"),
				default = defaults.uiAndVisuals.etherwarpHelper.highlightColor,
				property = config.uiAndVisuals.etherwarpHelper::highlightColor
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.etherwarpHelper.showFailText", "Show Fail Text"),
				tr("nobaaddons.config.uiAndVisuals.etherwarpHelper.showFailText.tooltip", "Displays the reason for an Etherwarp failure below the crosshair"),
				default = defaults.uiAndVisuals.etherwarpHelper.showFailText,
				property = config.uiAndVisuals.etherwarpHelper::showFailText
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.etherwarpHelper.allowOverlayOnAir", "Allow Overlay on Air"),
				tr("nobaaddons.config.uiAndVisuals.etherwarpHelper.allowOverlayOnAir.tooltip", "Allows the overlay to render on air blocks displaying how far you're allowed to teleport"),
				default = defaults.uiAndVisuals.etherwarpHelper.allowOverlayOnAir,
				property = config.uiAndVisuals.etherwarpHelper::allowOverlayOnAir
			)
		}
		// endregion

		// region Rendering Tweaks
		buildGroup(tr("nobaaddons.config.uiAndVisuals.renderingTweaks", "Rendering Tweaks")) {
			boolean(
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideLightningBolt", "Hide Lightning Bolt"),
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideLightningBolt.tooltip", "Prevents lightning bolts from rendering"),
				default = defaults.uiAndVisuals.renderingTweaks.hideLightningBolt,
				property = config.uiAndVisuals.renderingTweaks::hideLightningBolt
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing", "Hide Other People Fishing"),
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing.tooltip", "Hides the fishing bobber of other players"),
				default = defaults.uiAndVisuals.renderingTweaks.hideOtherPeopleFishing,
				property = config.uiAndVisuals.renderingTweaks::hideOtherPeopleFishing
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson", "Remove Front-Facing Third Person"),
				tr("nobaaddons.config.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson.tooltip", "Removes the front-facing perspective from F5"),
				default = defaults.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson,
				property = config.uiAndVisuals.renderingTweaks::removeFrontFacingThirdPerson
			)
		}
		// endregion

		// region Arm Swing Animation Tweaks
		buildGroup(tr("nobaaddons.config.uiAndVisuals.swingAnimation", "Arm Swing Animation Tweaks")) {
			slider(
				tr("nobaaddons.config.uiAndVisuals.swingAnimation.swingDuration", "Swing Duration"),
				tr("nobaaddons.config.uiAndVisuals.swingAnimation.swingDuration.tooltip", "Controls how long your arm swing animation duration is, ignoring all effects like Haste"),
				default = defaults.uiAndVisuals.swingAnimation.swingDuration,
				property = config.uiAndVisuals.swingAnimation::swingDuration,
				min = 1,
				max = 60,
				step = 1,
			) {
				when(it) {
					1 -> tr("nobaaddons.config.label.off", "Off").red()
					6 -> tr("nobaaddons.config.label.default", "Default")
					else -> it.toString().toText()
				}
			}

			boolean(
				tr("nobaaddons.config.uiAndVisuals.swingAnimation.applyToAllPlayers", "Apply to All Players"),
				tr("nobaaddons.config.uiAndVisuals.swingAnimation.applyToAllPlayers.tooltip", "If enabled, the above swing duration will also apply to all players, insteada of only yourself"),
				default = defaults.uiAndVisuals.swingAnimation.applyToAllPlayers,
				property = config.uiAndVisuals.swingAnimation::applyToAllPlayers,
			)
		}
		// endregion

		// region First Person Item Rendering
		buildGroup(tr("nobaaddons.config.uiAndVisuals.itemRendering", "First Person Item Rendering")) {
			boolean(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelReequip", "Cancel Re-equip Animation"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelReequip.tooltip", "Prevents the item update animation from playing entirely, including when switching items"),
				default = defaults.uiAndVisuals.itemPosition.cancelEquipAnimation,
				property = config.uiAndVisuals.itemPosition::cancelEquipAnimation
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelItemUpdate", "Cancel Item Update Animation"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelItemUpdate.tooltip", "Prevents the item update animation from playing when your held item is updated"),
				default = defaults.uiAndVisuals.itemPosition.cancelItemUpdateAnimation,
				property = config.uiAndVisuals.itemPosition::cancelItemUpdateAnimation
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelDrinkAnimation", "Cancel Item Consume Animation"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelDrinkAnimation.tooltip", "Prevents the item consume animation (such as from drinking potions) from playing"),
				default = defaults.uiAndVisuals.itemPosition.cancelDrinkAnimation,
				property = config.uiAndVisuals.itemPosition::cancelDrinkAnimation
			)
			slider(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.xOffset", "Held Item X Offset"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.xOffset.tooltip", "Moves the held item model in first person left or right across the screen; note that this slider is inverted if your main hand is set to left handed"),
				default = defaults.uiAndVisuals.itemPosition.x,
				property = config.uiAndVisuals.itemPosition::x,
				min = -150,
				max = 150,
				step = 1
			)
			slider(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.yOffset", "Held Item Y Offset"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.yOffset.tooltip", "Moves the held item up and down across the screen"),
				default = defaults.uiAndVisuals.itemPosition.y,
				property = config.uiAndVisuals.itemPosition::y,
				min = -150,
				max = 150,
				step = 1
			)
			slider(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.zOffset", "Held Item Z Offset"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.zOffset.tooltip", "Moves the held item towards or away from the camera"),
				default = defaults.uiAndVisuals.itemPosition.z,
				property = config.uiAndVisuals.itemPosition::z,
				min = -150,
				max = 50,
				step = 1
			)
			slider(
				tr("nobaaddons.config.uiAndVisuals.itemRendering.scale", "Held Item Scale"),
				tr("nobaaddons.config.uiAndVisuals.itemRendering.scale.tooltip", "Controls how large the held item is rendered in first person"),
				default = defaults.uiAndVisuals.itemPosition.scale,
				property = config.uiAndVisuals.itemPosition::scale,
				min = 0.1f,
				max = 2.0f,
				step = 0.1f
			)
		}
		// endregion

		// region Armor Glint Tweaks
		buildGroup(tr("nobaaddons.config.uiAndVisuals.armorGlints", "Armor Glint Tweaks")) {
			boolean(
				tr("nobaaddons.config.uiAndVisuals.armorGlints.fixGlints", "Fix Armor Enchant Glints"),
				tr("nobaaddons.config.uiAndVisuals.armorGlints.fixGlints.tooltip", "It's ${societyIsCrumbling(2019)}. Society has progressed little in the past 20 years. The world is falling apart. Hypixel still has yet to figure out how to consistently add an enchantment glint to armor."),
				default = defaults.uiAndVisuals.renderingTweaks.fixEnchantedArmorGlint,
				property = config.uiAndVisuals.renderingTweaks::fixEnchantedArmorGlint
			)
			boolean(
				tr("nobaaddons.config.uiAndVisuals.armorGlints.removeGlints", "Remove All Armor Enchant Glints"),
				tr("nobaaddons.config.uiAndVisuals.armorGlints.removeGlints.tooltip", "Removes enchantment glints from all armor pieces"),
				default = defaults.uiAndVisuals.renderingTweaks.removeArmorGlints,
				property = config.uiAndVisuals.renderingTweaks::removeArmorGlints
			)
		}
		// endregion
	}
}