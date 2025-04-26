package me.nobaboy.nobaaddons.config.util.builders

import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.OptionAddable
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.gui.YACLScreen
import me.nobaboy.nobaaddons.utils.TextUtils.buildText
import net.minecraft.text.MutableText
import net.minecraft.text.Text

/**
 * Create and add a new [LabelOption] to the current [OptionAddable] using [buildText]
 */
fun OptionCollectionBuilder.label(builder: MutableText.() -> Unit): LabelOption = LabelOption.create(buildText(builder)).also(::add)

/**
 * Create and add a new [LabelOption] with one or more [Text] elements
 */
fun OptionCollectionBuilder.label(text: Text, vararg extra: Text): LabelOption = LabelOption.createBuilder().apply {
	line(text)
	extra.toList().takeIf(List<*>::isNotEmpty)?.let(::lines)
}.build().also(::add)

/**
 * Create a new button option
 */
fun OptionCollectionBuilder.button(
	name: Text,
	description: Text? = null,
	text: Text? = null,
	action: (YACLScreen) -> Unit,
): ButtonOption = ButtonOption.createBuilder().apply {
	name(name)
	description?.let { description(OptionDescription.of(it)) }
	text?.let(::text)
	action { screen, _ -> action(screen) }
}.build().also(::add)
