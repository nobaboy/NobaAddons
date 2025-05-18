package me.nobaboy.nobaaddons.utils.chat

import me.nobaboy.nobaaddons.utils.MCUtils
import me.nobaboy.nobaaddons.utils.items.ItemUtils.lore
import net.minecraft.client.gui.screen.Screen
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.Text
import net.minecraft.text.Texts
import java.net.URI

object TextEventUtils {
	fun createHoverText(text: Text): HoverEvent {
		//? if >=1.21.5 {
		/*return HoverEvent.ShowText(text)
		*///?} else {
		return HoverEvent(HoverEvent.Action.SHOW_TEXT, text)
		//?}
	}

	fun createCommand(command: String, suggest: Boolean = false): ClickEvent {
		//? if >=1.21.5 {
		/*return if(suggest) ClickEvent.SuggestCommand(command) else ClickEvent.RunCommand(command)
		*///?} else {
		return ClickEvent(if(suggest) ClickEvent.Action.SUGGEST_COMMAND else ClickEvent.Action.RUN_COMMAND, command)
		//?}
	}

	fun createOpenUrl(url: String): ClickEvent {
		//? if >=1.21.5 {
		/*return ClickEvent.OpenUrl(URI.create(url))
		*///?} else {
		return ClickEvent(ClickEvent.Action.OPEN_URL, url)
		//?}
	}

	fun createCopyText(text: String): ClickEvent {
		//? if >=1.21.5 {
		/*return ClickEvent.CopyToClipboard(text)
		*///?} else {
		return ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, text)
		//?}
	}

	fun ClickEvent.commandOrNull(): String? {
		//? if >=1.21.5 {
		/*return when(this) {
			is ClickEvent.RunCommand, is ClickEvent.SuggestCommand -> value
			else -> null
		}
		*///?} else {
		return value
		//?}
	}

	fun ClickEvent.command(): String = commandOrNull() ?: error("Cannot find command on click event")

	val HoverEvent.value: Text? get() {
		// TODO show entity is not included because thats slightly more work for a hover element that i think is used
		//      even less than show item
		//? if >=1.21.5 {
		/*return when(this) {
			is HoverEvent.ShowText -> value
			// TODO is this correct? i don't think hypixel ever uses this
			is HoverEvent.ShowItem -> Texts.join(Screen.getTooltipFromItem(MCUtils.client, item), Text.literal("\n"))
			else -> null
		}
		*///?} else {
		return when(action) {
			HoverEvent.Action.SHOW_TEXT -> getValue(HoverEvent.Action.SHOW_TEXT)
			HoverEvent.Action.SHOW_ITEM -> Texts.join(
				Screen.getTooltipFromItem(MCUtils.client, getValue(HoverEvent.Action.SHOW_ITEM)?.asStack() ?: return null),
				Text.literal("\n"),
			)
			else -> null
		}
		//?}
	}

	val ClickEvent.value: String? get() {
		//? if >=1.21.5 {
		/*return when(this) {
			is ClickEvent.RunCommand -> command
			is ClickEvent.SuggestCommand -> command
			is ClickEvent.OpenFile -> path
			is ClickEvent.OpenUrl -> uri.toString()
			is ClickEvent.CopyToClipboard -> value
			is ClickEvent.ChangePage -> page.toString()
			// TODO 1.21.6 packet click event
			else -> error("Unknown click event type $this")
		}
		*///?} else {
		return value
		//?}
	}
}