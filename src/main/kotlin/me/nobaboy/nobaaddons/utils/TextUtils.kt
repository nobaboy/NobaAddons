package me.nobaboy.nobaaddons.utils

import net.minecraft.text.ClickEvent
import net.minecraft.text.HoverEvent
import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting
import java.util.function.UnaryOperator

object TextUtils {
	inline fun buildText(crossinline builder: MutableText.() -> Unit): Text = Text.empty().apply(builder)
	inline fun buildLiteral(text: String, crossinline builder: MutableText.() -> Unit): Text = Text.literal(text).apply(builder)

	inline fun MutableText.literal(string: String, builder: MutableText.() -> Unit = {}): MutableText =
		append(Text.literal(string).apply(builder))

	inline fun MutableText.translatable(key: String, vararg args: Any, fallback: String? = null, builder: MutableText.() -> Unit = {}): MutableText {
		val text = when(fallback) {
			null -> Text.translatable(key, *args)
			else -> Text.translatableWithFallback(key, fallback, *args)
		}
		text.apply(builder)
		return append(text)
	}

	fun MutableText.appendLine(line: Text): MutableText {
		append(line)
		append("\n")
		return this
	}

	fun MutableText.appendLine(line: String): MutableText = appendLine(Text.literal(line))

	fun MutableText.withColor(formatting: Formatting): MutableText = this.formatted(formatting)

	fun MutableText.black() = withColor(Formatting.BLACK)
	fun MutableText.darkBlue() = withColor(Formatting.DARK_BLUE)
	fun MutableText.darkGreen() = withColor(Formatting.DARK_GREEN)
	fun MutableText.darkAqua() = withColor(Formatting.DARK_AQUA)
	fun MutableText.darkRed() = withColor(Formatting.DARK_RED)
	fun MutableText.darkPurple() = withColor(Formatting.DARK_PURPLE)
	fun MutableText.gold() = withColor(Formatting.GOLD)
	fun MutableText.gray() = withColor(Formatting.GRAY)
	fun MutableText.darkGray() = withColor(Formatting.DARK_GRAY)
	fun MutableText.blue() = withColor(Formatting.BLUE)
	fun MutableText.green() = withColor(Formatting.GREEN)
	fun MutableText.aqua() = withColor(Formatting.AQUA)
	fun MutableText.red() = withColor(Formatting.RED)
	fun MutableText.lightPurple() = withColor(Formatting.LIGHT_PURPLE)
	fun MutableText.yellow() = withColor(Formatting.YELLOW)
	fun MutableText.white() = withColor(Formatting.WHITE)

	fun MutableText.bold(bold: Boolean = true): MutableText = this.styled { it.withBold(bold) }
	fun MutableText.italic(italic: Boolean = true): MutableText = this.styled { it.withItalic(italic) }
	fun MutableText.underline(underline: Boolean = true): MutableText = this.styled { it.withUnderline(underline) }
	fun MutableText.strikethrough(strikethrough: Boolean = true): MutableText = this.styled { it.withStrikethrough(strikethrough) }
	fun MutableText.obfuscated(obfuscated: Boolean = true): MutableText = this.styled { it.withObfuscated(obfuscated) }

	fun MutableText.runCommand(command: String = this.string): MutableText {
		require(command.startsWith("/"))
		return styled { it.withClickEvent(ClickEvent(ClickEvent.Action.RUN_COMMAND, command)) }
	}
	fun MutableText.openUrl(url: String): MutableText = styled { it.withClickEvent(ClickEvent(ClickEvent.Action.OPEN_URL, url)) }

	fun MutableText.hoverText(text: String): MutableText = hoverText(text.toText())
	fun MutableText.hoverText(text: Text): MutableText = styled { it.withHoverEvent(HoverEvent(HoverEvent.Action.SHOW_TEXT, text)) }

	fun String.toText(): MutableText = Text.literal(this)
	fun String.formatted(vararg formatting: Formatting): MutableText = toText().formatted(*formatting)
	fun String.styled(styleUpdater: UnaryOperator<Style>): MutableText = toText().styled(styleUpdater)
}

fun tr(key: String, default: String): MutableText = error("Compiler plugin did not run")
fun trResolved(key: String, vararg args: Any): MutableText = Text.stringifiedTranslatable(key, *args)
