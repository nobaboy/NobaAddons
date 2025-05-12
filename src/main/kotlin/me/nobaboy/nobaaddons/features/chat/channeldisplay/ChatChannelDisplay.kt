package me.nobaboy.nobaaddons.features.chat.channeldisplay

import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.core.PersistentCache
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.chat.SendMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.events.impl.render.ScreenRenderEvents
import me.nobaboy.nobaaddons.repo.Repo.fromRepo
import me.nobaboy.nobaaddons.utils.CommonPatterns
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.HypixelUtils
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.NobaColor
import me.nobaboy.nobaaddons.utils.RegexUtils.onFullMatch
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.green
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.render.RenderUtils
import me.nobaboy.nobaaddons.utils.tr
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.ChatScreen
import net.minecraft.client.render.RenderTickCounter
import kotlin.time.Duration.Companion.minutes

object ChatChannelDisplay {
	private val config get() = NobaConfig.chat
	private val enabled: Boolean get() = config.displayCurrentChannel && HypixelUtils.onHypixel

	private const val DISPLAY_FOR_TICKS = 40
	private const val FADE_OUT_AT = 10

	private var channel by PersistentCache::channel
	private var ticksSinceChatOpen = DISPLAY_FOR_TICKS

	private val CHANNEL_SWITCH_REGEX by Regex("^You are now in the (?<channel>ALL|GUILD|OFFICER|PARTY|SKYBLOCK CO-OP) channel").fromRepo("chat.channel")

	private val CONVERSATION_OPENED_REGEX by Regex(
		"^Opened a chat conversation with ${CommonPatterns.PLAYER_NAME_WITH_RANK_STRING} for the next 5 minutes\\. Use /chat a to leave"
	).fromRepo("chat.conversation_opened")

	private val CONVERSATION_EXPIRED_MESSAGE by "The conversation you were in expired and you have been moved back to the ALL channel.".fromRepo("chat.conversation_expired")
	private val NOT_IN_PARTY_MESSAGE by "You are not in a party and were moved to the ALL channel.".fromRepo("chat.not_in_party")

	init {
		TickEvents.TICK.register(this::onTick)
		ScreenRenderEvents.afterRender<ChatScreen> { _, ctx, _, _, _ -> onRenderChatScreen(ctx) }
		HudRenderCallback.EVENT.register { ctx, delta ->
			ErrorManager.catching("Failed to render chat display HUD") { onRenderHud(ctx, delta) }
		}
		ChatMessageEvents.CHAT.register(this::onChatMessage)
		SendMessageEvents.SEND_CHAT_MESSAGE.register { onSendChatMessage() }
	}

	private fun onTick(event: TickEvents.Tick) {
		if(event.client.currentScreen is ChatScreen) {
			ticksSinceChatOpen = 0
		} else {
			ticksSinceChatOpen++
		}
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

		CONVERSATION_OPENED_REGEX.onFullMatch(event.cleaned) {
			channel = ActiveChatChannel(ChatChannel.DM, groups["username"]!!.value, Timestamp.now() + 5.minutes)
			return
		}

		if(event.cleaned == CONVERSATION_EXPIRED_MESSAGE || event.cleaned == NOT_IN_PARTY_MESSAGE) {
			channel = ActiveChatChannel(ChatChannel.ALL)
		}
	}

	private fun onRenderHud(ctx: DrawContext, delta: RenderTickCounter) {
		if(!enabled) return
		if(!HypixelUtils.onHypixel) return
		if(MCUtils.client.currentScreen is ChatScreen) return

		val displayTicks = DISPLAY_FOR_TICKS - ticksSinceChatOpen
		val alpha: Int = when {
			displayTicks <= 0 -> return
			displayTicks > FADE_OUT_AT -> 255
			else -> {
				val partialTick = delta./*? if >=1.21.5 {*//*getTickProgress*//*?} else {*/getTickDelta/*?}*/(true)
				// avoid the display flickering at full visibility for a few frames by just hiding it slightly earlier.
				// this is definitely an insane solution and could undoubtedly be done better, but it works,
				// so fuck it we ball.
				if(ticksSinceChatOpen == 39 && partialTick >= 0.6) ticksSinceChatOpen++
				RenderUtils.lerpAlpha(partialTick, displayTicks, FADE_OUT_AT)
			}
		}

		draw(ctx, alpha)
	}

	private fun onRenderChatScreen(ctx: DrawContext) {
		if(!enabled) return
		if(!HypixelUtils.onHypixel) return

		draw(ctx)
	}

	private fun draw(ctx: DrawContext, alpha: Int = 255) {
		ctx.drawText(MCUtils.textRenderer, buildText(), 4, ctx.scaledWindowHeight - 26, NobaColor.WHITE.withAlpha(alpha), true)
	}

	private fun buildText() = buildText {
		append(tr("nobaaddons.chat.channel.current", "Chat: ${channel.name}").green())
		val extra = channel.extraInfo
		if(extra != null) {
			append(" ")
			append(extra)
		}
	}
}