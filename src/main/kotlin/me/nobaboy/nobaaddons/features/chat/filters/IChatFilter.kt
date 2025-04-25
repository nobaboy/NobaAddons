package me.nobaboy.nobaaddons.features.chat.filters

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.MultiEventInvoker
import me.nobaboy.nobaaddons.features.chat.filters.ability.AbilityChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.BlessingChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.HealerOrbChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.dungeons.PickupObtainChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.miscellaneous.ProfileInfoChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.miscellaneous.TipMessagesChatFilter
import me.nobaboy.nobaaddons.features.chat.filters.mobs.SeaCreatureCatchMessageChatFilter
import me.nobaboy.nobaaddons.utils.StringUtils.cleanFormatting
import net.minecraft.text.Text

interface IChatFilter {
	val config get() = NobaConfig.chat.filters

	val enabled: Boolean

	fun shouldFilter(message: Text): Boolean = shouldFilter(message.string.cleanFormatting())
	fun shouldFilter(message: String): Boolean

	companion object : MultiEventInvoker<ChatMessageEvents.Allow, IChatFilter>(
		dispatcher = ChatMessageEvents.ALLOW,
		toInvoke = arrayOf(
			// Item Abilities
			AbilityChatFilter,
			// Mobs
			SeaCreatureCatchMessageChatFilter,
			// Dungeons
			BlessingChatFilter,
			HealerOrbChatFilter,
			PickupObtainChatFilter,
			// Miscellaneous
			ProfileInfoChatFilter,
			TipMessagesChatFilter,
		),
		invoker = { if(enabled && shouldFilter(it.message)) it.cancel() },
	)
}