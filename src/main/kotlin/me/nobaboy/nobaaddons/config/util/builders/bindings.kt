package me.nobaboy.nobaaddons.config.util.builders

import dev.isxander.yacl3.api.Binding
import me.nobaboy.nobaaddons.config.AbstractNobaConfig
import me.nobaboy.nobaaddons.config.NobaConfig
import me.nobaboy.nobaaddons.config.util.BiMapper
import kotlin.reflect.KMutableProperty

@PublishedApi
internal fun <T> binding(getter: (AbstractNobaConfig) -> T, setter: (AbstractNobaConfig, T) -> Unit): Binding<T> =
	Binding.generic(getter(OptionBuilder.defaults), { getter(NobaConfig) }, { setter(NobaConfig, it) })

@PublishedApi
internal fun <T> (AbstractNobaConfig.() -> KMutableProperty<T>).binding(): Binding<T> = binding(
	getter = { this(it).getter.call() },
	setter = { config, value -> this(config).setter.call(value) }
)

@PublishedApi
internal fun <A, B> (AbstractNobaConfig.() -> KMutableProperty<A>).binding(biMapper: BiMapper<A, B>): Binding<B> = binding(
	getter = { biMapper.to(this(it).getter.call()) },
	setter = { config, value -> this(config).setter.call(biMapper.from(value)) },
)
