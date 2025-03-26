package me.nobaboy.nobaaddons.utils.chat

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.events.impl.client.TickEvents
import me.nobaboy.nobaaddons.utils.CooldownManager
import me.nobaboy.nobaaddons.utils.ErrorManager
import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.runCommand
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.Timestamp
import me.nobaboy.nobaaddons.utils.annotations.UntranslatedMessage
import net.fabricmc.loader.api.FabricLoader
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
		TickEvents.cooldown { _, cooldown -> processCommandQueue(cooldown) }
		TickEvents.everySecond { removeExpiredClickActions() }
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
	fun addMessage(message: Text, prefix: Boolean = true, color: Formatting? = Formatting.WHITE) {
		if(!MCUtils.client.isOnThread) {
			if(FabricLoader.getInstance().isDevelopmentEnvironment) {
				// log when this happens in a dev env to make it easier to spot when this might cause messages
				// to be out of order
				NobaAddons.LOGGER.warn("[Debug] Moving ChatUtils.addMessage() call to the client thread")
			}
			MCUtils.client.execute { addMessage(message, prefix, color) }
			return
		}

		val message = if(prefix || color != null) {
			buildText {
				if(prefix) append(NobaAddons.PREFIX)
				color?.let { formatted(it) }
				append(message)
			}
		} else message

		MCUtils.client.inGameHud.chatHud.addMessage(message)
	}

	/**
	 * Adds a message with [addMessage], and captures the added message in a [Message] object to allow for removal
	 * from the chat HUD later.
	 */
	fun addAndCaptureMessage(message: Text, prefix: Boolean = true, color: Formatting? = Formatting.WHITE): Message {
		check(MCUtils.client.isOnThread) { "Capturing a message can only be done on the client thread" }

		SHOULD_CAPTURE.set(true)
		addMessage(message, prefix, color)
		SHOULD_CAPTURE.set(false)

		val message = CAPTURED_MESSAGE.get() ?: error("Mixin did not capture message")
		CAPTURED_MESSAGE.remove()
		return message
	}

	/**
	 * Add an untranslated chat message to the player's chat
	 */
	@UntranslatedMessage
	fun addMessage(message: String, prefix: Boolean = true, color: Formatting? = Formatting.WHITE) =
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
	) = addMessage(buildText {
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
	) = addMessageWithClickAction(text.toText(), prefix, color, ttl, builder, clickAction)

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