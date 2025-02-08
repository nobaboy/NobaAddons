package me.nobaboy.nobaaddons.config.core

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.option.AbstractVersionedConfigOptionGroup
import me.nobaboy.nobaaddons.config.option.AbstractConfigOptionLoader
import me.nobaboy.nobaaddons.config.option.ConfigOption
import net.minecraft.text.Text

sealed class AbstractCoreConfig(id: String) : AbstractVersionedConfigOptionGroup(id) {
	override fun buildConfig(category: ConfigCategory.Builder) {
		options.values.mapNotNull { (it as? ConfigOption<*>)?.yaclOption }.forEach(category::option)
	}

	/**
	 * Overload wrapping this config's options in a group
	 */
	protected fun buildConfig(category: ConfigCategory.Builder, group: Text, description: Text? = null) {
		category.group(OptionGroup.createBuilder().apply {
			name(group)
			description?.let { description(OptionDescription.of(it)) }
			collapsed(true)
			options.values.mapNotNull { (it as? ConfigOption<*>)?.yaclOption }.forEach(::option)
		}.build())
	}

	companion object : AbstractConfigOptionLoader<AbstractCoreConfig>(NobaAddons.CONFIG_DIR.resolve("core.json").toFile()) {
		override val configs: Array<AbstractCoreConfig> = arrayOf(
			CoreAPIConfig,
		)
	}
}