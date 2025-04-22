package me.nobaboy.nobaaddons.config.categories

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.util.*
import me.nobaboy.nobaaddons.utils.CommonText
import me.nobaboy.nobaaddons.utils.tr

object DungeonsCategory {
	fun create() = category(tr("nobaaddons.config.dungeons", "Dungeons")) {
		highlightStarredMobs()
		simonSaysTimer()
	}

	private fun ConfigCategory.Builder.highlightStarredMobs() {
		group(tr("nobaaddons.config.dungeons.highlightStarredMobs", "Highlight Starred Mobs")) {
			val enabled = add({ dungeons.highlightStarredMobs::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ dungeons.highlightStarredMobs::highlightColor }, BiMapper.NobaAWTColorMapper) {
				name = CommonText.Config.HIGHLIGHT_COLOR
				require { option(enabled) }
				colorController()
			}
			add({ dungeons.highlightStarredMobs::highlightMode }) {
				name = tr("nobaaddons.config.dungeons.highlightStarredMobs.highlightMode", "Highlight Mode")
				require { option(enabled) }
				enumController()
			}
		}
	}

	private fun ConfigCategory.Builder.simonSaysTimer() {
		group(tr("nobaaddons.config.dungeons.simonSaysTimer", "Simon Says Timer")) {
			val enabled = add({ dungeons.simonSaysTimer::enabled }) {
				name = CommonText.Config.ENABLED
				booleanController()
			}
			add({ dungeons.simonSaysTimer::timeInPartyChat }) {
				name = tr("nobaaddons.config.dungeons.simonSaysTimer.timeInPartyChat", "Send Time in Party Chat")
				descriptionText = tr("nobaaddons.config.dungeons.simonSaysTimer.timeInPartyChat.tooltip", "Sends your Simon Says device completion time in party chat")
				require { option(enabled) }
				booleanController()
			}
		}
	}
}