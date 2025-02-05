package me.nobaboy.nobaaddons.config.utils

import dev.isxander.yacl3.api.Binding
import dev.isxander.yacl3.api.ButtonOption
import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.LabelOption
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionAddable
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.gui.YACLScreen
import net.minecraft.text.Text
import kotlin.reflect.KMutableProperty

inline fun buildCategory(name: Text, crossinline builder: ConfigCategory.Builder.() -> Unit): ConfigCategory =
	ConfigCategory.createBuilder().name(name).apply(builder).build()

inline fun buildGroup(name: Text, description: Text? = null, collapsed: Boolean = true, builder: OptionGroup.Builder.() -> Unit): OptionGroup =
	OptionGroup.createBuilder().apply {
		name(name)
		description?.let { description(OptionDescription.of(it)) }
		collapsed(collapsed)
		apply(builder)
	}.build()

inline fun ConfigCategory.Builder.group(
	name: Text,
	description: Text? = null,
	collapsed: Boolean = true,
	crossinline builder: OptionGroup.Builder.() -> Unit
) {
	group(buildGroup(name, description, collapsed, builder))
}

class OptionBuilder<T> {
	lateinit var name: Text

	var description: Text? = null
	var descriptionBuilder: (() -> Text)? = null

	lateinit var controller: (Option<T>) -> ControllerBuilder<T>
	lateinit var binding: Binding<T>

	fun property(default: T, property: KMutableProperty<T>) {
		binding = Binding.generic(default, property.getter::call, property.setter::call)
	}

	inline fun <I> property(defaults: I, config: I, property: I.() -> KMutableProperty<T>) {
		property(property(defaults).getter.call(), property(config))
	}

	var condition: OptionCondition? = null

	inline fun condition(builder: ConditionBuilder.() -> OptionCondition) {
		condition = builder(ConditionBuilder)
	}

	fun build(): Option<T> {
		require(::name.isInitialized) { "Option name must be set" }
		require(::controller.isInitialized) { "Option controller must be set" }
		require(::binding.isInitialized) { "Option binding must be set" }

		val option = Option.createBuilder<T>().apply {
			name(name)
			description?.let { description(OptionDescription.of(it)) }
			descriptionBuilder?.let { description { OptionDescription.of(it()) } }
			binding(binding)
			controller(controller)
		}.build()

		condition?.let { option.requires(it) }

		return option
	}
}

inline fun <T> OptionAddable.create(builder: OptionBuilder<T>.() -> Unit): Option<T> =
	OptionBuilder<T>().apply(builder).build().also(::option)

inline fun OptionAddable.boolean(crossinline builder: OptionBuilder<Boolean>.() -> Unit): Option<Boolean> = create<Boolean> {
	boolean()
	builder(this)
}

inline fun <reified T : Enum<T>> OptionAddable.enum(
	only: Array<T>? = null,
	crossinline builder: OptionBuilder<T>.() -> Unit
): Option<T> = create<T> {
	if(only == null) enumController() else enumController(only)
	builder(this)
}

fun <G : OptionAddable> G.label(vararg lines: Text): G = apply {
	require(lines.isNotEmpty()) { "Cannot create an empty label controller" }
	option(LabelOption.createBuilder().apply {
		if(lines.size == 1) line(lines[0]) else lines(lines.toList())
	}.build())
}

fun <G : OptionAddable> G.button(name: Text, description: Text? = null, text: Text? = null, action: (YACLScreen) -> Unit): ButtonOption {
	val button = ButtonOption.createBuilder()
		.name(name)
		.also { if(description != null) it.description(OptionDescription.of(description)) }
		.also { if(text != null) it.text(text) }
		.action { screen, _ -> action(screen) }
		.build()
	option(button)
	return button
}