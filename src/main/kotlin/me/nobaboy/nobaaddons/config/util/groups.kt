package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import net.minecraft.text.Text

/**
 * Convenience method creating a [ConfigCategory.Builder], providing it to [builder], and returns the built [ConfigCategory]
 */
inline fun category(name: Text, builder: ConfigCategory.Builder.() -> Unit): ConfigCategory = ConfigCategory.createBuilder().apply {
	name(name)
	builder(this)
}.build()

/**
 * Convenience method creating a [OptionGroup.Builder], providing it to [builder], and then adding it to the
 * current [ConfigCategory.Builder]
 */
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
