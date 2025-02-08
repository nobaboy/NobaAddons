package me.nobaboy.nobaaddons.config.core

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import kotlinx.serialization.json.JsonObject
import me.nobaboy.nobaaddons.NobaAddons
import me.nobaboy.nobaaddons.config.LegacyConfigMigration
import me.nobaboy.nobaaddons.config.LegacyConfigMigration.Companion.applyAll
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.option.AbstractVersionedConfigOptionGroup
import me.nobaboy.nobaaddons.config.option.AbstractConfigOptionLoader
import me.nobaboy.nobaaddons.config.option.ConfigOption
import me.nobaboy.nobaaddons.utils.FileUtils.readGson
import net.minecraft.text.Text
import kotlin.io.path.exists

sealed class AbstractCoreConfig(id: String) : AbstractVersionedConfigOptionGroup(id) {
	override fun buildConfig(category: ConfigCategory.Builder) {
		deepBuildYaclOptions()
		options.values.mapNotNull { (it as? ConfigOption<*>)?.yaclOption }.forEach(category::option)
	}

	/**
	 * Overload wrapping this config's options in a group
	 */
	protected fun buildConfig(category: ConfigCategory.Builder, group: Text, description: Text? = null) {
		deepBuildYaclOptions()
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

		override fun createNewConfig() {
			if(NobaConfig.CONFIG_PATH.exists()) {
				loadFromJson(
					LegacyConfigMigration.CORE.applyAll(
						NobaConfig.CONFIG_PATH.toFile().readGson(com.google.gson.JsonObject::class.java),
						JsonObject(mapOf())
					)
				)
			}
			save()
		}
	}
}