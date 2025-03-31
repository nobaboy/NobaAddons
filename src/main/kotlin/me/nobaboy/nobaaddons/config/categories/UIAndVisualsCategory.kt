package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.screens.infoboxes.InfoBoxesScreen
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.red
import me.nobaboy.nobaaddons.utils.TextUtils.strikethrough
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.screen.ScreenTexts
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
	fun create() = category(tr("nobaaddons.config.uiAndVisuals", "UI & Visuals")) {
		button(tr("nobaaddons.screen.infoBoxes", "Info Boxes"), text = CommonText.SCREEN_OPEN) {
			MCUtils.client.setScreen(InfoBoxesScreen(it))
		}
		add({ uiAndVisuals::renderInfoBoxesOutsideSkyBlock }) {
			name = tr("nobaaddons.config.uiAndVisuals.renderInfoBoxesOutsideSkyBlock", "Render Info Boxes Outside SkyBlock")
			descriptionText = tr("nobaaddons.config.uiAndVisuals.renderInfoBoxesOutsideSkyBlock.tooltip", "Enables rendering of Info Boxes while not in SkyBlock")
			booleanController()
		}

		temporaryWaypoints()
		etherwarpOverlay()
		renderingTweaks()
		armSwingAnimation()
		firstPersonItem()
		armorGlint()
	}

	private fun ConfigCategory.Builder.temporaryWaypoints() {
		group(tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints", "Temporary Waypoints")) {
			val enabled = add({ uiAndVisuals.temporaryWaypoints::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ uiAndVisuals.temporaryWaypoints::waypointColor }, BiMapper.NobaAWTColorMapper) {
				name = tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints.waypointColor", "Waypoint Color")
				require { option(enabled) }
				colorController()
			}
			add({ uiAndVisuals.temporaryWaypoints::expirationTime }) {
				name = tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints.expirationTime", "Expiration Time")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.temporaryWaypoints.expirationTime.tooltip", "Sets the duration after which a temporary waypoint disappears")
				require { option(enabled) }
				intSliderController(min = 1, max = 120)
			}
		}
	}

	private fun ConfigCategory.Builder.etherwarpOverlay() {
		group(tr("nobaaddons.config.uiAndVisuals.etherwarpOverlay", "Etherwarp Overlay")) {
			val enabled = add({ uiAndVisuals.etherwarpOverlay::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ uiAndVisuals.etherwarpOverlay::highlightColor }, BiMapper.NobaAWTColorMapper) {
				name = CommonText.Config.HIGHLIGHT_COLOR
				require { option(enabled) }
				colorController()
			}
			add({ uiAndVisuals.etherwarpOverlay::failHighlightColor }, BiMapper.NobaAWTColorMapper) {
				name = tr("nobaaddons.config.uiAndVisuals.etherwarpOverlay.failHighlightColor", "Fail Highlight Color")
				require { option(enabled) }
				colorController()
			}
			add({ uiAndVisuals.etherwarpOverlay::showFailText }) {
				name = tr("nobaaddons.config.uiAndVisuals.etherwarpOverlay.showFailText", "Show Fail Text")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.etherwarpOverlay.showFailText.tooltip", "Displays the reason for an Etherwarp failure below the crosshair")
				require { option(enabled) }
				booleanController()
			}
			add({ uiAndVisuals.etherwarpOverlay::allowOverlayOnAir }) {
				name = tr("nobaaddons.config.uiAndVisuals.etherwarpOverlay.allowOverlayOnAir", "Allow Overlay on Air")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.etherwarpOverlay.allowOverlayOnAir.tooltip", "Allows the overlay to render on air blocks displaying how far you're allowed to teleport")
				require { option(enabled) }
				booleanController()
			}
		}
	}

	private fun ConfigCategory.Builder.renderingTweaks() {
		group(tr("nobaaddons.config.uiAndVisuals.renderingTweaks", "Rendering Tweaks")) {
			add({ uiAndVisuals.renderingTweaks::hideLightningBolt }) {
				name = tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideLightningBolt", "Hide Lightning Bolt")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideLightningBolt.tooltip", "Prevents lightning bolts from rendering")
				booleanController()
			}
			add({ uiAndVisuals.renderingTweaks::removeFrontFacingThirdPerson }) {
				name = tr("nobaaddons.config.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson", "Remove Front-Facing Third Person")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.renderingTweaks.removeFrontFacingThirdPerson.tooltip", "Removes the front-facing perspective from F5")
				booleanController()
			}
			add({ uiAndVisuals.renderingTweaks::hideAbsorptionHearts }) {
				name = tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideAbsorptionHearts", "Hide Absorption Hearts")
				booleanController()
			}
			add({ uiAndVisuals.renderingTweaks::hideAirBubbles }) {
				name = tr("nobaaddons.config.uiAndVisuals.renderingTweaks.hideAirBubbles", "Hide Air Bubbles")
				booleanController()
			}
		}
	}

	private fun ConfigCategory.Builder.armSwingAnimation() {
		group(tr("nobaaddons.config.uiAndVisuals.swingAnimation", "Arm Swing Animation Tweaks")) {
			val duration = add({ uiAndVisuals.swingAnimation::swingDuration }) {
				name = tr("nobaaddons.config.uiAndVisuals.swingAnimation.swingDuration", "Swing Duration")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.swingAnimation.swingDuration.tooltip", "Controls how long your arm swing animation duration is, ignoring all effects like Haste")
				intSliderController(min = 1, max = 60) {
					when(it) {
						1 -> ScreenTexts.OFF.toText().red()
						6 -> tr("nobaaddons.config.label.default", "Default")
						else -> it.toText()
					}
				}
			}
			add({ uiAndVisuals.swingAnimation::staticSwingPosition }) {
				name = tr("nobaaddons.config.uiAndVisuals.swingAnimation.static", "Static Item Swing Position")
				booleanController()
			}
			add({ uiAndVisuals.swingAnimation::applyToAllPlayers }) {
				name = tr("nobaaddons.config.uiAndVisuals.swingAnimation.applyToAllPlayers", "Apply to All Players")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.swingAnimation.applyToAllPlayers.tooltip", "If enabled, the above swing duration will also apply to all players, insteada of only yourself")
				require { option(duration) { it > 1 } }
				booleanController()
			}
		}
	}

	private fun ConfigCategory.Builder.firstPersonItem() {
		group(tr("nobaaddons.config.uiAndVisuals.itemRendering", "First Person Item Rendering")) {
			val cancelReequip = add({ uiAndVisuals.itemPosition::cancelEquipAnimation }) {
				name = tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelReequip", "Cancel Re-equip Animation")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelReequip.tooltip", "Prevents the item update animation from playing entirely, including when switching items")
				booleanController()
			}
			add({ uiAndVisuals.itemPosition::cancelItemUpdateAnimation }) {
				name = tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelItemUpdate", "Cancel Item Update Animation")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelItemUpdate.tooltip", "Prevents the item update animation from playing when your held item is updated")
				require { !option(cancelReequip) }
				booleanController()
			}
			add({ uiAndVisuals.itemPosition::cancelDrinkAnimation }) {
				name = tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelDrinkAnimation", "Cancel Item Consume Animation")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.itemRendering.cancelDrinkAnimation.tooltip", "Prevents the item consume animation (such as from drinking potions) from playing")
				booleanController()
			}
			add({ uiAndVisuals.itemPosition::x }) {
				name = tr("nobaaddons.config.uiAndVisuals.itemRendering.xOffset", "Held Item X Offset")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.itemRendering.xOffset.tooltip", "Moves the held item model in first person left or right across the screen; note that this slider is inverted if your main hand is set to left handed")
				intSliderController(min = -150, max = 150)
			}
			add({ uiAndVisuals.itemPosition::y }) {
				name = tr("nobaaddons.config.uiAndVisuals.itemRendering.yOffset", "Held Item Y Offset")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.itemRendering.yOffset.tooltip", "Moves the held item up and down across the screen")
				intSliderController(min = -150, max = 150)
			}
			add({ uiAndVisuals.itemPosition::z }) {
				name = tr("nobaaddons.config.uiAndVisuals.itemRendering.zOffset", "Held Item Z Offset")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.itemRendering.zOffset.tooltip", "Moves the held item towards or away from the camera")
				intSliderController(min = -150, max = 50)
			}
			add({ uiAndVisuals.itemPosition::scale }) {
				name = tr("nobaaddons.config.uiAndVisuals.itemRendering.scale", "Held Item Scale")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.itemRendering.scale.tooltip", "Controls how large the held item is rendered in first person")
				floatSliderController(min = 0.1f, max = 2f, step = 0.1f)
			}
		}
	}

	private fun ConfigCategory.Builder.armorGlint() {
		group(tr("nobaaddons.config.uiAndVisuals.armorGlints", "Armor Glint Tweaks")) {
			val fix = add({ uiAndVisuals.renderingTweaks::fixEnchantedArmorGlint }) {
				name = tr("nobaaddons.config.uiAndVisuals.armorGlints.fixGlints", "Fix Armor Enchant Glints")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.armorGlints.fixGlints.tooltip", "It's ${societyIsCrumbling(2019)}. Society has progressed little in the past 20 years. The world is falling apart. Hypixel still has yet to figure out how to consistently add an enchantment glint to armor.")
				booleanController()
			}
			val remove = add({ uiAndVisuals.renderingTweaks::removeArmorGlints }) {
				name = tr("nobaaddons.config.uiAndVisuals.armorGlints.removeGlints", "Remove All Armor Enchant Glints")
				descriptionText = tr("nobaaddons.config.uiAndVisuals.armorGlints.removeGlints.tooltip", "Removes enchantment glints from all armor pieces")
				booleanController()
			}

			fix.require { !option(remove) }
		}
	}
}