package me.nobaboy.nobaaddons.utils.chat

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvent
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.runCommand
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import net.minecraft.text.MutableText
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper
import java.util.LinkedList
import java.util.Queue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object ChatUtils {
	private val commandQueue: Queue<String> = LinkedList()
	private val clickActions: MutableMap<String, ClickAction> = mutableMapOf()

	private val SHOULD_CAPTURE: ThreadLocal<Boolean> = ThreadLocal.withInitial { false }
	private val CAPTURED_MESSAGE: ThreadLocal<Message?> = ThreadLocal()

	init {
		TickEvent.cooldown { _, cooldown -> processCommandQueue(cooldown) }
		TickEvent.everySecond { removeExpiredClickActions() }
		ChatMessageEvents.ADDED.register(this::onChatAdded)
	}

	private fun removeExpiredClickActions() {
		// .toMap() is to create a copy in order to avoid causing a ConcurrentModificationException
		clickActions.toMap().forEach {
			if(it.value.createdAt.elapsedSince() > it.value.ttl) clickActions.remove(it.key)
		}
	}

	private fun processCommandQueue(cooldownManager: CooldownManager) {
		if(MCUtils.player == null) {
			commandQueue.clear()
			return
		}
		(MCUtils.networkHandler ?: return).sendCommand(commandQueue.poll() ?: return)
		cooldownManager.startCooldown(1.seconds)
	}

	private fun onChatAdded(event: ChatMessageEvents.Added) {
		if(SHOULD_CAPTURE.get()) {
			CAPTURED_MESSAGE.set(event.message)
		}
	}

	/**
	 * Add a command to the queue to send to the server
	 */
	fun queueCommand(message: String) {
		commandQueue.add(message)
	}

	/**
	 * Sends a chat message to the server as the player
	 */
	fun sendChatAsPlayer(message: String) {
		MCUtils.networkHandler?.sendChatMessage(message)
	}

	/**
	 * Add a chat message to the player's chat
	 */
	fun addMessage(message: Text, prefix: Boolean = true, color: Formatting? = Formatting.WHITE): Message {
		val message = if(prefix || color != null) {
			buildText {
				if(prefix) append(NobaAddons.PREFIX)
				color?.let { formatted(it) }
				append(message)
			}
		} else message

		SHOULD_CAPTURE.set(true)
		MCUtils.client.inGameHud.chatHud.addMessage(message)
		SHOULD_CAPTURE.set(false)

		val captured = CAPTURED_MESSAGE.get() ?: error("Mixin did not capture message")
		CAPTURED_MESSAGE.remove()
		return captured
	}

	/**
	 * Add an untranslated chat message to the player's chat
	 */
	@UntranslatedMessage
	fun addMessage(message: String, prefix: Boolean = true, color: Formatting? = Formatting.WHITE): Message =
		addMessage(Text.literal(message), prefix, color)

	/**
	 * Sends a chat message with a clickable action
	 */
	fun addMessageWithClickAction(
		text: Text,
		prefix: Boolean = true,
		color: Formatting? = Formatting.WHITE,
		ttl: Duration = 1.minutes,
		builder: MutableText.() -> Unit = {},
		clickAction: () -> Unit,
	): Message = addMessage(buildText {
		if(prefix) append(NobaAddons.PREFIX)
		append(text)

		val uuid = MathHelper.randomUuid().toString()
		clickActions[uuid] = ClickAction(clickAction, ttl = ttl)
		runCommand("/nobaaddons internal action $uuid")

		color?.let { formatted(it) }
		builder(this)
	}, prefix = false, color = null)

	/**
	 * Sends an untranslated chat message with a clickable action
	 */
	@UntranslatedMessage
	fun addMessageWithClickAction(
		text: String,
		prefix: Boolean = true,
		color: Formatting? = Formatting.WHITE,
		ttl: Duration = 1.minutes,
		builder: MutableText.() -> Unit = {},
		clickAction: () -> Unit,
	): Message = addMessageWithClickAction(text.toText(), prefix, color, ttl, builder, clickAction)

	/**
	 * Internal method called to invoke a click action from [addMessageWithClickAction]
	 */
	fun processClickAction(action: String) {
		val action = clickActions.remove(action) ?: return
		try {
			action.callback()
		} catch(e: Throwable) {
			ErrorManager.logError("Failed to process click action", e, ignorePreviousErrors = true)
		}
	}

	@JvmRecord
	private data class ClickAction(val callback: () -> Unit, val createdAt: Timestamp = Timestamp.now(), val ttl: Duration = 1.minutes)
}