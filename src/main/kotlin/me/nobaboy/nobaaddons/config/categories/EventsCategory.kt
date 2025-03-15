package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.NobaConfigUtils
import me.nobaboy.nobaaddons.config.NobaConfigUtils.boolean
import me.nobaboy.nobaaddons.config.NobaConfigUtils.buildGroup
import me.nobaboy.nobaaddons.config.NobaConfigUtils.cycler
import me.nobaboy.nobaaddons.config.NobaConfigUtils.label
import me.nobaboy.nobaaddons.config.NobaConfigUtils.requires
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr

object EventsCategory {
	fun create(defaults: NobaConfig, config: NobaConfig) = NobaConfigUtils.buildCategory(tr("nobaaddons.config.events", "Events")) {
		// region Hoppity's Hunt
		buildGroup(tr("nobaaddons.config.events.hoppity", "Hoppity's Hunt")) {
			boolean(
				tr("nobaaddons.config.events.hoppity.eggGuess", "Egg Guess"),
				tr("nobaaddons.config.events.hoppity.eggGuess.tooltip", "Guesses the eggs location from the Egglocator's ability"),
				default = defaults.events.hoppity.eggGuess,
				property = config.events.hoppity::eggGuess
			)
			boolean(
				tr("nobaaddons.config.events.hoppity.requireMythicRabbit", "Require Mythic Rabbit"),
				tr("nobaaddons.config.events.hoppity.requireMythicRabbit.tooltip", "Blocks opening /cf and displays a warning when using an Egglocator if you don't have a mythic Rabbit spawned"),
				default = defaults.events.hoppity.requireMythicRabbit,
				property = config.events.hoppity::requireMythicRabbit
			)
		}
		// endregion

		// region Mythological Ritual
		buildGroup(tr("nobaaddons.config.events.mythological", "Mythological Ritual")) {
			val burrowGuess = boolean(
				tr("nobaaddons.config.events.mythological.burrowGuess", "Burrow Guess"),
				tr("nobaaddons.config.events.mythological.burrowGuess.tooltip", "Guesses the next burrow location from the Ancestral Spade's Echo ability"),
				default = defaults.events.mythological.burrowGuess,
				property = config.events.mythological::burrowGuess
			)
			val findNearby = boolean(
				tr("nobaaddons.config.events.mythological.findNearbyBurrows", "Find Nearby Burrows"),
				tr("nobaaddons.config.events.mythological.findNearbyBurrows.tooltip", "Highlights nearby burrows with a waypoint"),
				default = defaults.events.mythological.findNearbyBurrows,
				property = config.events.mythological::findNearbyBurrows
			)
			boolean(
				tr("nobaaddons.config.events.mythological.dingOnBurrowFind", "Ding on Burrow Find"),
				tr("nobaaddons.config.events.mythological.dingOnBurrowFind.tooltip", "Plays a ding sound when a burrow is found nearby"),
				default = defaults.events.mythological.dingOnBurrowFind,
				property = config.events.mythological::dingOnBurrowFind
			) requires findNearby
			boolean(
				tr("nobaaddons.config.events.mythological.removeGuessOnBurrowFind", "Hide Guess Near Burrows"),
				tr("nobaaddons.config.events.mythological.removeGuessOnBurrowFind.tooltip", "Automatically hides any guesses when nearby burrows are found"),
				default = defaults.events.mythological.removeGuessOnBurrowFind,
				property = config.events.mythological::removeGuessOnBurrowFind
			) requires listOf(findNearby, burrowGuess)
			val warp = boolean(
				tr("nobaaddons.config.events.mythological.findNearestWarp", "Find Nearest Warp"),
				tr("nobaaddons.config.events.mythological.findNearestWarp.tooltip", "Finds the nearest /warp to the guess, which can automatically be warped to with the associated key configured in Controls"),
				default = defaults.events.mythological.findNearestWarp,
				property = config.events.mythological::findNearestWarp
			)

			label(tr("nobaaddons.config.events.mythological.label.inquisitorSharing", "Inquisitor Sharing"))

			val alertInquis = boolean(
				tr("nobaaddons.config.events.mythological.alertInquisitor", "Alert Inquisitor"),
				tr("nobaaddons.config.events.mythological.alertInquisitor.tooltip", "Send a message in chat when you find a Minos Inquisitor"),
				default = defaults.events.mythological.alertInquisitor,
				property = config.events.mythological::alertInquisitor
			)
			boolean(
				tr("nobaaddons.config.events.mythological.alertOnlyInParty", "Only Alert in Party Chat"),
				tr("nobaaddons.config.events.mythological.alertOnlyInParty.tooltip", "The Inquisitor alert message will always be sent in party chat, instead of your current selected /chat"),
				default = defaults.events.mythological.alertOnlyInParty,
				property = config.events.mythological::alertOnlyInParty
			) requires alertInquis
			cycler(
				CommonText.Config.NOTIFICATION_SOUND,
				default = defaults.events.mythological.notificationSound,
				property = config.events.mythological::notificationSound
			) requires alertInquis
			boolean(
				tr("nobaaddons.config.events.mythological.showInquisitorDespawnTime", "Show Inquisitor Despawn Time"),
				tr("nobaaddons.config.events.mythological.showInquisitorDespawnTime.tooltip", "Displays how much time is left until the Minos Inquisitor despawns"),
				default = defaults.events.mythological.showInquisitorDespawnTime,
				property = config.events.mythological::showInquisitorDespawnTime
			) requires alertInquis
			boolean(
				tr("nobaaddons.config.events.mythological.inquisitorFocusMode", "Inquisitor Focus Mode"),
				tr("nobaaddons.config.events.mythological.inquisitorFocusMode.tooltip", "Hides all other waypoints when an Inquisitor spawn is detected"),
				default = defaults.events.mythological.inquisitorFocusMode,
				property = config.events.mythological::inquisitorFocusMode
			) requires alertInquis

			// TODO: Questionable label
			label(tr("nobaaddons.config.events.mythological.label.tracker", "Tracker"))

			boolean(
				tr("nobaaddons.config.events.mythological.tracker", "Mythological Tracker"),
				default = defaults.events.mythological.tracker,
				property = config.events.mythological::tracker
			)
			boolean(
				tr("nobaaddons.config.events.mythological.showChainsFinished", "Show Chains Finished"),
				tr("nobaaddons.config.events.mythological.showChainsFinished.tooltip", "Shows how many burrow chains have been completed on the tracker"),
				default = defaults.events.mythological.showChainsFinished,
				property = config.events.mythological::showChainsFinished
			)

			label(CommonText.Config.LABEL_MISC)

			boolean(
				tr("nobaaddons.config.events.mythological.announceRareDrops", "Announce Rare Drops"),
				tr("nobaaddons.config.events.mythological.announceRareDrops.tooltip", "Sends rare drop messages for items that don't have one (like Dwarf Turtle Shelmet, Crochet Plushie, etc.)"),
				default = defaults.events.mythological.announceRareDrops,
				property = config.events.mythological::announceRareDrops
			)

			label(tr("nobaaddons.config.events.mythological.label.warpLocations", "Warp Locations"))

			boolean(
				tr("nobaaddons.config.events.mythological.ignoreCrypt", "Ignore /warp crypt"),
				tr("nobaaddons.config.events.mythological.ignoreCrypt.tooltip", "Because leaving the crypts may be inconvenient"),
				default = defaults.events.mythological.ignoreCrypt,
				property = config.events.mythological::ignoreCrypt
			) requires warp
			boolean(
				tr("nobaaddons.config.events.mythological.ignoreWizard", "Ignore /warp wizard"),
				tr("nobaaddons.config.events.mythological.ignoreWizard.tooltip", "Because it's easy to accidentally fall into the Rift from it"),
				default = defaults.events.mythological.ignoreWizard,
				property = config.events.mythological::ignoreWizard
			) requires warp
			boolean(
				tr("nobaaddons.config.events.mythological.ignoreStonks", "Ignore /warp stonks"),
				tr("nobaaddons.config.events.mythological.ignoreStonks.tooltip", "Because it's new"),
				default = defaults.events.mythological.ignoreStonks,
				property = config.events.mythological::ignoreStonks
			) requires warp
		}
		// endregion
	}
}