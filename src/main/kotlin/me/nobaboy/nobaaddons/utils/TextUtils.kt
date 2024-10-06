package me.nobaboy.nobaaddons.utils

import net.minecraft.text.MutableText
import net.minecraft.text.Style
import net.minecraft.text.Text
import net.minecraft.util.Formatting

object TextUtils {
	fun buildText(builder: MutableText.() -> Unit): MutableText = Text.empty().apply(builder)
	fun Formatting.bold(): Style = Style.EMPTY.withColor(this).withBold(true)
	fun String.toText(): MutableText = Text.literal(this)
}
