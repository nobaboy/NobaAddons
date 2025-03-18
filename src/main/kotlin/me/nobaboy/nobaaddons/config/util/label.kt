package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.OptionAddable
import net.minecraft.text.Text

class LabelBuilder {
	internal val text: MutableList<Text> = mutableListOf()

	operator fun Text.unaryPlus() {
		text.add(this)
	}

	fun newLine() = +Text.empty()
}

/**
 * Create and add a new [LabelOption] to the current [OptionAddable]
 *
 * ## Example
 * ```kt
 * label {
 *     +Text.literal("Shello!")
 *     newLine()
 *     +Text.literal("Now with an empty line!")
 * }
 * ```
 */
fun OptionAddable.label(builder: LabelBuilder.() -> Unit): LabelOption = LabelOption.createBuilder().apply {
	lines(LabelBuilder().apply(builder).text)
}.build().also(::option)
