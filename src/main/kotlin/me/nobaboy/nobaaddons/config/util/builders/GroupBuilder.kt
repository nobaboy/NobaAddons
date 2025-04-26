package me.nobaboy.nobaaddons.config.util.builders

import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionGroup
import me.nobaboy.nobaaddons.features.AbstractFeature

class GroupBuilder(
	val yacl: OptionGroup.Builder = OptionGroup.createBuilder(),
	override val feature: AbstractFeature? = null,
) : OptionCollectionBuilder() {
	override fun <T> add(option: Option<T>) {
		yacl.option(option)
	}

	fun build(): OptionGroup = yacl.build()
}
