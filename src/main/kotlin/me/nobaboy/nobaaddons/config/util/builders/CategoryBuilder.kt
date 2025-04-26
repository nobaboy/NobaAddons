package me.nobaboy.nobaaddons.config.util.builders

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.features.AbstractFeature
import net.minecraft.text.Text

class CategoryBuilder(val yacl: ConfigCategory.Builder = ConfigCategory.createBuilder()) : OptionCollectionBuilder() {
	override fun <T> add(option: Option<T>) {
		yacl.option(option)
	}

	/**
	 * Convenience method creating a [GroupBuilder], providing it to [builder], and then adds it to the
	 * current [CategoryBuilder]
	 */
	inline fun group(
		name: Text,
		description: Text? = null,
		collapsed: Boolean = true,
		crossinline builder: GroupBuilder.() -> Unit
	): OptionGroup = GroupBuilder().apply {
		yacl.name(name)
		description?.let { OptionDescription.of(it) }?.let(yacl::description)
		yacl.collapsed(collapsed)
		builder(this)
	}.build().also(yacl::group)

	/**
	 * Convenience method creating a [GroupBuilder] for an [AbstractFeature], providing it to [builder], and then
	 * adds it to the current [CategoryBuilder]
	 */
	inline fun group(
		feature: AbstractFeature,
		collapsed: Boolean = true,
		crossinline builder: GroupBuilder.() -> Unit
	): OptionGroup = GroupBuilder(feature = feature).apply {
		yacl.name(feature.name)
		feature.description?.let { OptionDescription.of(it) }?.let(yacl::description)
		yacl.collapsed(collapsed)
		builder(this)
	}.build().also(yacl::group)

	fun build(): ConfigCategory = yacl.build()
}