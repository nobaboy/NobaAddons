package me.nobaboy.nobaaddons.config.util

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionAddable
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.gui.YACLScreen
import me.nobaboy.nobaaddons.config.NobaConfig
import net.minecraft.text.Text
import kotlin.reflect.KMutableProperty

class OptionBuilder<T>(
	val getter: (NobaConfig) -> T,
	val setter: (NobaConfig, T) -> Unit,
) {
	constructor(property: NobaConfig.() -> KMutableProperty<T>) : this(
		getter = { property(it).getter.call() },
		setter = { config, value -> property(config).setter.call(value) },
	)

	lateinit var name: Text
	var description: OptionDescription? = null

	lateinit var controller: (Option<T>) -> ControllerBuilder<T>

	var requirement: OptionRequirement? = null

	fun require(builder: OptionRequirementBuilder.() -> OptionRequirement) {
		this.requirement = builder(OptionRequirementBuilder)
	}

	internal fun build(): Option<T> {
		val option = Option.createBuilder<T>().apply {
			name(name)
			description?.let(::description)
			binding(Binding.generic(getter(NobaConfig.DEFAULTS), { getter(NobaConfig.INSTANCE) }, { setter(NobaConfig.INSTANCE, it) }))
			controller(controller)
		}.build()

		requirement?.let(option::require)

		return option
	}
}

var OptionBuilder<*>.descriptionText: Text?
	get() = description?.text()
	set(value) { description = OptionDescription.of(value) }

fun <A, B> OptionAddable.add(
	property: NobaConfig.() -> KMutableProperty<A>,
	mapping: ReversibleMapping<A, B>,
	builder: OptionBuilder<B>.() -> Unit,
): Option<B> = OptionBuilder<B>(
	getter = { mapping.to(property(it).getter.call()) },
	setter = { config, value -> property(config).setter.call(mapping.from(value)) }
).apply(builder).build()

fun <T> OptionAddable.add(
	property: NobaConfig.() -> KMutableProperty<T>,
	builder: OptionBuilder<T>.() -> Unit,
): Option<T> =
	OptionBuilder<T>(property).apply(builder).build()

fun OptionAddable.button(
	name: Text,
	description: Text? = null,
	text: Text? = null,
	action: (YACLScreen) -> Unit,
): ButtonOption = ButtonOption.createBuilder().apply {
	name(name)
	description?.let { description(OptionDescription.of(it)) }
	text?.let(::text)
	action { screen, _ -> action(screen) }
}.build().also(::option)
