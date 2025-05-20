package me.nobaboy.nobaaddons.features.chat.filters.miscellaneous

import me.nobaboy.nobaaddons.api.skyblock.PetAPI
import me.nobaboy.nobaaddons.features.chat.filters.IChatFilter

object AutoPetChatFilter : IChatFilter {
	override val enabled: Boolean get() = config.hideAutopetMessages
	override fun shouldFilter(message: String): Boolean = message.matches(PetAPI.AUTOPET_REGEX)
}