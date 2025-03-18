package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import net.minecraft.text.Text

inline fun category(name: Text, builder: ConfigCategory.Builder.() -> Unit): ConfigCategory = ConfigCategory.createBuilder().apply {
	name(name)
	builder(this)
}.build()

inline fun ConfigCategory.Builder.group(
	name: Text,
	description: Text? = null,
	collapsed: Boolean = true,
	crossinline builder: OptionGroup.Builder.() -> Unit
): OptionGroup = OptionGroup.createBuilder().apply {
	name(name)
	description?.let { description(OptionDescription.of(it)) }
	builder(this)
	collapsed(collapsed)
}.build().also(::group)
