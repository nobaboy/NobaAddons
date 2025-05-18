package me.nobaboy.nobaaddons.features.chat

import me.nobaboy.nobaaddons.events.impl.chat.ChatMessageEvents
import me.nobaboy.nobaaddons.utils.TextUtils.aqua
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import me.nobaboy.nobaaddons.utils.TextUtils.hoverText
import me.nobaboy.nobaaddons.utils.TextUtils.toText
import me.nobaboy.nobaaddons.utils.TextUtils.yellow
import me.nobaboy.nobaaddons.utils.chat.TextEventUtils.value
import me.nobaboy.nobaaddons.utils.tr
import net.minecraft.text.ClickEvent
import net.minecraft.text.Text

object SafeChatClicks {
	init {
		ChatMessageEvents.LATE_MODIFY.register(this::modify)
	}

	private fun modify(event: ChatMessageEvents.Modify) {
		if(!event.message.hasAnyClickEvents()) return
		event.message = event.message.modify()
	}

	private fun Text.hasAnyClickEvents(): Boolean {
		return style.clickEvent != null || siblings.any { it.hasAnyClickEvents() }
	}

	// this feels like some of the most heinous shit ive ever written
	private fun Text.modify(): Text {
		val new = copyContentOnly()
		var style = style
		val clickEvent = style.clickEvent
		if(clickEvent != null) {
			val hover = style.hoverEvent?.value
			style.hoverText(buildText {
				hover?.let(::append)
				append("\n\n")
				append(when(clickEvent.action) {
					ClickEvent.Action.RUN_COMMAND, ClickEvent.Action.SUGGEST_COMMAND -> tr(
						"nobaaddons.chat.safeClicks.runsCommand", "Runs ${clickEvent.value.toText().aqua()} when clicked"
					)
					// we're not including the text here because it could be fairly large
					ClickEvent.Action.COPY_TO_CLIPBOARD -> tr(
						"nobaaddons.chat.safeClicks.copyText", "Copies text to clipboard when clicked"
					)
					ClickEvent.Action.OPEN_URL, ClickEvent.Action.OPEN_FILE -> tr(
						"nobaaddons.chat.safeClicks.openUrl", "Opens ${clickEvent.value.toText().aqua()} when clicked"
					)
					ClickEvent.Action.CHANGE_PAGE -> tr(
						"nobaaddons.chat.safeClicks.changePage", "Switches to page ${clickEvent.value.toText().aqua()} when clicked"
					)
				}.yellow())
			})
		}
		new.style = style
		siblings.forEach { new.append(it.modify()) }
		return new
	}
}