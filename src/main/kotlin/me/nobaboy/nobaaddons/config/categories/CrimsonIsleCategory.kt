package me.nobaboy.nobaaddons.config.categories

import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.config.util.builders.CategoryBuilder
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.enums.AnnounceChannel
import me.nobaboy.nobaaddons.utils.tr

object CrimsonIsleCategory {
	fun create() = category(tr("nobaaddons.config.crimsonIsle", "Crimson Isle")) {
		announceVanquisher()
	}

	private fun CategoryBuilder.announceVanquisher() {
		group(tr("nobaaddons.config.crimsonIsle.announceVanquisher", "Announce Vanquisher")) {
			val enabled = add({ crimsonIsle.announceVanquisher::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ crimsonIsle.announceVanquisher::announceChannel }) {
				name = CommonText.Config.ANNOUNCE_CHANNEL
				require { option(enabled) }
				enumController(onlyInclude = arrayOf(AnnounceChannel.ALL, AnnounceChannel.PARTY))
			}
		}
	}
}