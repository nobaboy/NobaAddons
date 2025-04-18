package me.nobaboy.nobaaddons.features.chat.channeldisplay

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.PersistentCache
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.chat.SendMessageEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.HypixelUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ChatScreen
import kotlin.text.get
import kotlin.time.Duration.Companion.minutes

object ChatChannelDisplay {
	private val enabled by NobaConfig.chat::displayCurrentChannel
	private var channel by PersistentCache::channel

	private val CHANNEL_SWITCH_REGEX by Regex("^You are now in the (?<channel>ALL|GUILD|PARTY) channel").fromRepo("chat.channel")

	private val MESSAGE_CHANNEL_REGEX by Regex(
		"^Opened a chat conversation with ${CommonPatterns.PLAYER_NAME_WITH_RANK_STRING} for the next 5 minutes\\. Use /chat a to leave"
	).fromRepo("chat.message_channel")

	private val MESSAGE_EXPIRED by "The conversation you were in expired and you have been moved back to the ALL channel.".fromRepo("chat.message_expired")

	fun init() {
		ScreenEvents.AFTER_INIT.register { _, screen, _, _ ->
			if(screen !is ChatScreen) return@register
			ScreenEvents.afterRender(screen).register { _, ctx, _, _, _ ->
				ErrorManager.catching("Chat channel display errored") { onRenderChatScreen(screen, ctx) }
			}
		}
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		SendMessageEvents.SEND_CHAT_MESSAGE.register { onSendChatMessage() }
	}

	private fun onSendChatMessage() {
		if(!enabled) return
		if(!HypixelUtils.onHypixel) return

		val channel = this.channel // avoid a possible race condition between checking if this is null and setting it
		if(channel.expires != null) {
			channel.expires = Timestamp.now() + 5.minutes
		}
	}

	private fun onChatMessage(event: ChatMessageEvents.Chat) {
		if(!enabled) return
		if(!HypixelUtils.onHypixel) return

		CHANNEL_SWITCH_REGEX.onFullMatch(event.cleaned) {
			channel = ActiveChatChannel(ChatChannel.fromString(groups["channel"]!!.value))
			return
		}

		MESSAGE_CHANNEL_REGEX.onFullMatch(event.cleaned) {
			channel = ActiveChatChannel(ChatChannel.DM, groups["username"]!!.value, Timestamp.now() + 5.minutes)
			return
		}

		if(event.cleaned == MESSAGE_EXPIRED) {
			channel = ActiveChatChannel(ChatChannel.ALL)
		}
	}

	// TODO it'd be nice if this displayed for ~2s after closing the chat screen
	private fun onRenderChatScreen(screen: ChatScreen, ctx: DrawContext) {
		if(!enabled) return
		if(!HypixelUtils.onHypixel) return

		ctx.drawText(
			MCUtils.textRenderer,
			tr("nobaaddons.chat.channel.current", "Currently in ${channel.toText()}").green(),
			4,
			screen.height - 26,
			0xFFFFFF,
			true,
		)
	}
}