package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.UISettings
import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Rarity.Companion.toArray
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.features.fishing.TrophyFishChat
import me.nobaboy.nobaaddons.ui.TextShadow
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.TextUtils.white
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

object FishingCategory {
	fun create() = category(tr("nobaaddons.config.fishing", "Fishing")) {
		add({ fishing::hideOtherPeopleFishing }) {
			name = tr("nobaaddons.config.fishing.hideOtherPeopleFishing", "Hide Other People Fishing")
			descriptionText = tr("nobaaddons.config.fishing.hideOtherPeopleFishing.tooltip", "Hides the fishing bobber of other players")
			booleanController()
		}

		add({ fishing.catchMessages::revertTreasureMessages }) {
			name = tr("nobaaddons.config.fishing.catchMessages.revertTreasureMessages", "Revert Treasure Catch Messages")
			descriptionText = tr("nobaaddons.config.fishing.catchMessages.revertTreasureMessages.tooltip", "Reverts treasure catch messages to the format used pre-Backwater Bayou")
			booleanController()
		}

		bobberTimer()
		catchTimer()
		trophyFishing()
		seaCreatureAlert()
		highlightThunderSparks()
	}

	private fun ConfigCategory.Builder.bobberTimer() {
		group(tr("nobaaddons.config.fishing.bobberTimer", "Bobber Timer")) {
			val enabled = add({ fishing.bobberTimer::enabled }) {
				name = CommonText.Config.ENABLED
				descriptionText = tr(
					"nobaaddons.config.fishing.bobberTimer.tooltip",
					"Displays how long your fishing bobber has been spawned above it; the color will change to gold once it's been spawned long enough to catch a Slugfish."
				)
				booleanController()
			}
			add({ fishing.bobberTimer::crimsonIsleOnly }) {
				name = tr("nobaaddons.config.fishing.bobberTimer.crimsonIsleOnly", "Only Show on Crimson Isle")
				require { option(enabled) }
				booleanController()
			}
		}
	}

	private fun ConfigCategory.Builder.catchTimer() {
		group(tr("nobaaddons.config.fishing.catchTimer", "Catch Timer")) {
			val enabled = add({ fishing::catchTimerHudElement }) {
				name = CommonText.Config.ENABLED
				descriptionText = tr("nobaaddons.config.fishing.catchTimer.enabled.tooltip", "Moves the catch timer text displayed above your fishing bobber to a HUD element")
				booleanController()
			}
			add(Binding.generic(TextShadow.SHADOW, UISettings.catchTimer::textShadow, UISettings.catchTimer::textShadow.setter)) {
				name = CommonText.Config.TEXT_STYLE
				require { option(enabled) }
				enumController()
			}
		}
	}

	private fun ConfigCategory.Builder.trophyFishing() {
		group(tr("nobaaddons.config.fishing.trophyFishing", "Trophy Fishing")) {
			val exampleMessage = TrophyFishChat.format(Text.literal("Blobfish").white(), TrophyFishRarity.BRONZE, 1234, 2345)

			val modify = add({ fishing.trophyFishing::modifyChatMessages }) {
				name = tr("nobaaddons.config.fishing.trophyFishing.modifyChatMessages", "Add Count to Catch Messages")
				descriptionText = tr("nobaaddons.config.fishing.trophyFishing.modifyChatMessages.tooltip", "Adds catch counts to chat messages, for example:\n\n$exampleMessage\n\nThis requires opening Odger's menu at least once to get accurate counts")
				booleanController()
			}
			val compact = add({ fishing.trophyFishing::compactMessages }) {
				name = tr("nobaaddons.config.fishing.trophyFishing.compactCatches", "Compact Catch Messages")
				descriptionText = tr("nobaaddons.config.fishing.trophyFishing.compactCatches.tooltip", "Removes the previous catch message of the same trophy fish & rarity while enabled; note that this may conflict with certain compact chat mods.")
				require { option(modify) }
				booleanController()
			}
			add({ fishing.trophyFishing::compactMaxRarity }) {
				name = tr("nobaaddons.config.fishing.trophyFishing.compactMaxRarity", "Compact Max Rarity")
				descriptionText = tr("nobaaddons.config.fishing.trophyFishing.compactMaxRarity.tooltip", "The maximum rarity to compact catch messages for. If this is set to Diamond, this will effectively compact all catch messages.")
				require { option(modify) and option(compact) }
				enumController()
			}
		}
	}

	private fun ConfigCategory.Builder.seaCreatureAlert() {
		group(tr("nobaaddons.config.fishing.seaCreatureAlert", "Sea Creature Alert")) {
			val enabled = add({ fishing.seaCreatureAlert::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ fishing.seaCreatureAlert::nameInsteadOfRarity }) {
				name = tr("nobaaddons.config.fishing.seaCreatureAlert.nameInsteadOfRarity", "Use Name instead of Rarity")
				descriptionText = tr("nobaaddons.config.fishing.seaCreatureAlert.nameInsteadOfRarity.tooltip", "Uses the sea creature's name instead when displaying the notification, instead of 'Legendary Catch!'")
				require { option(enabled) }
				booleanController()
			}
			add({ fishing.seaCreatureAlert::minimumRarity }) {
				name = tr("nobaaddons.config.fishing.seaCreatureAlert.minimumRarity", "Minimum Rarity")
				descriptionText = tr("nobaaddons.config.fishing.seaCreatureAlert.minimumRarity.tooltip", "The minimum rarity to display a catch notification for")
				require { option(enabled) }
				enumController(onlyInclude = (Rarity.COMMON..Rarity.MYTHIC).toArray())
			}
			add({ fishing.seaCreatureAlert::carrotKingIsRare }) {
				name = tr("nobaaddons.config.fishing.seaCreatureAlert.carrotKingIsRare", "Carrot King is Rare")
				descriptionText = tr("nobaaddons.config.fishing.seaCreatureAlert.carrotKingIsRare.tooltip", "Carrot King will be considered rare even if the above minimum rarity isn't low enough for it (since not many people fish for it)")
				require { option(enabled) }
				booleanController()
			}
			add({ fishing.seaCreatureAlert::announceInPartyChat }) {
				name = tr("nobaaddons.config.fishing.seaCreatureAlert.announceInPartyChat", "Announce in Party Chat")
				descriptionText = tr("nobaaddons.config.fishing.seaCreatureAlert.announceInPartyChat.tooltip", "A chat message will also be sent in party chat when catching a rare creature")
				require { option(enabled) }
				booleanController()
			}
			add({ fishing.seaCreatureAlert::notificationSound }) {
				name = CommonText.Config.NOTIFICATION_SOUND
				require { option(enabled) }
				enumController()
			}
		}
	}

	private fun ConfigCategory.Builder.highlightThunderSparks() {
		group(tr("nobaaddons.config.fishing.highlightThunderSparks", "Highlight Thunder Sparks")) {
			val enabled = add({ fishing.highlightThunderSparks::enabled }) {
				name = CommonText.Config.ENABLED
				descriptionText = null
				booleanController()
			}
			add({ fishing.highlightThunderSparks::highlightColor }, BiMapper.NobaAWTColorMapper) {
				name = CommonText.Config.HIGHLIGHT_COLOR
				require { option(enabled) }
				colorController()
			}
			add({ fishing.highlightThunderSparks::showText }) {
				name = tr("nobaaddons.config.fishing.highlightThunderSparks.showText", "Show Text")
				require { option(enabled) }
				booleanController()
			}
		}
	}
}