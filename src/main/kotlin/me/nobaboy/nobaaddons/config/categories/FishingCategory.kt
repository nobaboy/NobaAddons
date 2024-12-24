package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.color
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.core.Rarity
import me.nobaboy.nobaaddons.core.Rarity.Companion.toArray
import me.nobaboy.nobaaddons.core.fishing.TrophyFishRarity
import me.nobaboy.nobaaddons.features.fishing.TrophyFishChat
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.TextUtils.white
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.Text

object FishingCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.fishing", "Fishing")) {
		// region Bobber Timer
		buildGroup(tr("nobaaddons.config.fishing.bobberTimer", "Bobber Timer")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.fishing.bobberTimer.enabled,
				property = config.fishing.bobberTimer::enabled
			)
			boolean(
				tr("nobaaddons.config.fishing.bobberTimer.crimsonIsleOnly", "Show on Crimson Isle Only"),
				default = defaults.fishing.bobberTimer.crimsonIsleOnly,
				property = config.fishing.bobberTimer::crimsonIsleOnly
			)
		}
		// endregion

		// region Trophy Fishing
		buildGroup(tr("nobaaddons.config.fishing.trophyFishing", "Trophy Fishing")) {
			val exampleMessage = TrophyFishChat.format(Text.literal("Blobfish").white(), TrophyFishRarity.BRONZE, 1234, 2345)

			boolean(
				tr("nobaaddons.config.fishing.trophyFishing.modifyChatMessages", "Add Count to Catch Messages"),
				tr("nobaaddons.config.fishing.trophyFishing.modifyChatMessages.tooltip", "Adds catch counts to chat messages, for example:\n\n$exampleMessage\n\nThis requires opening Odger's menu at least once to get accurate counts"),
				default = defaults.fishing.trophyFishing.modifyChatMessages,
				property = config.fishing.trophyFishing::modifyChatMessages,
			)
		}
		// endregion

		// region Sea Creature Alert
		buildGroup(tr("nobaaddons.config.fishing.seaCreatureAlert", "Sea Creature Alert")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.fishing.seaCreatureAlert.enabled,
				property = config.fishing.seaCreatureAlert::enabled
			)
			boolean(
				tr("nobaaddons.config.fishing.seaCreatureAlert.nameInsteadOfRarity", "Use Name instead of Rarity"),
				tr("nobaaddons.config.fishing.seaCreatureAlert.nameInsteadOfRarity.tooltip", "Uses the sea creature's name instead when displaying the notification, instead of 'Legendary Catch!'"),
				default = defaults.fishing.seaCreatureAlert.nameInsteadOfRarity,
				property = config.fishing.seaCreatureAlert::nameInsteadOfRarity
			)
			cycler(
				tr("nobaaddons.config.fishing.seaCreatureAlert.minimumRarity", "Minimum Rarity"),
				tr("nobaaddons.config.fishing.seaCreatureAlert.minimumRarity.tooltip", "The minimum rarity to display a catch notification for"),
				default = defaults.fishing.seaCreatureAlert.minimumRarity,
				property = config.fishing.seaCreatureAlert::minimumRarity,
				onlyInclude = (Rarity.COMMON..Rarity.MYTHIC).toArray()
			)
			boolean(
				tr("nobaaddons.config.fishing.seaCreatureAlert.carrotKingIsRare", "Carrot King is Rare"),
				tr("nobaaddons.config.fishing.seaCreatureAlert.carrotKingIsRare.tooltip", "Carrot King will be considered rare even if the above minimum rarity isn't low enough for it (since not many people fish for it)"),
				default = defaults.fishing.seaCreatureAlert.carrotKingIsRare,
				property = config.fishing.seaCreatureAlert::carrotKingIsRare
			)
			boolean(
				tr("nobaaddons.config.fishing.seaCreatureAlert.announceInPartyChat", "Announce in Party Chat"),
				tr("nobaaddons.config.fishing.seaCreatureAlert.announceInPartyChat.tooltip", "A chat message will also be sent in party chat when catching a rare creature"),
				default = defaults.fishing.seaCreatureAlert.announceInPartyChat,
				property = config.fishing.seaCreatureAlert::announceInPartyChat
			)
			cycler(
				CommonText.Config.NOTIFICATION_SOUND,
				default = defaults.fishing.seaCreatureAlert.notificationSound,
				property = config.fishing.seaCreatureAlert::notificationSound
			)
		}
		// endregion

		// region Highlight Thunder Sparks
		buildGroup(tr("nobaaddons.config.fishing.highlightThunderSparks", "Highlight Thunder Sparks")) {
			boolean(
				CommonText.Config.ENABLED,
				default = defaults.fishing.highlightThunderSparks.enabled,
				property = config.fishing.highlightThunderSparks::enabled
			)
			color(
				tr("nobaaddons.config.fishing.highlightThunderSparks.highlightColor", "Highlight Color"),
				default = defaults.fishing.highlightThunderSparks.highlightColor,
				property = config.fishing.highlightThunderSparks::highlightColor
			)
			boolean(
				tr("nobaaddons.config.fishing.highlightThunderSparks.showText", "Show Text"),
				default = defaults.fishing.highlightThunderSparks.showText,
				property = config.fishing.highlightThunderSparks::showText
			)
		}
		// endregion
	}
}