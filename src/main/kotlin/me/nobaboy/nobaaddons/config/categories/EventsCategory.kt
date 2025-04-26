package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.config.util.builders.CategoryBuilder
import me.nobaboy.nobaaddons.config.util.builders.OptionBuilder.Companion.descriptionText
import me.nobaboy.nobaaddons.config.util.builders.label
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.enums.AnnounceChannel
import me.nobaboy.nobaaddons.utils.tr

object EventsCategory {
	fun create() = category(tr("nobaaddons.config.events", "Events")) {
		hoppity()
		mythologicalRitual()
	}

	private fun CategoryBuilder.hoppity() {
		group(tr("nobaaddons.config.events.hoppity", "Hoppity's Hunt")) {
			add({ events.hoppity::eggGuess }) {
				name = tr("nobaaddons.config.events.hoppity.eggGuess", "Egg Guess")
				descriptionText = tr("nobaaddons.config.events.hoppity.eggGuess.tooltip", "Guesses the eggs location from the Egglocator's ability")
				booleanController()
			}
			add({ events.hoppity::requireMythicRabbit }) {
				name = tr("nobaaddons.config.events.hoppity.requireMythicRabbit", "Require Mythic Rabbit")
				descriptionText = tr("nobaaddons.config.events.hoppity.requireMythicRabbit.tooltip", "Blocks opening /cf and displays a warning when using an Egglocator if you don't have a mythic Rabbit spawned")
				booleanController()
			}
		}
	}

	private fun CategoryBuilder.mythologicalRitual() {
		group(tr("nobaaddons.config.events.mythological", "Mythological Ritual")) {
			val burrowGuess = add({ events.mythological::burrowGuess }) {
				name = tr("nobaaddons.config.events.mythological.burrowGuess", "Burrow Guess")
				descriptionText = tr("nobaaddons.config.events.mythological.burrowGuess.tooltip", "Guesses the next burrow location from the Ancestral Spade's Echo ability")
				booleanController()
			}
			val findNearby = add({ events.mythological::findNearbyBurrows }) {
				name = tr("nobaaddons.config.events.mythological.findNearbyBurrows", "Find Nearby Burrows")
				descriptionText = tr("nobaaddons.config.events.mythological.findNearbyBurrows.tooltip", "Highlights nearby burrows with a waypoint")
				booleanController()
			}
			add({ events.mythological::dingOnBurrowFind }) {
				name = tr("nobaaddons.config.events.mythological.pingOnBurrowFind", "Ping on Burrow Find")
				descriptionText = tr("nobaaddons.config.events.mythological.pingOnBurrowFind.tooltip", "Plays a sound when a burrow is found nearby")
				require { option(findNearby) }
				booleanController()
			}
			add({ events.mythological::removeGuessOnBurrowFind }) {
				name = tr("nobaaddons.config.events.mythological.removeGuessOnBurrowFind", "Hide Guess Near Burrows")
				descriptionText = tr("nobaaddons.config.events.mythological.removeGuessOnBurrowFind.tooltip", "Automatically hides any guesses when nearby burrows are found")
				require { option(findNearby) and option(burrowGuess) }
				booleanController()
			}
			val warp = add({ events.mythological::findNearestWarp }) {
				name = tr("nobaaddons.config.events.mythological.findNearestWarp", "Find Nearest Warp")
				descriptionText = tr("nobaaddons.config.events.mythological.findNearestWarp.tooltip", "Finds the nearest /warp to the guess, which can automatically be warped to with the associated key configured in Controls")
				booleanController()
			}

			label(tr("nobaaddons.config.events.mythological.label.inquisitorSharing", "Inquisitor Sharing"))

			val alertInquis = add({ events.mythological::alertInquisitor }) {
				name = tr("nobaaddons.config.events.mythological.alertInquisitor", "Alert Inquisitor")
				descriptionText = tr("nobaaddons.config.events.mythological.alertInquisitor.tooltip", "Send a message in chat when you find a Minos Inquisitor")
				booleanController()
			}
			add({ events.mythological::announceChannel }) {
				name = CommonText.Config.ANNOUNCE_CHANNEL
				require { option(alertInquis) }
				enumController(onlyInclude = arrayOf(AnnounceChannel.ALL, AnnounceChannel.PARTY))
			}
			add({ events.mythological::notificationSound }) {
				name = CommonText.Config.NOTIFICATION_SOUND
				require { option(alertInquis) }
				enumController()
			}
			add({ events.mythological::showInquisitorDespawnTime }) {
				name = tr("nobaaddons.config.events.mythological.showInquisitorDespawnTime", "Show Inquisitor Despawn Time")
				descriptionText = tr("nobaaddons.config.events.mythological.showInquisitorDespawnTime.tooltip", "Displays how much time is left until the Minos Inquisitor despawns")
				require { option(alertInquis) }
				booleanController()
			}
			add({ events.mythological::inquisitorFocusMode }) {
				name = tr("nobaaddons.config.events.mythological.inquisitorFocusMode", "Inquisitor Focus Mode")
				descriptionText = tr("nobaaddons.config.events.mythological.inquisitorFocusMode.tooltip", "Hides all other waypoints when an Inquisitor spawn is detected")
				require { option(alertInquis) }
				booleanController()
			}

			label(CommonText.Config.LABEL_MISC)

			add({ events.mythological::announceRareDrops }) {
				name = tr("nobaaddons.config.events.mythological.announceRareDrops", "Announce Rare Drops")
				descriptionText = tr("nobaaddons.config.events.mythological.announceRareDrops.tooltip", "Sends rare drop messages for items that don't have one (like Dwarf Turtle Shelmet, Crochet Plushie, etc.)")
				booleanController()
			}

			label(tr("nobaaddons.config.events.mythological.label.warpLocations", "Warp Locations"))

			add({ events.mythological::ignoreCrypt }) {
				name = tr("nobaaddons.config.events.mythological.ignoreCrypt", "Ignore /warp crypt")
				descriptionText = tr("nobaaddons.config.events.mythological.ignoreCrypt.tooltip", "Because leaving the crypts may be inconvenient")
				require { option(warp) }
				booleanController()
			}
			add({ events.mythological::ignoreWizard }) {
				name = tr("nobaaddons.config.events.mythological.ignoreWizard", "Ignore /warp wizard")
				descriptionText = tr("nobaaddons.config.events.mythological.ignoreWizard.tooltip", "Because it's easy to accidentally fall into the Rift from it")
				require { option(warp) }
				booleanController()
			}
			add({ events.mythological::ignoreStonks }) {
				name = tr("nobaaddons.config.events.mythological.ignoreStonks", "Ignore /warp stonks")
				descriptionText = tr("nobaaddons.config.events.mythological.ignoreStonks.tooltip", "Because it's new")
				require { option(warp) }
				booleanController()
			}
		}
	}
}