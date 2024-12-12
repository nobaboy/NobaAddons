package me.nobaboy.nobaaddons.config

import dev.isxander.yacl3.api.ConfigCategory
import dev.isxander.yacl3.api.Controller
import dev.isxander.yacl3.api.Option
import dev.isxander.yacl3.api.OptionAddable
import dev.isxander.yacl3.api.OptionDescription
import dev.isxander.yacl3.api.OptionGroup
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder
import dev.isxander.yacl3.api.controller.ControllerBuilder
import dev.isxander.yacl3.api.controller.EnumControllerBuilder
import net.minecraft.text.Text
import kotlin.reflect.KMutableProperty

object NobaConfigUtils {
	fun createBooleanController(option: Option<Boolean>): BooleanControllerBuilder {
		return BooleanControllerBuilder.create(option).yesNoFormatter().coloured(true)
	}

	inline fun <reified E : Enum<E>> createCyclingController(option: Option<E>): EnumControllerBuilder<E> {
		return EnumControllerBuilder.create(option).enumClass(E::class.java)
	}

	fun <T> MutableList<T>.replaceWith(with: List<T>) {
		clear()
		addAll(with)
	}

	inline fun ConfigCategory.Builder.buildGroup(name: Text, description: Text? = null, collapsed: Boolean = true, crossinline builder: OptionGroup.Builder.() -> Unit): ConfigCategory.Builder {
		this.group(OptionGroup.createBuilder()
			.name(name)
			.also { if(description != null) it.description(OptionDescription.of(description)) }
			.also { builder(it) }
			.collapsed(collapsed)
			.build())
		return this
	}

	fun <G : OptionAddable, T : Any> G.add(
		name: Text,
		description: Text? = null,
		optionController: (Option<T>) -> ControllerBuilder<T>,
		default: T,
		property: KMutableProperty<T>,
	): G {
		this.option(Option.createBuilder<T>()
			.name(name)
			.also { if(description != null) it.description(OptionDescription.of(description)) }
			.binding(default, property.getter::call, property.setter::call)
			.controller(optionController)
			.build())
		return this
	}

	fun <T : OptionAddable> T.boolean(name: Text, description: Text? = null, default: Boolean, property: KMutableProperty<Boolean>): T {
		return this.add(name, description, ::createBooleanController, default, property)
	}
}