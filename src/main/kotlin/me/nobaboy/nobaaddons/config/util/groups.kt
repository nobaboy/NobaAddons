package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.ConfigCategory
import me.nobaboy.nobaaddons.config.util.builders.CategoryBuilder
import net.minecraft.text.Text

// TODO move this into builders
/**
 * Convenience method creating a [ConfigCategory.Builder], providing it to [builder], and returns the built [ConfigCategory]
 */
inline fun category(name: Text, builder: CategoryBuilder.() -> Unit): ConfigCategory = CategoryBuilder().apply {
	yacl.name(name)
	builder(this)
}.build()
