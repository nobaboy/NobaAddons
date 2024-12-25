package me.nobaboy.nobaaddons.utils

import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.runCommand
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.chat.ChatUtils
import net.minecraft.util.Formatting
import net.minecraft.util.math.MathHelper
import kotlin.time.Duration.Companion.minutes

object ErrorManager {
	private val errors = TimedCache<String, String>(10.minutes) // id -> stack trace
	private val erroredLines = TimedSet<Pair<String, Int>>(10.minutes) // file to line

	// This does send a (partially) untranslated message, but this is something I'm personally fine with,
	// as it's more worthwhile for this to be untranslated to help with support requests related to it.
	fun logError(message: String, error: Throwable, ignorePreviousErrors: Boolean = false) {
		val stack = error.stackTrace
		if(!ignorePreviousErrors) {
			val cause: Pair<String, Int> = stack.firstOrNull()?.let { it.fileName to it.lineNumber } ?: (message to 0)
			if(cause in erroredLines) return
			erroredLines.add(cause)
		}

		NobaAddons.LOGGER.error(message, error)

		val id = MathHelper.randomUuid().toString()
		val trace = error.stackTraceToString()
		errors[id] = trace

		ChatUtils.addMessage(buildText {
			append(tr("nobaaddons.error", "NobaAddons ${NobaAddons.VERSION} encountered an error: $message"))
			runCommand("/nobaaddons internal copyerror $id")
			hoverText(tr("nobaaddons.error.clickToCopy", "Click to copy the error to the clipboard").yellow())
		}, color = Formatting.RED)
	}

	fun copyError(id: String) {
		val error = errors[id] ?: return
		MCUtils.copyToClipboard(error)
		ChatUtils.addMessage(tr("nobaaddons.error.copiedToClipboard", "Copied full error to clipboard, please report it in the Discord"))
	}
}
