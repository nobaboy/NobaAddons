package me.nobaboy.nobaaddons.config.util.builders

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.Option
import me.nobaboy.nobaaddons.config.AbstractNobaConfig
import me.nobaboy.nobaaddons.config.util.BiMapper
import me.nobaboy.nobaaddons.features.AbstractFeature
import me.nobaboy.nobaaddons.features.AbstractFeature.Companion.getKillSwitch
import kotlin.reflect.KMutableProperty

abstract class OptionCollectionBuilder {
	open val feature: AbstractFeature? = null

	abstract fun <T> add(option: Option<T>)

	@PublishedApi
	internal inline fun <T> OptionBuilder<T>.build(feature: AbstractFeature?, builder: OptionBuilder<T>.() -> Unit): Option<T> {
		feature?.let { killSwitch = it.getKillSwitch() }
		builder(this)
		return build()
	}

	/**
	 * Add a new [Option] to the current collection
	 */
	inline fun <T> add(
		noinline property: AbstractNobaConfig.() -> KMutableProperty<T>,
		feature: AbstractFeature? = this.feature,
		crossinline builder: OptionBuilder<T>.() -> Unit
	): Option<T> =
		OptionBuilder<T>(property.binding()).build(feature, builder).also(::add)

	/**
	 * Add a new [Option] to the current collection, mapping its value from the stored type [A] to type [B] usable by YACL
	 */
	inline fun <A, B> add(
		noinline property: AbstractNobaConfig.() -> KMutableProperty<A>,
		mapping: BiMapper<A, B>,
		feature: AbstractFeature? = this.feature,
		crossinline builder: OptionBuilder<B>.() -> Unit
	): Option<B> =
		OptionBuilder<B>(property.binding(mapping)).build(feature, builder).also(::add)

	/**
	 * Add a new [Option] to the current collection with a custom [Binding]
	 */
	inline fun <T> add(
		binding: Binding<T>,
		feature: AbstractFeature? = this.feature,
		crossinline builder: OptionBuilder<T>.() -> Unit
	): Option<T> =
		OptionBuilder<T>(binding).build(feature, builder).also(::add)
}